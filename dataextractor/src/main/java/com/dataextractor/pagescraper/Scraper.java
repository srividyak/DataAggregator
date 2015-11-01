package com.dataextractor.pagescraper;

import com.dataextractor.entities.PageVariable;
import com.dataextractor.entities.PageVariableValue;
import com.dataextractor.entities.VariableGroup;
import com.dataextractor.entities.VariableGroupValue;

import java.util.List;

/**
 * Created by srividyak on 26/01/15.
 */
public interface Scraper {
    
    List<PageVariableValue> getPageVariables(PageVariable variable);
    
    List<VariableGroupValue> getPageVariableGroups(VariableGroup variableGroup);
}
