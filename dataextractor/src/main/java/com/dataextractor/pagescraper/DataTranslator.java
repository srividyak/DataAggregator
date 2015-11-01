package com.dataextractor.pagescraper;

import com.dataextractor.core.DataExtractorException;
import com.dataextractor.entities.PageVariable;
import com.dataextractor.entities.PageVariableValue;
import com.dataextractor.entities.VariableGroup;
import com.dataextractor.entities.VariableGroupValue;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by srividyak on 25/02/15.
 */
public class DataTranslator {
    
    private Scraper scraper;
    private List<PageVariable> pageVariables;
    private List<VariableGroup> variableGroups;
    private List<Object> scrapedValues;
    private static final Logger LOG = Logger.getLogger(DataTranslator.class);

    public DataTranslator(Scraper scraper, List<PageVariable> pageVariables, List<VariableGroup> variableGroups) {
        this.scraper = scraper;
        this.pageVariables = pageVariables;
        this.variableGroups = variableGroups;
        scrapedValues = new ArrayList<Object>();
    }

    public void translate() {
        for (VariableGroup variableGroup : variableGroups) {
            List<VariableGroupValue>  vgValues = scraper.getPageVariableGroups(variableGroup);
            LOG.debug("For variable group : " + variableGroup.getGroupingXpath());
            for (VariableGroupValue variableGroupValue : vgValues) {
                try {
                    Object o = DataBuilder.convertToStdVariable(variableGroupValue);
                    scrapedValues.add(o);
                } catch (DataExtractorException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public List<Object> getScrapedValues() {
        return scrapedValues;
    }

}
