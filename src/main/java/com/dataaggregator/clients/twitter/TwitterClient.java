package com.dataaggregator.clients.twitter;

import com.dataaggregator.core.ClientComponent;
import com.dataaggregator.core.PlatformClient;
import com.dataaggregator.util.Constants;
import com.dataaggregator.util.PropertyReader;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * Created by srividyak on 04/01/15.
 * TwitterClient => TwitterTrendsClient => TwitterPollingClient
 */
@ClientComponent( name = "twitter", propertiesFileName = "twitter.properties")
public class TwitterClient extends PlatformClient {

    private TwitterTrendsClient trendsClient;

    private static final Logger LOG = Logger.getLogger(TwitterClient.class);
    private static final PropertyReader reader = PropertyReader.getInstance();

    @Override
    public void initialize() {
        try {
            if (isClientEnabled()) {
                trendsClient.initialize();
            }
        } catch (TwitterClientException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        try {
            if (isClientEnabled()) {
                trendsClient.shutdown();
            }
        } catch (TwitterClientException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean isClientEnabled() {
        return Boolean.parseBoolean(reader.getProperty(Constants.TWITTER_CLIENT_ENABLED, "true"));
    }

    public TwitterClient(ApplicationContext context) {
        trendsClient = context.getBean(TwitterTrendsClient.class);
    }

}
