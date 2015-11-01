package com.dataaggregator.datastore.api.twitter;

import com.dataaggregator.core.ITwitterUserData;

/**
 * Created by srividyak on 02/01/15.
 */
public interface ITwitterUserDataDao {

    ITwitterUserData getTwitterUserDataDao();

    boolean save(ITwitterUserData data);
}
