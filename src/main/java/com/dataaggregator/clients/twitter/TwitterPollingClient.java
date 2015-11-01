package com.dataaggregator.clients.twitter;

import com.dataaggregator.util.Constants;
import com.dataaggregator.clients.twitter.kafka.api.KafkaConsumerGroup;
import com.dataaggregator.clients.twitter.kafka.api.MQHosebirdMessageProcessor;
import com.dataaggregator.clients.twitter.kafka.api.impl.KafkaProducer;
import com.dataaggregator.util.PropertyReader;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.KafkaTwitter4jStatusClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by srividyak on 02/01/15.
 */
@Component
public class TwitterPollingClient {

    @Autowired
    private TweetListener tweetListener;

    private static PropertyReader reader = PropertyReader.getInstance();
    private static int MAX_TRACKING_TERMS_PER_CLIENT;
    private static final Logger LOG = Logger.getLogger(TwitterPollingClient.class);
    private static String consumerKey;
    private static String consumerSecret;
    private static String accessToken;
    private static String accessTokenSecret;
    private static int numProcessingThreads;
    private static String groupName;
    private static String topicName;

    private TwitterPollingClientInternal clientInternal;
    private Authentication auth;
    private Set<String> trackingTerms;

    public void initialize() {
        initProperties();
        auth = new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        trackingTerms = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>() {
            protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
                return size() > MAX_TRACKING_TERMS_PER_CLIENT;
            }
        });
    }

    private static void initProperties() {
        MAX_TRACKING_TERMS_PER_CLIENT = Integer.parseInt(reader.getProperty(Constants.TWITTER_FILTER_MAX_TRACK_KEYWORDS, "400"));
        consumerKey = reader.getProperty(Constants.TWITTER_CONSUMER_KEY);
        consumerSecret = reader.getProperty(Constants.TWITTER_CONSUMER_SECRET);
        accessToken = reader.getProperty(Constants.TWITTER_ACCESS_TOKEN);
        accessTokenSecret = reader.getProperty(Constants.TWITTER_ACCESS_TOKEN_SECRET);
        numProcessingThreads = Integer.parseInt(reader.getProperty(Constants.TWITTER_KAFKA_NUM_CONSUMER_THREADS, "5"));
        groupName = reader.getProperty(com.dataaggregator.clients.twitter.kafka.util.Constants.KAFKA_CONSUMER_RAW_TWEETS_GROUP_NAME, "tw_raw_tweets_consumer");
        topicName = reader.getProperty(com.dataaggregator.clients.twitter.kafka.util.Constants.KAFKA_RAW_MSG_TOPIC, "raw_tweets");
    }

    public void addTrackingTerms(List<String> terms) {
        synchronized (trackingTerms) {
            LOG.debug("in add tracking terms");
            for (String term : terms) {
                LOG.debug("term added: " + term);
                String firstTermBeforeAddition = trackingTerms.iterator().hasNext() ? trackingTerms.iterator().next() : null;
                trackingTerms.add(term);
                String firstTermAfterAddition = trackingTerms.iterator().hasNext() ? trackingTerms.iterator().next() : null;
                if (firstTermBeforeAddition != null && firstTermAfterAddition != null) {
                    if (!firstTermBeforeAddition.equals(firstTermAfterAddition)) {
                        LOG.error("Tracking term: " + firstTermBeforeAddition + " got eliminated from being tracked");
                    }
                }
            }
            initializeInternalClient();
        }
    }

    private void initializeInternalClient() {
        if (clientInternal == null) {
            clientInternal = new TwitterPollingClientInternal(Lists.newArrayList(trackingTerms.iterator()));
        } else {
            clientInternal.addTrackingTerms(Lists.newArrayList(trackingTerms.iterator()));
        }
    }

    public void removeTrackingTerm(String term) {
        synchronized (trackingTerms) {
            if (trackingTerms.remove(term)) {
                initializeInternalClient();
            }
        }
    }

    /**
     * Stops the existing clients completely and re-initializes clientList to empty list
     */
    public void stop() {
        LOG.debug("stopping twitter polling client");
        clientInternal.stop();
    }

    class TwitterPollingClientInternal {
        private List<String> trackingTerms;
        private StatusesFilterEndpoint endpoint;
        private Client connectionClient;
        private KafkaConsumerGroup consumerGroup;
        private KafkaTwitter4jStatusClient kafkaClient;

        public TwitterPollingClientInternal(List<String> trackingTerms) {
            this.trackingTerms = new ArrayList<String>();
            this.trackingTerms.addAll(trackingTerms);
            connect();
        }

        private void connect() {
            endpoint = new StatusesFilterEndpoint();
            endpoint.stallWarnings(false);
            endpoint.trackTerms(trackingTerms);
            connectionClient = new ClientBuilder()
                    .hosts(com.twitter.hbc.core.Constants.STREAM_HOST)
                    .endpoint(endpoint)
                    .authentication(auth)
                    .processor(new MQHosebirdMessageProcessor(KafkaProducer.getInstance()))
                    .build();
            ExecutorService service = Executors.newFixedThreadPool(numProcessingThreads);
            consumerGroup = new KafkaConsumerGroup(groupName, topicName, numProcessingThreads);
            kafkaClient = new KafkaTwitter4jStatusClient(connectionClient, Lists.newArrayList(tweetListener), service,
                    consumerGroup.getKafkaStreams());
            kafkaClient.connect();
            kafkaClient.process();
        }

        public void stop() {
            LOG.debug("stopping kafka client");
            kafkaClient.stop();
        }

        public void reconnect() {
            stop();
            connect();
        }

        public void addTrackingTerms(List<String> trackingTerms) {
            this.trackingTerms.addAll(trackingTerms);
            reconnect();
        }

    }
}
