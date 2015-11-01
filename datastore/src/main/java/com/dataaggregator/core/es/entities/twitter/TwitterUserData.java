package com.dataaggregator.core.es.entities.twitter;

import com.dataaggregator.core.ITwitterUserData;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import twitter4j.User;

import java.util.Date;
import org.springframework.data.annotation.Id;

/**
 * Created by srividyak on 02/01/15.
 */
@Document(indexName = "data_aggs", type = "twitter_user_data")
public class TwitterUserData implements ITwitterUserData {

    @Id
    private String id;

    @Field(type = FieldType.Nested)
    private User user;
    private Date createdAt;
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
