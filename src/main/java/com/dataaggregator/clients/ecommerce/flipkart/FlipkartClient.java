package com.dataaggregator.clients.ecommerce.flipkart;

import com.dataaggregator.core.ClientComponent;
import com.dataaggregator.datastore.api.ecommerce.IECommerceDataDao;
import com.dataaggregator.util.Constants;
import com.dataaggregator.clients.ecommerce.ECommerceClient;
import com.dataaggregator.util.PropertyReader;
import com.dataextractor.core.DataExtractor;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * Created by srividyak on 21/02/15.
 */
@ClientComponent(name = "flipkart", propertiesFileName = "flipkart.properties")
public class FlipkartClient extends ECommerceClient {

    private static final Logger LOG = Logger.getLogger(FlipkartClient.class);
    private static final PropertyReader reader = PropertyReader.getInstance();
    private static final String clientName = "flipkart";
    
    private void initProperties() {
        setPollIntervalUnits(reader.getProperty(Constants.FLIPKART_POLLING_INTERVAL_UNIT, "HOURS"));
        setPollInterval(Integer.parseInt(reader.getProperty(Constants.FLIPKART_POLLING_INTERVAL, "24")));
    }
    
    @Override
    public void initialize() {
        initProperties();
        initClient();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public boolean isClientEnabled() {
        return Boolean.parseBoolean(reader.getProperty(Constants.FLIPKART_CLIENT_ENABLED, "true"));
    }
    
    public FlipkartClient(ApplicationContext context) {
        setSourceName(clientName);
        DataExtractor dataExtractor = DataExtractor.getInstance();
        setDataExtractor(dataExtractor);
        IECommerceDataDao dataDao = (IECommerceDataDao) context.getBean(IECommerceDataDao.class);
        setEcommerceDataDao(dataDao);
    }

}
