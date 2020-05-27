package zoomapi.cache.databaseData;

import zoomapi.utils.Message;

import java.util.Map;

public class ChannelMessage implements CacheUnit {
    private String id;
    private String userId;
    private String channelId;

    public ChannelMessage() {}

    public String getId(){
        return this.id;
    }

    @Override
    public void setValue(Map<String, String> value) {

    }
}
