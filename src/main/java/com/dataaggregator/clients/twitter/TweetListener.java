package com.dataaggregator.clients.twitter;

import com.twitter.hbc.twitter4j.handler.StatusStreamHandler;
import com.twitter.hbc.twitter4j.message.DisconnectMessage;
import com.twitter.hbc.twitter4j.message.StallWarningMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

/**
 * Created by srividyak on 01/01/15.
 */
@Component
public class TweetListener implements StatusStreamHandler {

    @Autowired
    private ITwitterDataTranslator translator;

    private static final Logger LOG = Logger.getLogger(TweetListener.class);

    @Override
    public void onDisconnectMessage(DisconnectMessage message) {

    }

    @Override
    public void onStallWarningMessage(StallWarningMessage warning) {

    }

    @Override
    public void onUnknownMessageType(String msg) {

    }

    @Override
    public void onStatus(Status status) {
        LOG.debug(status.getText());
        translator.translate(status);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {

    }

    @Override
    public void onStallWarning(StallWarning warning) {

    }

    @Override
    public void onException(Exception ex) {

    }
}
