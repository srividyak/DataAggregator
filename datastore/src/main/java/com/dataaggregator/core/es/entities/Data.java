package com.dataaggregator.core.es.entities;

import com.dataaggregator.core.IData;
import com.google.gson.JsonObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;

/**
 * Created by srividyak on 02/01/15.
 */
@Document(indexName = "data_aggs", type = "data")
public class Data implements IData {

    @Id
    private String id;

    private String title;
    private String description;
    private String source;
    private String sourceLink;

    @Field(type = FieldType.Nested)
    private Map<String, Object> customData;
    private Date timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
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
    public Map<String, Object> getCustomData() {
        return customData;
    }

    @Override
    public void setCustomData(Map<String, Object> customData) {
        this.customData = customData;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
