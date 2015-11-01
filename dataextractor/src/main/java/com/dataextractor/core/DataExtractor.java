package com.dataextractor.core;

import com.dataextractor.core.api.ProcessJob;
import com.dataextractor.daos.IPageVariableDAO;
import com.dataextractor.daos.IVariableGroupDAO;
import com.dataextractor.entities.Job;
import com.dataextractor.entities.PageVariableValue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Created by srividyak on 26/01/15.
 */
@Component
public class DataExtractor<T> {
    
    @Autowired
    private IPageVariableDAO pageVariableDAO;

    @Autowired
    private IVariableGroupDAO variableGroupDAO;
    
    private static ApplicationContext context;
    
    private static DataExtractor INSTANCE;

    private static final Logger LOG = Logger.getLogger(DataExtractor.class);

    private static void init() {
        LOG.debug("initializing data extractor");
        String configDir = System.getProperty("config.path", "config" + File.separator);
        context = new FileSystemXmlApplicationContext(configDir + "dataExtractorHibernateConfig.xml");
    }
    
    public synchronized static DataExtractor getInstance() {
        if (INSTANCE == null) {
            init();
            INSTANCE = context.getBean(DataExtractor.class);
        }
        return INSTANCE;
    }
    
    public List<Object> extract(List<Job> jobs) {
        ExecutorService executorService = Executors.newFixedThreadPool(jobs.size());
        LOG.debug("extracting data for jobs ...");
        List<Future> futures = new ArrayList<Future>();
        List<Object> objects = new ArrayList<Object>();
        for (Job job : jobs) {
            LOG.debug("extracting data for job with url : " + job.getUrl());
            ProcessJob processJob = new ProcessJob(job, pageVariableDAO, variableGroupDAO);
            Future future = executorService.submit(processJob);
            LOG.debug("adding future to future list");
            futures.add(future);
        }
        executorService.shutdown();
        for (Future future : futures) {
            try {
                Object o = future.get();
                objects.add(o);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            } catch (ExecutionException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return objects;
    }
    
    public List<Object> extract(Job job) {
        LOG.debug("extracting data for job ...");
        ProcessJob processJob = new ProcessJob(job, pageVariableDAO, variableGroupDAO);
        List<Object> objects = processJob.process();
        return objects;
    }
    
    public Object getBean(Class clazz) {
        return context.getBean(clazz);
    }

}
