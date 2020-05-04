package zoomapi.utils;

public class Message extends Event{
    private String id;
    private String message;
    private String sender;
    private String date_time;
    private int timestamp;

    public Message(String id, String message, String sender, String date_time, int timestamp) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.date_time = date_time;
        this.timestamp = timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String toString(){
        return ("[ID] " + this.id + " [MESSAGE] " + this.message + " [SENDER] " + this.sender + " [DATE_TIME] " + this.date_time);
    }
}
