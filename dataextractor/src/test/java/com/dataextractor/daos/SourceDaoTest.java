package com.dataextractor.daos;

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
 * Created by srividyak on 21/02/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:dataExtractorHibernateConfig.xml"
})
@Transactional
public class SourceDaoTest {
    
    @Autowired
    private ISourceDAO sourceDAO;

    @Autowired
    private SessionFactory sessionFactory;
    
    private Source source;
    
    private Source.type sourceType = Source.type.ECOMMERCE;
    
    @Before
    public void before() {
        source = new Source();
        source.setSource("flipkart");
        source.setSourceType(sourceType);
        source.setSearchUrlFormat("http://flipkart.com?search=QUERY");
        source.setUrl("http://flipkart.com");

        sessionFactory.getCurrentSession().save(source);
    }
    
    @Test
    public void testGetSources() {
        List<Source> sources = sourceDAO.getSources();
        assert sources.size() == 1;
    }
    
    @Test
    public void testGetSourcesOfType() {
        List<Source> sources = sourceDAO.getSources(sourceType);
        assert sources.size() == 1;
        Source s = sources.get(0);
        assert s.getSourceType() == sourceType;
    }

    @After
    public void after() {
        Query query = sessionFactory.getCurrentSession().createQuery("delete from Source");
        query.executeUpdate();
    }
}
