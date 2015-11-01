package com.dataaggregator.clients.twitter;

import com.dataaggregator.core.IData;
import com.dataaggregator.core.ITwitterUserData;
import com.dataaggregator.datastore.api.IDataDao;
import com.dataaggregator.datastore.api.twitter.ITwitterUserDataDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by srividyak on 02/01/15.
 */
@Component
public class TwitterDataProcessor implements ITwitterDataProcessor {

    @Autowired
    private IDataDao dataDao;

    @Autowired
    private ITwitterUserDataDao userDataDao;

    private static final Logger LOG = Logger.getLogger(TwitterDataProcessor.class);

    @Override
    public void process(IData data, ITwitterUserData twitterUserData) {
        LOG.debug("processing twitter data : " + data.getDescription());
        dataDao.save(data);
        userDataDao.save(twitterUserData);
    }
}
