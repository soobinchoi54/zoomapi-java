package zoomapi.cache.databaseData;

import zoomapi.utils.Member;

import java.util.Map;

public class ChannelMember implements CacheUnit {
    private String id;
    private String userId;
    private String channelId;

    public ChannelMember(){}

    public String getId(){
        return this.id;
    }

    @Override
    public void setValue(Map<String, String> value) {

    }
}
