package com.dataextractor.daos;

import com.dataextractor.entities.Job;
import com.dataextractor.entities.PageVariable;

import java.util.List;

/**
 * Created by srividyak on 26/01/15.
 */
public interface IPageVariableDAO {
    
    List<PageVariable> getPageVariablesOfJob(Job job);

    List<PageVariable> getPageVariablesOfGroup(Job job, int groupVariableId);
    
    List<PageVariable> getPageVariablesWithNoGroup(Job job);
}
