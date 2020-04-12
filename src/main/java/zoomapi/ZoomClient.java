package zoomapi;

import zoomapi.utils.ApiClient;
import zoomapi.components.MeetingComponent;
import zoomapi.components.RecordingComponent;
import zoomapi.components.ReportComponent;
import zoomapi.components.UserComponent;
import zoomapi.components.WebinarComponent;

import java.util.HashMap;
import java.util.Map;

public class ZoomClient extends ApiClient {
    // Zoom api client
    protected Map<String, Object> components = new HashMap<>();
    protected ZoomClient(String api_key, String api_secret) {
        super("https://api.zoom.us/v2", 15);
        setApiKey(api_key);
        setApiSecret(api_secret);
        this.config.put("data_type", "json");

        this.components.put("meeting", new MeetingComponent(this.base_uri, this.timeout));
        this.components.put("report", new RecordingComponent(this.base_uri, this.timeout));
        this.components.put("user", new UserComponent(this.base_uri, this.timeout));
        this.components.put("webinar", new WebinarComponent(this.base_uri, this.timeout));
        this.components.put("recording", new ReportComponent(this.base_uri, this.timeout));

    }
    protected ZoomClient(String api_key, String api_secret, String data_type, int timeout){
        super("https://api.zoom.us/v2", timeout);
        setApiKey(api_key);
        setApiSecret(api_secret);
        this.config.put("data_type", data_type);

        this.components.put("meeting", new MeetingComponent(this.base_uri, this.timeout));
        this.components.put("report", new RecordingComponent(this.base_uri, this.timeout));
        this.components.put("user", new UserComponent(this.base_uri, this.timeout));
        this.components.put("webinar", new WebinarComponent(this.base_uri, this.timeout));
        this.components.put("recording", new ReportComponent(this.base_uri, this.timeout));
    }
    protected void refreshToken(){
        // to be implemented
    }

    protected String getApiKey(){
        return this.config.get("api_key");
    }

    protected void setApiKey(String api_key){
        this.config.put("api_key", api_key);
        refreshToken();
    }

    protected String getApiSecret(){
        return this.config.get("api_secret");
    }

    protected void setApiSecret(String api_secret){
        this.config.put("api_secret", api_secret);
        refreshToken();
    }

    public Object getMeeting(){
        return this.components.get("meeting");
    }

    public Object getReport(){
        return this.components.get("report");
    }

    public Object getUser(){
        return this.components.get("user");
    }

    public Object getWebinar(){
        return this.components.get("webinar");
    }

    public Object getRecording(){
        return this.components.get("recording");
    }
}
