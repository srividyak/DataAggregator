package com.dataaggregator.core;

import twitter4j.User;

import java.util.Date;

/**
 * Created by srividyak on 02/01/15.
 */
public interface ITwitterUserData {

    public User getUser();

    public void setUser(User user);

    public Date getCreatedAt();

    public void setCreatedAt(Date createdAt);

    public Date getUpdatedAt();

    public void setUpdatedAt(Date updatedAt);

    public void setId(String id);

    public String getId();

}
