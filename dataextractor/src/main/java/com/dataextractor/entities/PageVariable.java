package com.dataextractor.entities;

import javax.persistence.*;

/**
 * Created by srividyak on 26/01/15.
 */
@Entity
@Table(name = "page_variable")
public class PageVariable {
    
    private String xpath;
    private String variableId;
    private String variableDataType;
    private String attribute;
    private int id;
    private Job job;
    private VariableGroup variableGroup;

    public PageVariable() {
        
    }

    public PageVariable(String xpath, String variableId, String variableDataType) {
        this.xpath = xpath;
        this.variableId = variableId;
        this.variableDataType = variableDataType;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_variable_id")
    public VariableGroup getVariableGroup() {
        return variableGroup;
    }

    public void setVariableGroup(VariableGroup variableGroup) {
        this.variableGroup = variableGroup;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_id")
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Column(name = "xpath")
    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    @Column(name = "variable_id")
    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    @Column(name = "variable_data_type")
    public String getVariableDataType() {
        return variableDataType;
    }

    public void setVariableDataType(String variableDataType) {
        this.variableDataType = variableDataType;
    }

    @Column(name = "attribute")
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
