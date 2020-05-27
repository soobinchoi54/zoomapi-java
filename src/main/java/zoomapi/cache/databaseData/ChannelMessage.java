package zoomapi.cache.databaseData;

import zoomapi.utils.Message;
import zoomapi.utils.Unit;

import java.util.Map;

public class ChannelMessage extends Unit implements CacheUnit {
    private String id;
    private String userId;
    private String channelId;
    private String messageId;
    private String message;
    private String sender;
    private String dateTime;

    public ChannelMessage() {}

    @Override
    public void setValues(Map<String, String> values) {
        this.id = values.get("id");
        this.userId = values.get("userId");
        this.channelId = values.get("channelId");
        this.messageId = values.get("messageId");
        this.message = values.get("message");
        this.sender = values.get("sender");
        this.dateTime = values.get("dateTime");
    }

    public String getId(){return this.id;}

    public String getUserId(){return this.userId;}

    public String getChannelId(){return this.channelId;}

    public String getMessageId(){return this.messageId;}

    public String getMessage(){return this.message;}

    public String getSender(){return this.sender;}

    public String getDateTime(){return this.dateTime;}

    @Override
    public String toString() {
        return "id: " + this.getId() + " userId: " + this.getUserId() + " channelId: " + this.getChannelId() + " messageId: " + this.getMessageId() + " message: " + this.getMessage() + " sender: " + this.getSender() + " dataTime: " + this.getDateTime();
    }
}
