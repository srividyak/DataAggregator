package com.dataextractor.daos;

import com.dataextractor.entities.Job;
import com.dataextractor.entities.PageVariable;
import com.dataextractor.entities.Source;
import com.dataextractor.entities.VariableGroup;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by srividyak on 16/02/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:dataExtractorHibernateConfig.xml"
})
@Transactional
public class PageVariableDaoTest {
    
    @Autowired
    private IPageVariableDAO pageVariableDAO;
    
    private Source source;
    private Job job;
    private PageVariable pageVariable;
    private VariableGroup variableGroup;
    
    @Autowired
    private SessionFactory sessionFactory;
    
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
        
        // inserting data into page variable
        pageVariable = new PageVariable();
        pageVariable.setAttribute("src");
        pageVariable.setJob(job);
        pageVariable.setVariableDataType("datatype");
        pageVariable.setVariableId("variableId");
        pageVariable.setXpath("xpath");
        pageVariable.setVariableGroup(variableGroup);

        sessionFactory.getCurrentSession().save(pageVariable);
    }

    @Test
    public void testPageVariablesOfJob() {
        List<PageVariable> pageVariables = pageVariableDAO.getPageVariablesOfJob(job);
        assert pageVariables.size() == 1;
        PageVariable pv = pageVariables.get(0);
        assert pv.getJob().getUrl().equals(job.getUrl());
        assert pv.getJob().getSource().getSource().equals(source.getSource());
        assert pv.getAttribute().equals(pageVariable.getAttribute());
        assert pv.getVariableDataType().equals(pageVariable.getVariableDataType());
        assert pv.getVariableId().equals(pageVariable.getVariableId());
        assert pv.getXpath().equals(pageVariable.getXpath());
    }
    
    @Test
    public void testPageVariablesOfGroup() {
        List<PageVariable> pageVariables = pageVariableDAO.getPageVariablesOfGroup(job, variableGroup.getId());
        assert pageVariables.size() == 1;
        PageVariable pv = pageVariables.get(0);
        assert pv.getJob().getUrl().equals(job.getUrl());
        assert pv.getJob().getSource().getSource().equals(source.getSource());
        assert pv.getAttribute().equals(pageVariable.getAttribute());
        assert pv.getVariableDataType().equals(pageVariable.getVariableDataType());
        assert pv.getVariableId().equals(pageVariable.getVariableId());
        assert pv.getXpath().equals(pageVariable.getXpath());
    }
    
    @Test
    public void testPageVariablesWithNoGroup() {
        // saving new page variable with no variable group
        PageVariable newPageVariable = new PageVariable();
        Job job = new Job();
        job.setUrl("noJob");
        job.setSource(source);
        
        String attribute = "noGroupAttr";
        String varDataType = "noGroupDataType";
        String variableId = "noGroupVarId";
        String xpath = "noGroupXpath";
        
        newPageVariable.setJob(job);
        newPageVariable.setAttribute(attribute);
        newPageVariable.setVariableDataType(varDataType);
        newPageVariable.setXpath(xpath);
        newPageVariable.setVariableId(variableId);
        sessionFactory.getCurrentSession().save(newPageVariable);
        
        List<PageVariable> pageVariables = pageVariableDAO.getPageVariablesWithNoGroup(job);
        assert pageVariables.size() == 1;
        PageVariable pv = pageVariables.get(0);
        assert pv.getJob().getUrl().equals(job.getUrl());
        assert pv.getJob().getSource().getSource().equals(source.getSource());
        assert pv.getAttribute().equals(attribute);
        assert pv.getVariableDataType().equals(varDataType);
        assert pv.getVariableId().equals(variableId);
        assert pv.getXpath().equals(xpath);
    }
    
    @After
    public void after() {
        Query query = sessionFactory.getCurrentSession().createQuery("delete from PageVariable");
        query.executeUpdate();
        query = sessionFactory.getCurrentSession().createQuery("delete from VariableGroup");
        query.executeUpdate();
        query = sessionFactory.getCurrentSession().createQuery("delete from Job");
        query.executeUpdate();
        query = sessionFactory.getCurrentSession().createQuery("delete from Source");
        query.executeUpdate();
    }
}
