package com.twitter.hbc.twitter4j;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.twitter.hbc.SitestreamController;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.StatsReporter;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.twitter4j.handler.StatusStreamHandler;
import com.twitter.hbc.twitter4j.message.DisconnectMessage;
import com.twitter.hbc.twitter4j.message.StallWarningMessage;
import com.twitter.hbc.twitter4j.parser.JSONObjectParser;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.twitter.hbc.twitter4j.parser.JSONObjectParser.parseFriendList;

/**
 * Created by srividyak on 01/01/15.
 */
public class KafkaTwitter4jStatusClient implements Twitter4jClient {

    private final static Logger logger = Logger.getLogger(KafkaTwitter4jStatusClient.class);
    private final Client client;
    private final PublicObjectFactory factory;
    private final List<? extends StatusListener> statusListeners;
    private final ExecutorService executorService;
    private final List<KafkaStream<byte[], byte[]>> kafkaStreams;

    public KafkaTwitter4jStatusClient(Client client, List<? extends StatusListener> listeners, ExecutorService executorService, List<KafkaStream<byte[], byte[]>> streams) {
        this.client = client;
        this.factory = new PublicObjectFactory(new ConfigurationBuilder().build());
        this.statusListeners = ImmutableList.copyOf(listeners);
        this.executorService = executorService;
        this.kafkaStreams = streams;
    }

    @Override
    public void process() {
        logger.debug("in process: " + kafkaStreams.size());
        for (final KafkaStream stream : kafkaStreams) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
                    while (iterator.hasNext()) {
                        String tweetMsg = new String(iterator.next().message());
                        logger.debug(tweetMsg);
                        try {
                            parseMessage(tweetMsg);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            };
            this.executorService.submit(runnable);
        }

    }

    @Override
    public void connect() {
        client.connect();
    }

    @Override
    public void reconnect() {
        client.reconnect();
    }

    @Override
    public void stop() {
        client.stop();
        executorService.shutdown();
    }

    @Override
    public void stop(int waitMillis) {
        client.stop(waitMillis);
        executorService.shutdown();
    }

    @Override
    public boolean isDone() {
        return client.isDone();
    }

    @Override
    public String getName() {
        return client.getName();
    }

    @Override
    public StreamingEndpoint getEndpoint() {
        return client.getEndpoint();
    }

    @Override
    public SitestreamController createSitestreamController() {
        return client.createSitestreamController();
    }

    @Override
    public StatsReporter.StatsTracker getStatsTracker() {
        return client.getStatsTracker();
    }

    protected void parseMessage(String msg) throws JSONException, TwitterException, IOException {
        JSONObject json = new JSONObject(msg);
        long sitestreamUser = getSitestreamUser(json);
        processMessage(sitestreamUser, preprocessMessage(json));
    }

    /**
     * @return the user id of the message if its for a sitestreams connection. -1 otherwise
     */
    protected long getSitestreamUser(JSONObject json) throws JSONException {
        return -1;
    }

    /**
     * Removes the sitestreams envelope, if necessary
     */
    protected JSONObject preprocessMessage(JSONObject json) throws JSONException {
        return json;
    }

    @VisibleForTesting
    void processMessage(long sitestreamUser, JSONObject json) throws JSONException, TwitterException, IOException {
        JSONObjectType.Type type = JSONObjectType.determine(json);
        switch (type) {
            case STATUS:
                processStatus(sitestreamUser, json);
                break;
            case LIMIT:
                processLimit(sitestreamUser, json);
                break;
            case DELETE:
                processDelete(sitestreamUser, json);
                break;
            case SCRUB_GEO:
                processScrubGeo(sitestreamUser, json);
                break;
            case DIRECT_MESSAGE:
            case SENDER:
                processDirectMessage(sitestreamUser, json);
                break;
            case FRIENDS:
                processFriends(sitestreamUser, json);
                break;
            case FAVORITE:
                processFavorite(sitestreamUser, json);
                break;
            case UNFAVORITE:
                processUnfavorite(sitestreamUser, json);
                break;
            case FOLLOW:
                processFollow(sitestreamUser, json);
                break;
            case UNFOLLOW:
                processUnfollow(sitestreamUser, json);
                break;
            case USER_LIST_MEMBER_ADDED:
                processUserListMemberAddition(sitestreamUser, json);
                break;
            case USER_LIST_MEMBER_DELETED:
                processUserListMemberDeletion(sitestreamUser, json);
                break;
            case USER_LIST_SUBSCRIBED:
                processUserListSubscription(sitestreamUser, json);
                break;
            case USER_LIST_UNSUBSCRIBED:
                processUserListUnsubscription(sitestreamUser, json);
                break;
            case USER_LIST_CREATED:
                processUserListCreation(sitestreamUser, json);
                break;
            case USER_LIST_UPDATED:
                processUserListUpdated(sitestreamUser, json);
                break;
            case USER_LIST_DESTROYED:
                processUserListDestroyed(sitestreamUser, json);
                break;
            case BLOCK:
                processBlock(sitestreamUser, json);
                break;
            case UNBLOCK:
                processUnblock(sitestreamUser, json);
                break;
            case USER_UPDATE:
                processUserUpdate(sitestreamUser, json);
                break;
            case DISCONNECTION:
                processDisconnectMessage(json);
                break;
            case STALL_WARNING:
                processStallWarning(json);
                break;
            case UNKNOWN:
            default:
                if (JSONObjectParser.isRetweetMessage(json)) {
                    processRetweet(sitestreamUser, json);
                } else if (JSONObjectParser.isControlStreamMessage(json)) {
                    processControlStream(json);
                } else {
                    onUnknownMessageType(json.toString());
                }
        }
    }

    private void processStatus(long sitestreamUser, JSONObject json) throws TwitterException {
        Status status = factory.createStatus(json);
        onStatus(sitestreamUser, status);
    }

    private void processDirectMessage(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        DirectMessage dm = factory.newDirectMessage(json.getJSONObject("direct_message"));
        onDirectMessage(sitestreamUser, dm);
    }

    private void processDelete(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        JSONObject deletionNotice = json.getJSONObject("delete");
        if (deletionNotice.has("status")) {
            onDelete(sitestreamUser, JSONObjectParser.parseStatusDelete(json));
        } else if (deletionNotice.has("direct_message")) {
            JSONObject dm = deletionNotice.getJSONObject("direct_message");
            final long statusId = dm.getLong("id");
            final long userId = dm.getLong("user_id");
            onDeleteDirectMessage(sitestreamUser, statusId, userId);
        }
    }

    private void processStallWarning(JSONObject json) throws JSONException {
        JSONObject warning = json.getJSONObject("warning");
        String code = ((String) warning.opt("code"));
        String message = ((String) warning.opt("message"));
        int percentFull = warning.getInt("percent_full");

        onStallWarning(new StallWarningMessage(code, message, percentFull));
    }

    private void processLimit(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        onTrackLimitationNotice(sitestreamUser, JSONObjectParser.parseTrackLimit(json));
    }

    private void processScrubGeo(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        JSONObject scrubGeo = json.getJSONObject("scrub_geo");
        long userId = scrubGeo.getLong("user_id");
        long upToStatusId = scrubGeo.getLong("up_to_status_id");
        onScrubGeo(sitestreamUser, userId, upToStatusId);
    }

    private void processFriends(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        onFriends(sitestreamUser, parseFriendList(json));
    }

    private void processFavorite(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        User target = factory.createUser(JSONObjectParser.parseEventTarget(json));
        Status status = factory.createStatus(JSONObjectParser.parseEventTargetObject(json));
        onFavorite(sitestreamUser, source, target, status);
    }

    private void processUnfavorite(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        User target = factory.createUser(JSONObjectParser.parseEventTarget(json));
        Status status = factory.createStatus(JSONObjectParser.parseEventTargetObject(json));
        onUnfavorite(sitestreamUser, source, target, status);
    }

    private void processRetweet(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        User target = factory.createUser(JSONObjectParser.parseEventTarget(json));
        Status status = factory.createStatus(JSONObjectParser.parseEventTargetObject(json));
        onRetweet(sitestreamUser, source, target, status);
    }

    private void processFollow(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        User target = factory.createUser(JSONObjectParser.parseEventTarget(json));
        onFollow(sitestreamUser, source, target);
    }

    private void processUnfollow(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        User target = factory.createUser(JSONObjectParser.parseEventTarget(json));
        onUnfollow(sitestreamUser, source, target);
    }

    private void processUserListMemberAddition(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User addedUser = factory.createUser(JSONObjectParser.parseEventSource(json));
        User owner = factory.createUser(JSONObjectParser.parseEventTarget(json));
        UserList userList = factory.createAUserList(JSONObjectParser.parseEventTargetObject(json));
        onUserListMemberAddition(sitestreamUser, addedUser, owner, userList);
    }

    private void processUserListMemberDeletion(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User deletedMember = factory.createUser(JSONObjectParser.parseEventSource(json));
        User owner = factory.createUser(JSONObjectParser.parseEventTarget(json));
        UserList userList = factory.createAUserList(JSONObjectParser.parseEventTargetObject(json));
        onUserListMemberDeletion(sitestreamUser, deletedMember, owner, userList);
    }

    private void processUserListSubscription(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        User owner = factory.createUser(JSONObjectParser.parseEventTarget(json));
        UserList userList = factory.createAUserList(JSONObjectParser.parseEventTargetObject(json));
        onUserListSubscription(sitestreamUser, source, owner, userList);
    }

    private void processUserListUnsubscription(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        User owner = factory.createUser(JSONObjectParser.parseEventTarget(json));
        UserList userList = factory.createAUserList(JSONObjectParser.parseEventTargetObject(json));
        onUserListUnsubscription(sitestreamUser, source, owner, userList);
    }

    private void processUserListCreation(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        UserList userList = factory.createAUserList(JSONObjectParser.parseEventTargetObject(json));
        onUserListCreation(sitestreamUser, source, userList);
    }

    private void processUserListUpdated(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        UserList userList = factory.createAUserList(JSONObjectParser.parseEventTargetObject(json));
        onUserListUpdate(sitestreamUser, source, userList);
    }

    private void processUserListDestroyed(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        UserList userList = factory.createAUserList(JSONObjectParser.parseEventTargetObject(json));
        onUserListDeletion(sitestreamUser, source, userList);
    }

    private void processUserUpdate(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        onUserProfileUpdate(sitestreamUser, factory.createUser(JSONObjectParser.parseEventSource(json)));
    }

    private void processBlock(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        User target = factory.createUser(JSONObjectParser.parseEventTarget(json));
        onBlock(sitestreamUser, source, target);
    }

    private void processUnblock(long sitestreamUser, JSONObject json) throws TwitterException, JSONException {
        User source = factory.createUser(JSONObjectParser.parseEventSource(json));
        User target = factory.createUser(JSONObjectParser.parseEventTarget(json));
        onUnblock(sitestreamUser, source, target);
    }

    private void processControlStream(JSONObject json) throws JSONException {
        onControlStreamMessage(JSONObjectParser.getStreamId(json));
    }

    private void processDisconnectMessage(JSONObject json) throws JSONException {
        onDisconnectMessage(JSONObjectParser.parseDisconnectMessage(json));
    }

    protected void onStatus(long sitestreamUser, final Status status) {
        for (StatusListener listener : statusListeners) {
            listener.onStatus(status);
        }
    }

    protected void onDelete(long sitestreamUser, StatusDeletionNotice delete) {
        for (StatusListener listener : statusListeners) {
            listener.onDeletionNotice(delete);
        }
    }

    protected void onTrackLimitationNotice(long sitestreamUser, final int limit) {
        for (StatusListener listener : statusListeners) {
            listener.onTrackLimitationNotice(limit);
        }
    }

    protected void onScrubGeo(long sitestreamUser, long userId, long upToStatusId) {
        for (StatusListener listener : statusListeners) {
            listener.onScrubGeo(userId, upToStatusId);
        }
    }

    protected void onDeleteDirectMessage(long sitestreamUser, long directMessageId, long userId) {
        logger.info("Unhandled event: onDeleteDirectMessage");
    }

    protected void onDirectMessage(long sitestreamUser, final DirectMessage directMessage) {
        logger.info("Unhandled event: onDirectMessage");
    }

    protected void onFriends(long sitestreamUser, final long[] json) {
        logger.info("Unhandled event: onFriends");
    }

    protected void onFavorite(long sitestreamUser, final User source, final User target, final Status targetObject) {
        logger.info("Unhandled event: onFavorite");
    }

    protected void onUnfavorite(long sitestreamUser, final User source, final User target, final Status targetObject) {
        logger.info("Unhandled event: onUnfavorite");
    }

    protected void onRetweet(long sitestreamUser, User source, User target, Status tweet) {
        logger.info("Unhandled event: onRetweet");
    }

    protected void onFollow(long sitestreamUser, final User source, final User target) throws TwitterException {
        logger.info("Unhandled event: onFollow");
    }

    protected void onUnfollow(long sitestreamUser, final User source, final User target) throws TwitterException {
        logger.info("Unhandled event: onUnfollow");
    }

    protected void onUserListMemberAddition(long sitestreamUser, final User addedMember, final User owner, final UserList userList) {
        logger.info("Unhandled event: onUserListMemberAddition");
    }

    protected void onUserListMemberDeletion(long sitestreamUser, final User deletedMember, final User owner, final UserList userList) {
        logger.info("Unhandled event: onUserListMemberDeletion");
    }

    protected void onUserListSubscription(long sitestreamUser, final User subscriber, final User owner, final UserList userList) {
        logger.info("Unhandled event: onUserListSubscription");
    }

    protected void onUserListUnsubscription(long sitestreamUser, final User deletedMember, final User owner, final UserList userList) {
        logger.info("Unhandled event: onUserListUnsubscription");
    }

    protected void onUserListCreation(long sitestreamUser, final User source, final UserList userList) {
        logger.info("Unhandled event: onUserListCreation");
    }

    protected void onUserListUpdate(long sitestreamUser, User source, UserList userList) {
        logger.info("Unhandled event: onUserListUpdate");
    }

    protected void onUserListDeletion(long sitestreamUser, final User source, final UserList userList) {
        logger.info("Unhandled event: onUserListDeletion");
    }

    protected void onUserProfileUpdate(long sitestreamUser, User source) {
        logger.info("Unhandled event: onUserProfileUpdate");
    }

    protected void onBlock(long sitestreamUser, User source, User target) {
        logger.info("Unhandled event: onBlock");
    }

    protected void onUnblock(long sitestreamUser, User source, User target) {
        logger.info("Unhandled event: onUnblock");
    }

    protected void onControlStreamMessage(String streamId) {
        logger.info("Unhandled event: onControlStreamMessage");
    }

    protected void onDisconnectMessage(DisconnectMessage disconnectMessage) {
        for (StatusListener listener : statusListeners) {
            if (listener instanceof StatusStreamHandler) {
                ((StatusStreamHandler) listener).onDisconnectMessage(disconnectMessage);
            }
        }
    }

    protected void onException(Exception e) {
        logger.info("Exception caught", e);
        for (StatusListener listener : statusListeners) {
            listener.onException(e);
        }
    }

    protected void onStallWarning(StallWarningMessage stallWarning) {
        for (StatusListener listener : statusListeners) {
            if (listener instanceof StatusStreamHandler) {
                ((StatusStreamHandler) listener).onStallWarningMessage(stallWarning);
            }
        }
    }

    protected void onUnknownMessageType(String msg) {
        for (StatusListener listener : statusListeners) {
            if (listener instanceof StatusStreamHandler) {
                ((StatusStreamHandler) listener).onUnknownMessageType(msg);
            }
        }
    }
}
