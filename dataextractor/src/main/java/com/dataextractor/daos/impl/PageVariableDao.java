package com.dataextractor.daos.impl;

import com.dataextractor.daos.IPageVariableDAO;
import com.dataextractor.entities.Job;
import com.dataextractor.entities.PageVariable;
import org.hibernate.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by srividyak on 15/02/15.
 */
@Component
@Transactional
public class PageVariableDao extends BaseDao implements IPageVariableDAO {
    
    @Override
    public List<PageVariable> getPageVariablesOfJob(Job job) {
        Query query = sessionFactory.getCurrentSession().createQuery("select pv from PageVariable pv where pv.job.id = :jobId");
        query.setParameter("jobId", job.getId());
        List<PageVariable> pageVariables = query.list();
        return pageVariables;
    }

    @Override
    public List<PageVariable> getPageVariablesOfGroup(Job job, int groupVariableId) {
        Query query = sessionFactory.getCurrentSession().createQuery("select pv from PageVariable pv where pv.job.id = :jobId and pv.variableGroup.id = :groupVariableId");
        query.setParameter("jobId", job.getId());
        query.setParameter("groupVariableId", groupVariableId);
        List<PageVariable> pageVariables = query.list();
        return pageVariables;
    }

    @Override
    public List<PageVariable> getPageVariablesWithNoGroup(Job job) {
        Query query = sessionFactory.getCurrentSession().createQuery("select pv from PageVariable pv where pv.job.id = :jobId and pv.variableGroup is null");
        query.setParameter("jobId", job.getId());
        List<PageVariable> pageVariables = query.list();
        return pageVariables;
    }
}
