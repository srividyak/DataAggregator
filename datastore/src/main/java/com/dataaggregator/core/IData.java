package com.dataaggregator.core;

import com.dataaggregator.core.es.ICommonData;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by srividyak on 02/01/15.
 */
public interface IData extends ICommonData {

    public Map<String, Object> getCustomData();

    public void setCustomData(Map<String, Object> customData);

    public String getTitle();

    public void setTitle(String title);

    public String getDescription();

    public void setDescription(String description);

    public Date getTimestamp();

    public void setTimestamp(Date timestamp);

    public String getSourceLink();

    public void setSourceLink(String sourceLink);

    public void setId(String id);

    public String getId();

}
