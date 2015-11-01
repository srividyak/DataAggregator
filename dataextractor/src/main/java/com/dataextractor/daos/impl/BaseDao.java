package com.dataextractor.daos.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by srividyak on 15/02/15.
 */
@Component
public abstract class BaseDao {
    
    @Autowired
    protected SessionFactory sessionFactory;
}
