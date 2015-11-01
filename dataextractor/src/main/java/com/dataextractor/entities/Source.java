package com.dataextractor.entities;

import javax.persistence.*;

/**
 * Created by srividyak on 11/02/15.
 */
@Entity
@Table(name = "source")
public class Source {
    
    public enum type {
        ECOMMERCE, OTHER
    };
    
    private int id;
    private String source;
    private String searchUrlFormat;
    private String url;
    private type sourceType;

    @Column(name = "type")
    public type getSourceType() {
        return sourceType;
    }

    public void setSourceType(type sourceType) {
        this.sourceType = sourceType;
    }

    @Column(name = "search_url_format")
    public String getSearchUrlFormat() {
        return searchUrlFormat;
    }

    public void setSearchUrlFormat(String searchUrlFormat) {
        this.searchUrlFormat = searchUrlFormat;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "source")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
