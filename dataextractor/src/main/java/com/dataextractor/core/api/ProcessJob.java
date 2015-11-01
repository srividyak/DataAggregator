package com.dataextractor.core.api;

import com.dataextractor.daos.IPageVariableDAO;
import com.dataextractor.daos.IVariableGroupDAO;
import com.dataextractor.entities.*;
import com.dataextractor.pagescraper.DataBuilder;
import com.dataextractor.pagescraper.DataTranslator;
import com.dataextractor.pagescraper.HtmlScraper;
import com.dataextractor.pagescraper.Scraper;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by srividyak on 11/02/15.
 */
public class ProcessJob implements Callable {

    private Job job;
    
    private String url;
    
    private IPageVariableDAO pageVariableDAO;
    
    private IVariableGroupDAO variableGroupDAO;
    
    private static final Logger LOG = Logger.getLogger(ProcessJob.class);
    
    public ProcessJob(Job job, IPageVariableDAO pageVariableDAO, IVariableGroupDAO variableGroupDAO) {
        this.job = job;
        this.pageVariableDAO = pageVariableDAO;
        this.variableGroupDAO = variableGroupDAO;
        this.url = job.getUrl();
        if (job.getReplacements() != null) {
            for (Map.Entry<String, String> entry : job.getReplacements().entrySet()) {
                url = url.replaceAll(entry.getKey(), entry.getValue());
            }
        }
    }

    public List<Object> process() {
        LOG.debug("in process : url => " + this.url + ", Job => " + job.getId() + ", source => " + job.getSource().getSource());
        List<VariableGroup> variableGroups = variableGroupDAO.getVariableGroups(job);
        List<PageVariable> pageVariables = pageVariableDAO.getPageVariablesWithNoGroup(job);
        Scraper scraper;
        List<Object> scrapedValues = new ArrayList<Object>();
        switch (job.getPageType()) {
            case HTML:
                try {
                    Document document = Jsoup.connect(url).get();
                    LOG.debug("Got data from Job url: ");
                    LOG.debug(document.text());
                    scraper = new HtmlScraper(document);
                    DataTranslator translator = new DataTranslator(scraper, pageVariables, variableGroups);
                    translator.translate();
                    scrapedValues = translator.getScrapedValues();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
                break;
            
            case JSON:
            case XML:
                break;
        }
        return scrapedValues;
    }

    @Override
    public Object call() throws Exception {
        return process();
    }
}
