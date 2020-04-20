package zoomapi;

import zoomapi.components.BaseComponent;
import zoomapi.utils.Util;
import zoomapi.components.ChatChannelsComponent;
import zoomapi.components.ChatMessagesComponent;

public class OauthZoomClient extends ZoomClient{
    // Zoom Oauth api java client

    public OauthZoomClient(String client_id, String client_secret, int PORT, String redirect_url, String browser_path){
        super(client_id, client_secret);

        // specify config details
        this.config.put("client_id", client_id);
        this.config.put("client_secret", client_secret);
        this.config.put("PORT", String.valueOf(PORT));
        this.config.put("redirect_url", redirect_url);
        this.config.put("browser_path", browser_path);
        refreshToken();
        this.components.put("chat_channels", new ChatChannelsComponent(this.base_uri, this.config));
        this.components.put("chat_messages", new ChatMessagesComponent(this.base_uri, this.config));
    }

    public OauthZoomClient(String client_id, String client_secret, int PORT, String redirect_url, String browser_path, String data_type, int time_out){
        super(client_id, client_secret, data_type, time_out);
        this.config.put("client_id", client_id);
        this.config.put("client_secret", client_secret);
        this.config.put("PORT", String.valueOf(PORT));
        this.config.put("redirect_url", redirect_url);
        this.config.put("browser_path", browser_path);
        this.config.put("token", Util.getOauthToken(this.config.get("client_id"), this.config.get("client_secret"), this.config.get("PORT"), this.config.get("redirect_url"), this.config.get("browser_path")));
        this.components.put("chat_channels", new ChatChannelsComponent(this.base_uri, this.config));
        this.components.put("chat_messages", new ChatMessagesComponent(this.base_uri, this.config));
    }

    @Override
    public void refreshToken(){
        System.out.println(this.config.get("client_id")+" "+this.config.get("client_secret")+" "+this.config.get("PORT")+" "+this.config.get("redirect_url")+" "+this.config.get("browser_path"));
        this.config.put("token", Util.getOauthToken(this.config.get("client_id"), this.config.get("client_secret"), this.config.get("PORT"), this.config.get("redirect_url"), this.config.get("browser_path")));
    }

    public String getRedirectUrl(){
        return this.config.get("redirect_url");
    }

    public void setRedirectUrl(String redirect_url){
        this.config.put("redirect_url", redirect_url);
        refreshToken();
    }

    public BaseComponent getChatChannels(){
        return this.components.get("chat_channels");
    }

    public BaseComponent getChatMessages(){
        return this.components.get("chat_messages");
    }
}
