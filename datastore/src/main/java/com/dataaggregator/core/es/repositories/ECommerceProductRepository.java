package com.dataaggregator.core.es.repositories;

import com.dataaggregator.core.es.entities.ecommerce.ECommerceProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by srividyak on 13/04/15.
 */
public interface ECommerceProductRepository extends ElasticsearchRepository<ECommerceProduct, String> {
}
