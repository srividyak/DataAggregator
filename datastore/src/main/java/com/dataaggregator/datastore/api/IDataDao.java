package com.dataaggregator.datastore.api;

import com.dataaggregator.core.IData;

/**
 * Created by srividyak on 02/01/15.
 */
public interface IDataDao {

    IData getData();

    boolean save(IData data);

}
