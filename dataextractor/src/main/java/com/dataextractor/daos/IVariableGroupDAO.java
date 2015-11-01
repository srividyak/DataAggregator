package com.dataextractor.daos;

import com.dataextractor.entities.Job;
import com.dataextractor.entities.VariableGroup;

import java.util.List;

/**
 * Created by srividyak on 26/01/15.
 */
public interface IVariableGroupDAO {
    
    List<VariableGroup> getVariableGroups(Job job);
}
