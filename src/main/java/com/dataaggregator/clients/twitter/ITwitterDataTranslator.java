package com.dataaggregator.clients.twitter;

import twitter4j.Status;

/**
 * Created by srividyak on 02/01/15.
 */
public interface ITwitterDataTranslator {

    void translate(Status status);
}
