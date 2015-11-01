package com.dataaggregator.clients.twitter;

import com.dataaggregator.util.Constants;
import com.dataaggregator.util.PropertyReader;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.SearchOperations;
import org.springframework.social.twitter.api.Trend;
import org.springframework.social.twitter.api.Trends;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by srividyak on 03/01/15.
 */
@Component
public class TwitterTrendsClient {

    @Autowired
    private TwitterPollingClient pollingClient;

    private Twitter twitter;

    private ScheduledExecutorService executorService;

    private static final PropertyReader reader = PropertyReader.getInstance();
    private static String consumerKey;
    private static String consumerSecret;
    private static String accessToken;
    private static String accessTokenSecret;
    private static String woeIdMap;
    private static Map<String, String> locationToWoeId;
    private static Boolean fetchTrendingTopics;
    private static int fetchInterval;
    private static boolean pollForTrendingTopics;
    private static Logger LOG = Logger.getLogger(TwitterTrendsClient.class);

    private static void initProperties() {
        consumerKey = reader.getProperty(Constants.TWITTER_CONSUMER_KEY);
        consumerSecret = reader.getProperty(Constants.TWITTER_CONSUMER_SECRET);
        accessToken = reader.getProperty(Constants.TWITTER_ACCESS_TOKEN);
        accessTokenSecret = reader.getProperty(Constants.TWITTER_ACCESS_TOKEN_SECRET);
        woeIdMap = reader.getProperty(Constants.TWITTER_WOEID_MAP);
        if (woeIdMap != null) {
            Gson gson = new Gson();
            locationToWoeId = gson.fromJson(woeIdMap, HashMap.class);
        }
        fetchTrendingTopics = Boolean.parseBoolean(reader.getProperty(Constants.TWITTER_FETCH_TRENDING_TOPICS, "true"));
        fetchInterval = Integer.parseInt(reader.getProperty(Constants.TWITTER_TRENDING_TOPICS_FETCH_INTERVAL_MINS, "60"));
        pollForTrendingTopics = Boolean.parseBoolean(reader.getProperty(Constants.TWITTER_POLLING_TRENDING_TOPICS, "true"));
    }

    private void fetchTrendingTopics(Twitter twitter) {
        SearchOperations searchOperations = twitter.searchOperations();
        List<String> trendingTopics = new ArrayList<String>();
        for (Map.Entry<String, String> entry : locationToWoeId.entrySet()) {
            Trends trends = searchOperations.getLocalTrends(Long.parseLong(entry.getValue()));
            List<Trend> trendList = trends.getTrends();
            for (Trend trend : trendList) {
                trendingTopics.add(trend.getName());
            }
        }
        if (pollForTrendingTopics) {
            pollingClient.addTrackingTerms(trendingTopics);
        }
    }

    public void initialize() throws TwitterClientException {
        initProperties();
        pollingClient.initialize();
        executorService = Executors.newScheduledThreadPool(1);
        if (fetchTrendingTopics) {
            twitter = new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
            executorService.scheduleAtFixedRate(new TwitterTrendsClientInternal(), 0, fetchInterval, TimeUnit.MINUTES);
        }
    }

    public void shutdown() throws TwitterClientException {
        executorService.shutdown();
        pollingClient.stop();
    }

    class TwitterTrendsClientInternal implements Runnable {

        @Override
        public void run() {
            SearchOperations searchOperations = twitter.searchOperations();
            List<String> trendingTopics = new ArrayList<String>();
            for (Map.Entry<String, String> entry : locationToWoeId.entrySet()) {
                Trends trends = searchOperations.getLocalTrends(Long.parseLong(entry.getValue()));
                List<Trend> trendList = trends.getTrends();
                for (Trend trend : trendList) {
                    trendingTopics.add(trend.getName());
                }
            }
            if (pollForTrendingTopics) {
                pollingClient.addTrackingTerms(trendingTopics);
            }
        }
    }
}
