package com.dataextractor.daos.impl;

import com.dataextractor.daos.ISourceDAO;
import com.dataextractor.entities.Source;
import org.hibernate.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by srividyak on 21/02/15.
 */
@Component
@Transactional
public class SourceDao extends BaseDao implements ISourceDAO {
    
    @Override
    public List<Source> getSources() {
        Query query = sessionFactory.getCurrentSession().createQuery("select source from Source source");
        List<Source> sources = query.list();
        return sources;
    }

    @Override
    public List<Source> getSources(Source.type type) {
        Query query = sessionFactory.getCurrentSession().createQuery("select source from Source source where " +
                "source.sourceType = :type");
        query.setParameter("type", type);
        List<Source> sources = query.list();
        return sources;
    }

    @Override
    public Source getSource(String source) {
        Query query = sessionFactory.getCurrentSession().createQuery("select source from Source source where source.source = :source");
        query.setParameter("source", source);
        List<Source> sources = query.list();
        return sources.size() > 0 ? sources.get(0) : null;
    }
}
