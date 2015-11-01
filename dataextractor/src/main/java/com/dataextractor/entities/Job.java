package com.dataextractor.entities;

import javax.persistence.*;
import java.util.Map;

/**
 * Created by srividyak on 26/01/15.
 */
@Entity
@Table(name = "job")
public class Job extends BaseEntity {
    
    private int id;
    private String url;
    private Source source;
    private PageType pageType;
    private Boolean isProcessed;
    private Boolean isActive;
    private Boolean isRegex;
    private Boolean isPaginated;
    private Boolean isMaster;
    
    private Map<String, String> replacements;

    @Transient
    public Map<String, String> getReplacements() {
        return replacements;
    }

    public void setReplacements(Map<String, String> replacements) {
        this.replacements = replacements;
    }

    @Column(name = "is_regex")
    public Boolean getIsRegex() {
        return isRegex;
    }

    public void setIsRegex(Boolean isRegex) {
        this.isRegex = isRegex;
    }

    @Column(name = "is_paginated")
    public Boolean getIsPaginated() {
        return isPaginated;
    }

    public void setIsPaginated(Boolean isPaginated) {
        this.isPaginated = isPaginated;
    }

    @Column(name = "is_master")
    public Boolean getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(Boolean isMaster) {
        this.isMaster = isMaster;
    }

    @Column(name = "is_processed")
    public Boolean getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    @Column(name = "is_active")
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Column(name = "page_type")
    public PageType getPageType() {
        return pageType;
    }

    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "source_id")
    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
