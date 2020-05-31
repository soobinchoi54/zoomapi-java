package lib.clients;

import lib.components.*;
import lib.utils.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class ZoomClient extends ApiClient {
    // Zoom api client
    protected Map<String, BaseComponent> components = new HashMap<>();
    protected ZoomClient(String api_key, String api_secret) {
        super("https://api.zoom.us/v2", 1500);
        this.config.put("api_key", api_key);
        this.config.put("api_secret", api_secret);
        this.config.put("data_type", "json");

        this.components.put("meeting", new MeetingComponent(this.base_uri, this.config));
        this.components.put("report", new RecordingComponent(this.base_uri, this.config));
        this.components.put("user", new UserComponent(this.base_uri, this.config));
        this.components.put("webinar", new WebinarComponent(this.base_uri, this.config));
        this.components.put("recording", new ReportComponent(this.base_uri, this.config));

    }
    protected ZoomClient(String api_key, String api_secret, String data_type, int timeout){
        super("https://api.zoom.us/v2", timeout);
        this.config.put("api_key", api_key);
        this.config.put("api_secret", api_secret);
        this.config.put("data_type", data_type);

        this.components.put("meeting", new MeetingComponent(this.base_uri, this.config));
        this.components.put("report", new RecordingComponent(this.base_uri, this.config));
        this.components.put("user", new UserComponent(this.base_uri, this.config));
        this.components.put("webinar", new WebinarComponent(this.base_uri, this.config));
        this.components.put("recording", new ReportComponent(this.base_uri, this.config));
    }

    private void refreshToken(){
        // to be implemented
    }

    protected String getApiKey(){
        return this.config.get("api_key");
    }

    protected void setApiKey(String api_key){
        this.config.put("api_key", api_key);
        this.refreshToken();
    }

    protected String getApiSecret(){
        return this.config.get("api_secret");
    }

    protected void setApiSecret(String api_secret){
        this.config.put("api_secret", api_secret);
        this.refreshToken();
    }

    public BaseComponent getMeeting(){
        return this.components.get("meeting");
    }

    public BaseComponent getReport(){
        return this.components.get("report");
    }

    public BaseComponent getUser(){
        return this.components.get("user");
    }

    public BaseComponent getWebinar(){
        return this.components.get("webinar");
    }

    public BaseComponent getRecording(){
        return this.components.get("recording");
    }
}
