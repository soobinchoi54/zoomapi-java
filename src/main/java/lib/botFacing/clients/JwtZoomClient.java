package lib.botFacing.clients;

import lib.components.BaseComponent;
import lib.components.ChatChannelsComponent;
import lib.components.ChatMessagesComponent;
import lib.utils.Util;

public class JwtZoomClient extends ZoomClient{
    public JwtZoomClient(String api_key, String api_secret, int PORT, String redirect_url, String browser_path){
        super(api_key, api_secret);
        this.base_uri = "https://api.zoom.us/v2";
        String data_type="json";
        int timeout=15;
        String version="2";
        // specify config details
        this.config.put("api_key", api_key);
        this.config.put("api_secret", api_secret);
        this.config.put("data_type", "json");
        this.config.put("version", version);
        this.config.put("token", Util.generate_jwt(api_key, api_secret));
        refreshToken();
        this.components.put("chat_channels", new ChatChannelsComponent(this.base_uri, this.config));
        this.components.put("chat_messages", new ChatMessagesComponent(this.base_uri, this.config));
    }

    private void refreshToken(){
        this.config.put("token", Util.generate_jwt(this.config.get("api_key"), this.config.get("api_secret")));
    }

    public String getApiKey(){
        return this.config.get("api_key");
    }

    public void setApiKey(String val){
        this.config.put("api_key", val);
        refreshToken();
    }

    public String getApiKSecret(){
        return this.config.get("api_secret");
    }

    public void setApiSecret(String val){
        this.config.put("api_secret", val);
        refreshToken();
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
