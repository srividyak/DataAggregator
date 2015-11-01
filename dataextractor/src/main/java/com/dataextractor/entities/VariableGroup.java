package com.dataextractor.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by srividyak on 26/01/15.
 */
@Entity
@Table(name = "variable_group")
public class VariableGroup {
    
    private List<PageVariable> pageVariables;
    private String groupingXpath;
    private String groupId;
    private String groupDataType;
    private int id;
    private Job job;
    
    public VariableGroup() {

    }

    public VariableGroup(List<PageVariable> pageVariables, String groupingXpath, String groupId, String groupDataType) {
        this.pageVariables = pageVariables;
        this.groupingXpath = groupingXpath;
        this.groupId = groupId;
        this.groupDataType = groupDataType;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_id")
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Transient
    public List<PageVariable> getPageVariables() {
        return pageVariables;
    }

    public void setPageVariables(List<PageVariable> pageVariables) {
        this.pageVariables = pageVariables;
    }

    @Column(name = "grouping_xpath")
    public String getGroupingXpath() {
        return groupingXpath;
    }

    public void setGroupingXpath(String groupingXpath) {
        this.groupingXpath = groupingXpath;
    }

    @Column(name = "group_id")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name = "group_data_type")
    public String getGroupDataType() {
        return groupDataType;
    }

    public void setGroupDataType(String groupDataType) {
        this.groupDataType = groupDataType;
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
