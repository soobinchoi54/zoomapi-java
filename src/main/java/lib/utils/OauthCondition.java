package lib.utils;

import lib.cache.databaseData.ChannelMessage;

public interface OauthCondition {
    boolean isTrue(ChannelMessage message);
}
