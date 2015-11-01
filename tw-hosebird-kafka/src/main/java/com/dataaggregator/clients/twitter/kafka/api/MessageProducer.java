package com.dataaggregator.clients.twitter.kafka.api;

/**
 * Created by srividyak on 01/01/15.
 */
public interface MessageProducer<T> {

    public boolean send(T msg);
}
