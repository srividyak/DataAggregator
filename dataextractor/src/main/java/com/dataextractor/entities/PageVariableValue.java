package com.dataextractor.entities;

/**
 * Created by srividyak on 26/01/15.
 */
public class PageVariableValue extends PageVariable {
    
    private Object variableValue;

    public Object getVariableValue() {
        return variableValue;
    }
    
    public PageVariableValue() {
        
    }
    
    public PageVariableValue(PageVariable variable) {
        super(variable.getXpath(), variable.getVariableId(), variable.getVariableDataType());
    }
    
    public PageVariableValue(PageVariable variable, Object value) {
        this(variable);
        setVariableValue(value);
    }

    public void setVariableValue(Object variableValue) {
        this.variableValue = variableValue;
    }
}
