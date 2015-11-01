package com.dataextractor.pagescraper;

import com.dataaggregator.core.es.entities.ecommerce.ECommerceSearchData;
import com.dataextractor.core.DataExtractorException;
import com.dataextractor.entities.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by srividyak on 23/02/15.
 */
public class DataBuilderTest {
    
    private Source source;
    private Job job;
    
    @Before
    public void before() {
        source = new Source();
        source.setSource("flipkart");

        job = new Job();
        job.setIsActive(true);
        job.setIsMaster(true);
        job.setSource(source);
        
    }
    
    private PageVariableValue initPageVariableValue() {
        PageVariableValue pageVariableValue = new PageVariableValue();
        pageVariableValue.setVariableValue("variable value");
        pageVariableValue.setAttribute("data-tracking-id");
        pageVariableValue.setJob(job);
        pageVariableValue.setVariableDataType("java.lang.String");
        pageVariableValue.setXpath(".goquickly-list li a");
        pageVariableValue.setVariableId("searchQuery");
        return pageVariableValue;
    }
    
    @Test
    public void testConvertToStdVariableWithVariableGroup() {
        PageVariableValue pageVariableValue = initPageVariableValue();
        VariableGroupValue variableGroupValue = new VariableGroupValue();
        variableGroupValue.setGroupDataType("com.dataaggregator.core.es.entities.ecommerce.ECommerceSearchData");
        List<PageVariableValue> pageVariableValues = new ArrayList<PageVariableValue>();
        pageVariableValues.add(pageVariableValue);
        variableGroupValue.setVariableValues(pageVariableValues);
        try {
            Object o = DataBuilder.convertToStdVariable(variableGroupValue);
            assert o instanceof ECommerceSearchData;
            ECommerceSearchData searchData = (ECommerceSearchData) o;
            assert searchData.getSearchQuery().equals("variable value");
        } catch (DataExtractorException e) {
            assert false;
        }
    }
    
    @Test
    public void testConvertToStdVariable() {
        try {
            PageVariableValue pageVariableValue = initPageVariableValue();
            Object o = DataBuilder.convertToStdVariable(pageVariableValue);
            assert o instanceof String;
            assert o.equals("variable value");
        } catch (DataExtractorException e) {
            e.printStackTrace();
            assert false;
        }
    }
}
