package com.dataaggregator.clients.twitter.kafka.api.impl;

import com.dataaggregator.clients.twitter.kafka.api.MessageProducer;
import com.dataaggregator.clients.twitter.kafka.util.Constants;
import com.dataaggregator.util.PropertyReader;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by srividyak on 01/01/15.
 */
public class KafkaProducer implements MessageProducer {

    private static ProducerConfig config = null;
    private Producer<String, Object> producer;
    private static KafkaProducer INSTANCE;
    private static String kafkaRawMsgTopic = PropertyReader.getInstance().getProperty(Constants.KAFKA_RAW_MSG_TOPIC, "raw_tweets");
    private static String brokerConfig = PropertyReader.getInstance().getProperty(Constants.KAFKA_BROKER_LIST);
    private static final Logger LOG = Logger.getLogger(KafkaProducer.class);

    private KafkaProducer() {
        Properties props = new Properties();
        props.put("metadata.broker.list", brokerConfig);
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        config = new ProducerConfig(props);
        producer = new Producer<String, Object>(config);
    }

    public synchronized static KafkaProducer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KafkaProducer();
        }
        return INSTANCE;
    }

    @Override
    public boolean send(Object msg) {
        try {
            KeyedMessage<String, Object> data = new KeyedMessage<String, Object>(kafkaRawMsgTopic, msg);
            LOG.debug("sending msg from kafka producer: " + msg);
            producer.send(data);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
