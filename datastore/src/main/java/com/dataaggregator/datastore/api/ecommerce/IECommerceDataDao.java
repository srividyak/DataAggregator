package com.dataaggregator.datastore.api.ecommerce;

import com.dataaggregator.core.es.entities.ecommerce.ECommerceProduct;

/**
 * Created by srividyak on 13/04/15.
 */
public interface IECommerceDataDao {
    
    boolean save(ECommerceProduct product);
}
