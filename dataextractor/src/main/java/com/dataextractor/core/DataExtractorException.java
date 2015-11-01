package com.dataextractor.core;

import org.apache.log4j.Logger;

/**
 * Created by srividyak on 22/02/15.
 */
public class DataExtractorException extends Exception {
    
    private static final Logger LOG = Logger.getLogger(DataExtractorException.class);
    
    public DataExtractorException(String msg) {
        super(msg);
        LOG.error(msg);
    }
}
