package com.dataextractor.daos.impl;

import com.dataextractor.daos.IJobDAO;
import com.dataextractor.entities.Job;
import com.dataextractor.entities.Source;
import org.hibernate.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by srividyak on 15/02/15.
 */
@Component
@Transactional
public class JobDao extends BaseDao implements IJobDAO {
    
    @Override
    public List<Job> getAllJobs() {
        Query query = sessionFactory.getCurrentSession().createQuery("select job from Job job where job.isProcessed = false and job.isActive = true");
        List<Job> jobs = query.list();
        return jobs;
    }

    @Override
    public Job getJob(int id) {
        Query query = sessionFactory.getCurrentSession().createQuery("select job from Job job where job.id = :id");
        query.setParameter("id", id);
        List<Job> jobs = query.list();
        return jobs.size() > 0 ? jobs.get(0) : null;
    }

    @Override
    public List<Job> getMasterJobs() {
        Query query = sessionFactory.getCurrentSession().createQuery("select job from Job job where job.isProcessed = false and job.isActive = true " +
                "and job.isMaster = true");
        List<Job> jobs = query.list();
        return jobs;
    }

    @Override
    public List<Job> getAllJobs(Source source) {
        Query query = sessionFactory.getCurrentSession().createQuery("select job from Job job where job.isProcessed = false and job.isActive = true " +
                "and job.source.id = :id");
        query.setParameter("id", source.getId());
        List<Job> jobs = query.list();
        return jobs;
    }

    @Override
    public List<Job> getMasterJobs(Source source) {
        Query query = sessionFactory.getCurrentSession().createQuery("select job from Job job where job.isProcessed = false and job.isActive = true " +
                "and job.isMaster = true and job.source.id = :id");
        query.setParameter("id", source.getId());
        List<Job> jobs = query.list();
        return jobs;
    }

    @Override
    public List<Job> getAllNonMasterJobs() {
        Query query = sessionFactory.getCurrentSession().createQuery("select job from Job job where job.isProcessed = false and job.isActive = true" +
                " and isMaster = false");
        List<Job> jobs = query.list();
        return jobs;
    }

    @Override
    public List<Job> getNonMasterJobs(Source source) {
        Query query = sessionFactory.getCurrentSession().createQuery("select job from Job job where job.isProcessed = false and job.isActive = true" +
                " and isMaster = false and job.source.id = :id");
        query.setParameter("id", source.getId());
        List<Job> jobs = query.list();
        return jobs;
    }
}
