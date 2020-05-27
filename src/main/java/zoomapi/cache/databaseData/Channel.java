package zoomapi.cache.databaseData;

import zoomapi.utils.Unit;

import java.util.Map;

public class Channel extends Unit implements CacheUnit {
    private String id;
    private String userId;
    private String channelName;
    private String channelId;
    private String channelType;

    public Channel(){

    }

    public String getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void setValue(Map<String, String> value) {

    }
}
