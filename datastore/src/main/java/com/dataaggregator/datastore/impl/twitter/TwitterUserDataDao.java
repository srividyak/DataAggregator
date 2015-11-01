package com.dataaggregator.datastore.impl.twitter;

import com.dataaggregator.core.es.entities.twitter.TwitterUserData;
import com.dataaggregator.core.es.repositories.TwitterUserDataRepository;
import com.dataaggregator.core.ITwitterUserData;
import com.dataaggregator.datastore.api.twitter.ITwitterUserDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by srividyak on 02/01/15.
 */
@Component
public class TwitterUserDataDao implements ITwitterUserDataDao {

    @Autowired
    private TwitterUserDataRepository repository;

    /**
     * TBD : based on search queries
     * @return
     */
    @Override
    public ITwitterUserData getTwitterUserDataDao() {
        return null;
    }

    @Override
    public boolean save(ITwitterUserData data) {
        try {
            repository.save((TwitterUserData) data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
