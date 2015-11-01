package com.dataaggregator.clients.twitter;

import com.dataaggregator.core.exception.PlatformException;

/**
 * Created by srividyak on 04/01/15.
 */
public class TwitterClientException extends PlatformException {

    public TwitterClientException() {
        super();
    }

    public TwitterClientException(String msg) {
        super(msg);
    }
}
