package com.dataextractor.pagescraper;

import com.dataextractor.entities.PageVariable;
import com.dataextractor.entities.PageVariableValue;
import com.dataextractor.entities.VariableGroup;
import com.dataextractor.entities.VariableGroupValue;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srividyak on 26/01/15.
 */
public class HtmlScraper implements Scraper {
    
    private Document pageDocument;
    
    private static final Logger LOG = Logger.getLogger(HtmlScraper.class);
    
    public HtmlScraper(Document document) {
        pageDocument = document;
    }
    
    @Override
    public List<PageVariableValue> getPageVariables(PageVariable variable) {
        Elements elements = pageDocument.select(variable.getXpath());
        List<PageVariableValue> values =  new ArrayList<PageVariableValue>();
        if (elements != null) {
            for (Element element : elements) {
                String attribute = variable.getAttribute();
                PageVariableValue value;
                if (attribute != null) {
                    value = new PageVariableValue(variable, element.attr(attribute));
                } else {
                    value = new PageVariableValue(variable, element.text());
                }
                values.add(value);
            }
        }
        return values;
    }

    @Override
    public List<VariableGroupValue> getPageVariableGroups(VariableGroup variableGroup) {
        String groupingXpath = variableGroup.getGroupingXpath();
        List<VariableGroupValue> variableGroupValues = new ArrayList<VariableGroupValue>();
        if (groupingXpath != null) {
            Elements elements = pageDocument.select(groupingXpath);
            List<PageVariable> pageVariables = variableGroup.getPageVariables();
            if (elements != null) {
                for (Element element : elements) {
                    VariableGroupValue variableGroupValue = new VariableGroupValue();
                    variableGroupValue.setGroupId(variableGroup.getGroupId());
                    variableGroupValue.setGroupDataType(variableGroup.getGroupDataType());
                    List<PageVariableValue> pageVariableValues = new ArrayList<PageVariableValue>();
                    for (PageVariable pageVariable : pageVariables) {
                        Elements node = element.select(pageVariable.getXpath());
                        PageVariableValue variableValue = new PageVariableValue(pageVariable);
                        if (node != null) {
                            String attribute = pageVariable.getAttribute();
                            if (attribute != null) {
                                variableValue.setVariableValue(node.attr(attribute));
                            } else {
                                variableValue.setVariableValue(node.text());
                            }
                            pageVariableValues.add(variableValue);
                        }
                    }
                    variableGroupValue.setVariableValues(pageVariableValues);
                    variableGroupValues.add(variableGroupValue);
                }
            }
        }
        return variableGroupValues;
    }
}
