package com.dataaggregator.core.es.entities.ecommerce;

import com.dataaggregator.core.es.IECommerceSearchData;

/**
 * Created by srividyak on 22/02/15.
 */
public class ECommerceSearchData implements IECommerceSearchData {
    
    private String searchQuery;
    
    @Override
    public String getSearchQuery() {
        return searchQuery;
    }

    @Override
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

}
