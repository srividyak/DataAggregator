package com.dataextractor.pagescraper;

import com.dataextractor.core.DataExtractorException;
import com.dataextractor.entities.PageVariableValue;
import com.dataextractor.entities.VariableGroup;
import com.dataextractor.entities.VariableGroupValue;
import com.google.common.base.CaseFormat;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by srividyak on 22/02/15.
 */
public class DataBuilder {

    private static final Logger LOG = Logger.getLogger(DataBuilder.class);
    
    /**
     * * Assumes that required variable has default constructor
     * @param pageVariableValue
     * @return
     */
    public static Object convertToStdVariable(PageVariableValue pageVariableValue) throws DataExtractorException {
        try {
            Object value = pageVariableValue.getVariableValue(); // Eg: mobiles
            String dataType = pageVariableValue.getVariableDataType(); // Eg: string. This is scalar always
            if (dataType.equals("java.lang.Integer")) {
                return Integer.parseInt(value.toString());
            } else if (dataType.equals("java.lang.Double")) {
                return Double.parseDouble(value.toString());
            } else if (dataType.equals("java.lang.Long")) {
                return Long.parseLong(value.toString());
            } else if (dataType.equals("java.lang.Float")) {
                return Float.parseFloat(value.toString());
            } else if (dataType.equals("java.lang.String")) {
                return value.toString();
            } else {
                throw new DataExtractorException("Invalid data type to convert data into: " + dataType);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new DataExtractorException(e.getMessage());
        }
    }


    public static Object convertToStdVariable(VariableGroupValue variableGroupValue) throws DataExtractorException {
        List<PageVariableValue> pageVariableValues = variableGroupValue.getVariableValues();
        try {
            String dataType = variableGroupValue.getGroupDataType();
            Class clazz = Class.forName(dataType);
            Object group = clazz.newInstance();
            for (PageVariableValue pageVariableValue : pageVariableValues) {
                try {
                    Object o = convertToStdVariable(pageVariableValue);
                    String variableId = pageVariableValue.getVariableId();
                    LOG.debug("value of pageVariable : " + variableId + " = " + o);
                    String variableDataType = pageVariableValue.getVariableDataType();
                    Class fieldClass = Class.forName(variableDataType);
                    String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, variableId); // setter method
                    Method method = clazz.getMethod(methodName, fieldClass);
                    LOG.debug("calling setter: " + methodName + " on class of type: " + dataType + " with parameter: " + o);
                    method.invoke(group, o);
                } catch (DataExtractorException deEx) {
                    LOG.error(deEx.getMessage(), deEx);
                }
            }
            return group;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new DataExtractorException(e.getMessage());
        }
    }

}
