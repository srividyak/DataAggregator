package com.dataaggregator.core.es.repositories;

import com.dataaggregator.core.es.entities.Data;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by srividyak on 02/01/15.
 */
//@Component
public interface DataRepository extends ElasticsearchRepository<Data, String> {
}
