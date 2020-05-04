package zoomapi.botAPIs.subscribe;

import zoomapi.OauthZoomClient;
import zoomapi.botAPIs.OauthChannel;
import zoomapi.botAPIs.OauthMessage;

import java.net.PasswordAuthentication;

public abstract class OauthEventHandler extends EventHandler {
    protected OauthZoomClient client;
    public OauthEventHandler(OauthZoomClient client){
        super();
        this.client = client;
    }
}
