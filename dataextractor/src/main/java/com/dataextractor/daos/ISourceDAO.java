package com.dataextractor.daos;

import com.dataextractor.entities.Source;

import java.util.List;

/**
 * Created by srividyak on 21/02/15.
 */
public interface ISourceDAO {
    
    List<Source> getSources();
    
    List<Source> getSources(Source.type type);
    
    Source getSource(String source);

}
