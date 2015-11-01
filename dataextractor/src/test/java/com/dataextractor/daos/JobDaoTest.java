package com.dataextractor.daos;

import com.dataextractor.entities.Job;
import com.dataextractor.entities.Source;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by srividyak on 17/02/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:dataExtractorHibernateConfig.xml"
})
@Transactional
public class JobDaoTest {

    @Autowired
    private IJobDAO jobDAO;

    private Source source;
    private Job job;

    @Autowired
    private SessionFactory sessionFactory;

    @Before
    public void before() {
        // inserting a source
        source = new Source();
        source.setSource("flipkart");
        source.setSourceType(Source.type.ECOMMERCE);
        source.setSearchUrlFormat("http://flipkart.com?search=QUERY");
        source.setUrl("http://flipkart.com");

        // inserting a job
        job = new Job();
        job.setIsActive(true);
        job.setIsProcessed(false);
        job.setSource(source);
        job.setUrl("url");
        job.setIsMaster(true);

        sessionFactory.getCurrentSession().save(job);
    }
    
    @Test
    public void testJobsOfSource() {
        List<Job> jobs = jobDAO.getAllJobs(source);
        assert jobs.size() == 1;
    }
    
    @Test
    public void testMasterJobs() {
        List<Job> jobs = jobDAO.getMasterJobs();
        assert jobs.size() == 1;
    }
    
    public void testMasterJobsOfSource() {
        List<Job> jobs = jobDAO.getMasterJobs(source);
        assert jobs.size() == 1;
    }
    
    @Test
    public void testActiveJobs() {
        Job newJob = new Job();
        Source newSource = new Source();
        newSource.setSource("newSource");
        newJob.setSource(newSource);
        newJob.setIsActive(false);
        newJob.setIsProcessed(false);
        
        sessionFactory.getCurrentSession().save(newJob);
        
        List<Job> jobs = jobDAO.getAllJobs();
        assert jobs.size() == 1;
        Job j = jobs.get(0);
        assert j.getSource().getSource().equals(source.getSource());
    }
    
    @Test
    public void testJobOfId() {
        Job j = jobDAO.getJob(job.getId());
        assert j.getSource().getSource().equals(source.getSource());
    }

    @After
    public void after() {
        Query query = sessionFactory.getCurrentSession().createQuery("delete from Job");
        query.executeUpdate();
        query = sessionFactory.getCurrentSession().createQuery("delete from Source");
        query.executeUpdate();
    }
}
