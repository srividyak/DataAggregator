package com.dataextractor.entities;

import java.util.List;

/**
 * Created by srividyak on 26/01/15.
 */
public class VariableGroupValue {
    
    private List<PageVariableValue> variableValues;
    private String groupId;
    private String groupDataType;

    public String getGroupDataType() {
        return groupDataType;
    }

    public void setGroupDataType(String groupDataType) {
        this.groupDataType = groupDataType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<PageVariableValue> getVariableValues() {
        return variableValues;
    }

    public void setVariableValues(List<PageVariableValue> variableValues) {
        this.variableValues = variableValues;
    }
}
