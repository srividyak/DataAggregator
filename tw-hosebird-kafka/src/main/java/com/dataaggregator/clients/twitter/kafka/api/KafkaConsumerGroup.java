package com.dataaggregator.clients.twitter.kafka.api;

import com.dataaggregator.clients.twitter.kafka.util.Constants;
import com.dataaggregator.util.PropertyReader;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by srividyak on 01/01/15.
 */
public class KafkaConsumerGroup {

    private ConsumerConnector consumerConnector;
    private String topic;
    private int numConsumerThreads;
    private List<KafkaStream<byte[], byte[]>> kafkaStreams;
    private static String zkConfig = PropertyReader.getInstance().getProperty(Constants.ZOOKEEPER_HOST) +
            ":" + PropertyReader.getInstance().getProperty(Constants.ZOOKEEPER_PORT);
    private static String zkSessionTimeout = PropertyReader.getInstance().getProperty(Constants.ZOOKEEPER_SESSION_TIMEOUT_MS, "400");
    private static String zkSyncTimeout = PropertyReader.getInstance().getProperty(Constants.ZOOKEEPER_SYNC_TIME_MS, "200");
    private static String zkAutocommitInterval = PropertyReader.getInstance().getProperty(Constants.ZOOKEEPER_AUTOCOMMIT_INTERVAL_MS, "1000");

    public KafkaConsumerGroup(String groupId, String topic, int numConsumerThreads) {
        this.topic = topic;
        consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(groupId));
        this.numConsumerThreads = numConsumerThreads;
        initKafkaStreams();
    }

    private void initKafkaStreams() {
        Map<String, Integer> topicNumConsumerThreadsMap = new HashMap<String, Integer>();
        topicNumConsumerThreadsMap.put(topic, new Integer(numConsumerThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicNumConsumerThreadsMap);
        kafkaStreams = consumerMap.get(topic);
    }

    public List<KafkaStream<byte[], byte[]>> getKafkaStreams() {
        return kafkaStreams;
    }

    private static ConsumerConfig createConsumerConfig(String groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", zkConfig);
        props.put("group.id", groupId);
        props.put("zookeeper.session.timeout.ms", zkSessionTimeout);
        props.put("zookeeper.sync.time.ms", zkSyncTimeout);
        props.put("auto.commit.interval.ms", zkAutocommitInterval);

        return new ConsumerConfig(props);
    }
}
