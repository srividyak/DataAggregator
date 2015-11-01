package com.dataaggregator.core.es.entities.ecommerce;

import com.dataaggregator.core.es.IECommerceProduct;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.Map;

/**
 * Created by srividyak on 12/04/15.
 */
@Document(indexName = "data_aggs", type = "ecommerce_product")
public class ECommerceProduct implements IECommerceProduct {

    private String image;
    private String name;
    private String sourceLink;
    private String rating;
    private String numRatings;
    private String category;
    private String price;
    private List<String> features;
    private Map<String, String> customProperties;
    private String source;
    
    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSourceLink() {
        return sourceLink;
    }

    @Override
    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    @Override
    public String getRating() {
        return rating;
    }

    @Override
    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String getNumRatings() {
        return numRatings;
    }

    @Override
    public void setNumRatings(String numRatings) {
        this.numRatings = numRatings;
    }

    @Override
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public List<String> getFeatures() {
        return features;
    }

    @Override
    public void setFeatures(List<String> features) {
        this.features = features;
    }

    @Override
    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    @Override
    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }
}
