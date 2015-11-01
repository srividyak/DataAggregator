package com.dataaggregator.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * Created by srividyak on 01/01/15.
 */
public class PropertyReader {
    private Map<String, String> propertyMap = new HashMap();
    private static PropertyReader INSTANCE = null;
    private static final Logger LOG = Logger.getLogger(PropertyReader.class);
    private static String PROPERTIES_FILE_EXT = ".properties";
    private PropertyReader() {
        loadProperties();
    }

    /**
     * This API can be used to reload property files without any need to restart the service
     */
    public void reload() {
        loadProperties();
    }

    private void loadProperties() {
        synchronized (propertyMap) {
            try {
                String configPath = System.getProperty("config.path", "config" + File.separator);
                File folder = new File(configPath);
                final File[] propFiles = folder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(PROPERTIES_FILE_EXT);
                    }
                });
                for (File propFile : propFiles) {
                    Properties prop = new Properties();
                    FileInputStream in = new FileInputStream(propFile);
                    prop.load(in);
                    propertyMap.putAll((Hashtable)prop);
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public String getProperty(String key, String defaultValue) {
        return propertyMap.containsKey(key) ? propertyMap.get(key) : defaultValue;
    }

    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public static PropertyReader getInstance() {
        if (INSTANCE == null) {
            synchronized (PropertyReader.class) {
                if(INSTANCE == null){
                    INSTANCE = new PropertyReader();
                }
            }
        }
        return INSTANCE;
    }
}
