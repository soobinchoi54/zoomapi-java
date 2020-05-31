package lib.cache.databaseData;

import lib.utils.Unit;

import java.util.Map;

public class Channel extends Unit implements CacheUnit {
    private String id;
    private String clientId;
    private String channelId;
    private String channelName;
    private String channelType;

    public Channel(){ }

    @Override
    public void setValues(Map<String, String> values) {
        this.id = values.getOrDefault("id", null);
        this.clientId = values.getOrDefault("clientId", null);
        this.channelId = values.getOrDefault("channelId", null);
        this.channelName = values.getOrDefault("channelName", null);
        this.channelType = values.getOrDefault("channelType", null);
    }

    /*********************************************************************
     * After careful consideration, getId methods should not be provided,
     * since id indicates the sensitive data in the cache system
     *********************************************************************/
    private String getId(){
        return this.id;
    }

    public String getClientId(){ return this.clientId; }

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
        return " clientId: " + this.getClientId() + " channelId: " + this.getChannelId() + " channelName: " + this.getChannelName() + " channelType: " + this.getChannelType();
    }

}
