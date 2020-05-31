package lib.events;

import lib.botFacing.clients.OauthZoomClient;

public abstract class OauthEventHandler extends EventHandler {
    protected OauthZoomClient client;
    public OauthEventHandler(OauthZoomClient client){
        super();
        this.client = client;
    }
}
