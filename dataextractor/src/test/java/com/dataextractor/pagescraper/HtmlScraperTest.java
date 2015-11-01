package com.dataextractor.pagescraper;

import com.dataextractor.entities.PageVariable;
import com.dataextractor.entities.PageVariableValue;
import com.dataextractor.entities.VariableGroup;
import com.dataextractor.entities.VariableGroupValue;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srividyak on 26/01/15.
 */
public class HtmlScraperTest {
    
    Scraper searchPageScraper;
    Scraper mainPageScraper;
    
    @Before
    public void setup() {
        String url = "http://www.flipkart.com/search?q=redmi";
        String mainPageUrl = "http://www.flipkart.com";
        Document document = null, mainPageDocument = null;
        try {
            document = Jsoup.connect(url).get();
            mainPageDocument = Jsoup.connect(mainPageUrl).get();
            searchPageScraper = new HtmlScraper(document);
            mainPageScraper = new HtmlScraper(mainPageDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testMainPageVariableGroups() {
        PageVariable pageVariable = new PageVariable("a", "searchQuery", "java.lang.String");
        List<PageVariable> pageVariables = new ArrayList<PageVariable>();
        pageVariables.add(pageVariable);
        VariableGroup variableGroup = new VariableGroup(pageVariables, ".goquickly-list li", "group_id", "com.dataaggregator.core.es.entities.ecommerce.ECommerceSearchData");
        List<VariableGroupValue> groupValues = mainPageScraper.getPageVariableGroups(variableGroup);
        for (VariableGroupValue value : groupValues) {
            System.out.println(new Gson().toJson(value));
        }
        assert groupValues.size() > 0;
    }
    
    @Test
    public void testVariableGroups() {
        List<PageVariable> pageVariables = getPageVariablesForFlipkart();
        VariableGroup variableGroup = new VariableGroup(pageVariables, "#products .gd-row.browse-grid-row .gd-col", "groupId", "EcommerceData");
        List<VariableGroupValue> groupValues = searchPageScraper.getPageVariableGroups(variableGroup);
        assert groupValues.size() > 0;
        for (VariableGroupValue groupValue : groupValues) {
            List<PageVariableValue> values = groupValue.getVariableValues();
            for (PageVariableValue value : values) {
//                System.out.println(new Gson().toJson(value));
                assert value.getVariableValue() != null;
            }
        }
    }
    
    private List<PageVariable> getPageVariablesForFlipkart() {
        List<PageVariable> pageVariables = new ArrayList<PageVariable>();
        
        //img
        PageVariable pageVariable = new PageVariable();
        pageVariable.setXpath(".pu-visual-section img");
        pageVariable.setVariableId("img");
        pageVariable.setVariableDataType("java.lang.String");
        pageVariable.setAttribute("data-src");
        pageVariables.add(pageVariable);
        
        //title
        pageVariable = new PageVariable();
        pageVariable.setXpath(".pu-details a");
        pageVariable.setVariableId("title");
        pageVariable.setVariableDataType("java.lang.String");
        pageVariables.add(pageVariable);
        return pageVariables;
    }
}
