package zoomapi.cache.databaseData;

import zoomapi.utils.Unit;

import java.util.Map;

public class Channel extends Unit implements CacheUnit {
    private String id;
    private String userId;
    private String channelId;
    private String channelName;
    private String channelType;

    public Channel(){ }

    @Override
    public void setValues(Map<String, String> values) {
        this.id = values.get("id");
        this.userId = values.get("userId");
        this.channelId = values.get("channelId");
        this.channelName = values.get("channelName");
        this.channelType = values.get("channelType");
    }

    public String getId(){
        return this.id;
    }

    public String getUserId(){ return this.userId; }

    public String getChannelId(){
        return this.channelId;
    }

    public String getChannelName(){
        return this.channelName;
    }

    public String getChannelType(){
        return this.channelType;
    }

    @Override
    public String toString() {
        return "id: " + this.getId() + " userId: " + this.getUserId() + " channelId: " + this.getChannelId() + " channelName: " + this.getChannelName() + " channelType: " + this.getChannelType();
    }

}
