package com.dataaggregator.clients.twitter;

import com.dataaggregator.core.IData;
import com.dataaggregator.core.es.entities.Data;
import com.dataaggregator.core.es.entities.twitter.TwitterUserData;
import com.dataaggregator.core.ITwitterUserData;
import com.dataaggregator.core.util.CommonUtils;
import com.dataaggregator.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by srividyak on 02/01/15.
 */
@Component
public class TwitterDataTranslator implements ITwitterDataTranslator {

    @Autowired
    private ITwitterDataProcessor processor;

    @Override
    public void translate(Status status) {
        IData data = getData(status);
        ITwitterUserData twitterUserData = getTwitterUserData(status);
        processor.process(data, twitterUserData);
    }

    private ITwitterUserData getTwitterUserData(Status status) {
        ITwitterUserData twitterUserData = new TwitterUserData();
        twitterUserData.setUser(status.getUser());
        twitterUserData.setCreatedAt(new Date());
        twitterUserData.setUpdatedAt(new Date());
        return twitterUserData;
    }

    private IData getData(Status status) {
        IData data = new Data();
        data.setDescription(status.getText());
        data.setTimestamp(status.getCreatedAt());
        data.setSource(Constants.SOURCE_TWITTER);
        data.setSourceLink(CommonUtils.getTwitterStatusUrl(status.getUser().getScreenName(), status.getId()));
        Map<String, Object> customObject = new HashMap<String, Object>();
        customObject.put("source", status.getSource());
        customObject.put("inReplyToStatusId", status.getInReplyToStatusId());
        customObject.put("inReplyToUserId", status.getInReplyToUserId());
        customObject.put("hashtagEntities", status.getHashtagEntities());
        customObject.put("mediaEntities", status.getMediaEntities());
        customObject.put("symbolEntities", status.getSymbolEntities());
        customObject.put("urlEntities", status.getURLEntities());
        customObject.put("userMentionEntities", status.getUserMentionEntities());
        customObject.put("retweeted", status.isRetweeted());
        customObject.put("retweet", status.isRetweet());
        customObject.put("favorited", status.isFavorited());
        customObject.put("retweetCount", status.getRetweetCount());
        data.setCustomData(customObject);
        return data;
    }
}
