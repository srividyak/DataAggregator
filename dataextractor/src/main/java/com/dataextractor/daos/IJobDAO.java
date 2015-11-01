package com.dataextractor.daos;

import com.dataextractor.entities.Job;
import com.dataextractor.entities.Source;

import java.util.List;

/**
 * Created by srividyak on 26/01/15.
 */
public interface IJobDAO {
    
    public List<Job> getAllJobs();
    
    public Job getJob(int id);
    
    public List<Job> getMasterJobs();
    
    public List<Job> getAllJobs(Source source);
    
    public List<Job> getMasterJobs(Source source);
    
    public List<Job> getAllNonMasterJobs();
    
    public List<Job> getNonMasterJobs(Source source);
}
