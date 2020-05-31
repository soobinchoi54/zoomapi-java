package lib.subscription;

import lib.clients.OauthZoomClient;

public abstract class OauthEventHandler extends EventHandler {
    protected OauthZoomClient client;
    public OauthEventHandler(OauthZoomClient client){
        super();
        this.client = client;
    }
}
