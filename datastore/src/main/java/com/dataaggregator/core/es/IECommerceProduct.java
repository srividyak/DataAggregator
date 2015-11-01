package com.dataaggregator.core.es;

import java.util.List;
import java.util.Map;

/**
 * Created by srividyak on 12/04/15.
 */
public interface IECommerceProduct {
    
    public void setImage(String image);
    
    public String getImage();
    
    public void setName(String name);
    
    public String getName();
    
    public void setSourceLink(String sourceLink);
    
    public String getSourceLink();
    
    public void setRating(String rating);
    
    public String getRating();
    
    public void setNumRatings(String numRatings);
    
    public String getNumRatings();
    
    public void setCategory(String category);
    
    public String getCategory();
    
    public void setPrice(String price);
    
    public String getPrice();
    
    public void setFeatures(List<String> features);
    
    public List<String> getFeatures();
    
    public void setCustomProperties(Map<String, String> customProperties);
    
    public Map<String, String> getCustomProperties();
    
    public void setSource(String source);
    
    public String getSource();
}
