package com.dataaggregator.clients.ecommerce;

import com.dataaggregator.core.PlatformClient;
import com.dataaggregator.core.es.entities.ecommerce.ECommerceProduct;
import com.dataaggregator.core.es.entities.ecommerce.ECommerceSearchData;
import com.dataaggregator.datastore.api.ecommerce.IECommerceDataDao;
import com.dataaggregator.util.Constants;
import com.dataaggregator.util.PropertyReader;
import com.dataextractor.core.DataExtractor;
import com.dataextractor.daos.IJobDAO;
import com.dataextractor.daos.ISourceDAO;
import com.dataextractor.entities.Job;
import com.dataextractor.entities.Source;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by srividyak on 21/02/15.
 */
public abstract class ECommerceClient extends PlatformClient {
    
    private String sourceName;
    private ISourceDAO sourceDAO;
    private IJobDAO jobDAO;
    private Source source;
    private DataExtractor dataExtractor;
    private int pollInterval;
    private String pollIntervalUnits;
    private ScheduledExecutorService scheduledExecutorService;
    private IECommerceDataDao ecommerceDataDao;

    public void setEcommerceDataDao(IECommerceDataDao ecommerceDataDao) {
        this.ecommerceDataDao = ecommerceDataDao;
    }

    private static PropertyReader reader = PropertyReader.getInstance();

    private ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(reader.getProperty(Constants.ECOMMERCE_MAX_MULTIPLE_SEARCHES, "4")));

    public void setPollIntervalUnits(String pollIntervalUnits) {
        this.pollIntervalUnits = pollIntervalUnits;
    }

    public void setPollInterval(int pollInterval) {
        this.pollInterval = pollInterval;
    }

    protected void setDataExtractor(DataExtractor dataExtractor) {
        this.dataExtractor = dataExtractor;
        ISourceDAO sourceDAO = (ISourceDAO) dataExtractor.getBean(ISourceDAO.class);
        IJobDAO jobDAO = (IJobDAO) dataExtractor.getBean(IJobDAO.class);
        setSourceDAO(sourceDAO);
        setJobDAO(jobDAO);
    }

    protected void setJobDAO(IJobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

    private static final Logger LOG = Logger.getLogger(ECommerceClient.class);

    protected void setSourceDAO(ISourceDAO sourceDAO) {
        this.sourceDAO = sourceDAO;
    }

    protected void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public void shutdown() {
        LOG.debug("Shutting down client for source : " + sourceName);
        scheduledExecutorService.shutdown();
    }
    
    protected void initClient() {
        LOG.debug("initializing e-commerce client for source : " + sourceName);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        TimeUnit timeUnit;
        if (pollIntervalUnits.equals("MINS")) {
            timeUnit = TimeUnit.MINUTES;
        } else if (pollIntervalUnits.equals("DAYS")) {
            timeUnit = TimeUnit.DAYS;
        } else {
            timeUnit = TimeUnit.HOURS;
        }
        scheduledExecutorService.scheduleAtFixedRate(new ECommerceClientMasterJobsInternal(), 0, pollInterval, timeUnit);
    }
    
    protected void processMasterJobs() {
        source = sourceDAO.getSource(sourceName);
        if (source != null) {
            LOG.debug("Getting jobs for source : " + sourceName);
            List<Job> jobs = jobDAO.getMasterJobs(source);
            List<Object> objects = dataExtractor.extract(jobs);
            for (Object object : objects) {
                List<ECommerceSearchData> searchDataList = (List<ECommerceSearchData>) object;
                LOG.debug("processing search data list of size : " + searchDataList.size());
                for (ECommerceSearchData searchData : searchDataList) {
                    LOG.debug("search query : " + searchData.getSearchQuery());
                    executorService.submit(new ECommerceClientJobInternal(searchData));
                }
            }
        }
    }

    protected void processSearchData(ECommerceSearchData searchData) {
        int page = 0;
        String searchQuery = searchData.getSearchQuery().trim();
        if (!searchQuery.isEmpty()) {
            List<Job> nonMasterJobs = jobDAO.getNonMasterJobs(source);
            // assuming only one non master job exists for a given search data (search page)
            Job job = nonMasterJobs.get(0);
            String url = job.getUrl();
            String searchQueryPlaceholder = reader.getProperty(Constants.ECOMMERCE_SEARCH_QUERY);
            String nextIndexPlaceholder = reader.getProperty(Constants.ECOMMERCE_NEXT_INDEX);
            while (true) {
                Map<String, String> replacements = new HashMap<String, String>();
                if (url.indexOf(searchQueryPlaceholder) != -1) {
                    replacements.put(searchQueryPlaceholder, searchQuery);
                }
                if (url.indexOf(nextIndexPlaceholder) != -1) {
                    replacements.put(nextIndexPlaceholder, String.valueOf(page++));
                }
                job.setReplacements(replacements);
                List<Object> objects = dataExtractor.extract(job);
                for (Object o : objects) {
                    ECommerceProduct product = (ECommerceProduct) o;
                    product.setSource(sourceName);
                    ecommerceDataDao.save(product);
                }
                if (!job.getIsPaginated() || objects.size() == 0) {
                    break;
                }
            }
        }
    }

    class ECommerceClientJobInternal implements Runnable {
        
        private ECommerceSearchData searchData;

        public ECommerceClientJobInternal(ECommerceSearchData searchData) {
            this.searchData = searchData;
        }

        @Override
        public void run() {
            processSearchData(searchData);
        }
    }
    
    class ECommerceClientMasterJobsInternal implements Runnable {

        @Override
        public void run() {
            processMasterJobs();
        }
    }
}
