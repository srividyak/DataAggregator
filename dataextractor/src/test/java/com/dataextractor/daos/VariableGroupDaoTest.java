package com.dataextractor.daos;

import com.dataextractor.entities.Job;
import com.dataextractor.entities.Source;
import com.dataextractor.entities.VariableGroup;
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
public class VariableGroupDaoTest {
    
    @Autowired
    private IVariableGroupDAO variableGroupDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Source source;
    private Job job;
    private VariableGroup variableGroup;
    
    @Before
    public void before() {
        // inserting a source
        source = new Source();
        source.setSource("source");

        // inserting a job
        job = new Job();
        job.setSource(source);
        job.setUrl("url");

        // inserting into variable group
        variableGroup = new VariableGroup();
        variableGroup.setGroupDataType("groupDataType");
        variableGroup.setGroupId("groupId");
        variableGroup.setGroupingXpath("groupXpath");
        variableGroup.setJob(job);

        sessionFactory.getCurrentSession().save(variableGroup);
    }
    
    @Test
    public void testVariableGroups() {
        List<VariableGroup> variableGroups = variableGroupDAO.getVariableGroups(job);
        assert variableGroups.size() == 1;
        VariableGroup vg = variableGroups.get(0);
        assert vg.getGroupDataType().equals(variableGroup.getGroupDataType());
        assert vg.getGroupingXpath().equals(variableGroup.getGroupingXpath());
        assert vg.getJob().getSource().getSource().equals(variableGroup.getJob().getSource().getSource());
    }
    
    @After
    public void after() {
        Query query = sessionFactory.getCurrentSession().createQuery("delete from VariableGroup");
        query.executeUpdate();
        query = sessionFactory.getCurrentSession().createQuery("delete from Job");
        query.executeUpdate();
        query = sessionFactory.getCurrentSession().createQuery("delete from Source");
        query.executeUpdate();
    }
}
