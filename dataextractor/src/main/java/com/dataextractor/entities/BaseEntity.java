package com.dataextractor.entities;

import javax.persistence.Column;
import java.util.Date;

/**
 * Created by srividyak on 14/02/15.
 */
public abstract class BaseEntity {
    
    protected Date createdAt;
    protected Date updatedAt;
    
    @Column(name = "created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
