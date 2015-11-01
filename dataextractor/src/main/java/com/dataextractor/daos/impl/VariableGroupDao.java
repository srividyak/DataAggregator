package com.dataextractor.daos.impl;

import com.dataextractor.daos.IPageVariableDAO;
import com.dataextractor.daos.IVariableGroupDAO;
import com.dataextractor.entities.Job;
import com.dataextractor.entities.PageVariable;
import com.dataextractor.entities.VariableGroup;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by srividyak on 15/02/15.
 */
@Component
@Transactional
public class VariableGroupDao extends BaseDao implements IVariableGroupDAO {
    
    @Autowired
    private IPageVariableDAO pageVariableDAO;
    
    @Override
    public List<VariableGroup> getVariableGroups(Job job) {
        Query query = sessionFactory.getCurrentSession().createQuery("select vg from VariableGroup vg where vg.job.id = :jobId");
        query.setParameter("jobId", job.getId());
        List<VariableGroup> variableGroups  = query.list();
        for (VariableGroup variableGroup : variableGroups) {
            List<PageVariable> pageVariables = pageVariableDAO.getPageVariablesOfGroup(job, variableGroup.getId());
            variableGroup.setPageVariables(pageVariables);
        }
        return variableGroups;
    }
}
