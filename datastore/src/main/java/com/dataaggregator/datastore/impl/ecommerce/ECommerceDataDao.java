package com.dataaggregator.datastore.impl.ecommerce;

import com.dataaggregator.core.es.entities.ecommerce.ECommerceProduct;
import com.dataaggregator.core.es.repositories.ECommerceProductRepository;
import com.dataaggregator.datastore.api.ecommerce.IECommerceDataDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by srividyak on 13/04/15.
 */
@Component
public class ECommerceDataDao implements IECommerceDataDao {
    
    @Autowired
    private ECommerceProductRepository repository;
    
    private static final Logger LOG = Logger.getLogger(ECommerceDataDao.class);
    
    @Override
    public boolean save(ECommerceProduct product) {
        String md5 = DigestUtils.md5Hex(product.getSource() + "_" + product.getSourceLink());
        product.setId(md5);
        try {
            LOG.debug("saving ecommerce product: " + product.getId());
            repository.save(product);
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;    
        }
    }
}
