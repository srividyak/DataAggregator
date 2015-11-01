package com.dataaggregator.clients.twitter;

import com.dataaggregator.core.IData;
import com.dataaggregator.core.ITwitterUserData;

/**
 * Created by srividyak on 02/01/15.
 */
public interface ITwitterDataProcessor {

    void process(IData data, ITwitterUserData twitterUserData);

}
