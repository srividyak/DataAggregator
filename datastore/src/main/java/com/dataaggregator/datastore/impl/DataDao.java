package com.dataaggregator.datastore.impl;

import com.dataaggregator.core.IData;
import com.dataaggregator.core.es.entities.Data;
import com.dataaggregator.core.es.repositories.DataRepository;
import com.dataaggregator.datastore.api.IDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by srividyak on 02/01/15.
 */
@Component
public class DataDao implements IDataDao {

    @Autowired
    private DataRepository repository;

    /**
     * TBD: based on search queries
     * @return
     */
    @Override
    public IData getData() {
        return null;
    }

    @Override
    public boolean save(IData iData) {
        try {
            repository.save((Data) iData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
