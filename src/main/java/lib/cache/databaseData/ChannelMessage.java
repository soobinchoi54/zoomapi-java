package lib.cache.databaseData;

import java.util.Map;

public class ChannelMessage extends Unit implements CacheUnit {
    private String id;
    private String clientId;
    private String channelId;
    private String messageId;
    private String message;
    private String sender;
    private String dateTime;

    public ChannelMessage() {}

    @Override
    public void setValues(Map<String, String> values) {
        this.id = values.getOrDefault("id", null);
        this.clientId = values.getOrDefault("clientId", null);
        this.channelId = values.getOrDefault("channelId", null);
        this.messageId = values.getOrDefault("messageId", null);
        this.message = values.getOrDefault("message", null);
        this.sender = values.getOrDefault("sender", null);
        this.dateTime = values.getOrDefault("dateTime", null);
    }

    /*********************************************************************
     * After careful consideration, getId methods should not be provided,
     * since id indicates the sensitive data in the cache system
     *********************************************************************/
    private String getId(){return this.id;}

    public String getClientId(){return this.clientId;}

    public String getChannelId(){return this.channelId;}

    public String getMessageId(){return this.messageId;}

    public String getMessage(){return this.message;}

    public String getSender(){return this.sender;}

    public String getDateTime(){return this.dateTime;}

    @Override
    public String toString() {
        return " clientId: " + this.getClientId() + " channelId: " + this.getChannelId() + " messageId: " + this.getMessageId() + " message: " + this.getMessage() + " sender: " + this.getSender() + " dateTime: " + this.getDateTime();
    }
}
