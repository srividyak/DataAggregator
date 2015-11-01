package com.dataaggregator.core.util;

import com.dataaggregator.util.Constants;

/**
 * Created by srividyak on 02/01/15.
 */
public class CommonUtils {

    public static String getTwitterStatusUrl(String username, long statusId) {
        String baseUrl = Constants.TWITTER_BASE_URL;
        String url = baseUrl + username + "/" + Constants.TWITTER_STATUS_STRING + "/" + statusId;
        return url;
    }

}
