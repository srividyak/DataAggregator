package com.dataaggregator.core.es.repositories;

import com.dataaggregator.core.es.entities.twitter.TwitterUserData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * Created by srividyak on 02/01/15.
 */
//@Component
public interface TwitterUserDataRepository extends ElasticsearchRepository<TwitterUserData, String> {
}
