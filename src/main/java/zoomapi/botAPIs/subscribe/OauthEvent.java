package zoomapi.botAPIs.subscribe;

import zoomapi.OauthZoomClient;
import zoomapi.botAPIs.OauthChannel;
import zoomapi.botAPIs.OauthMessage;

import java.net.PasswordAuthentication;

public abstract class OauthEvent extends Event {
    protected OauthZoomClient client;
    protected OauthMessage oauthMessage;
    protected OauthChannel oauthChannel;
    public OauthEvent(OauthZoomClient client){
        super();
        this.client = client;
        oauthMessage = new OauthMessage(client);
        oauthChannel = new OauthChannel(client);
    }
}
