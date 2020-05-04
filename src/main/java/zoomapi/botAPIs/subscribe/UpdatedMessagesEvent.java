package zoomapi.botAPIs.subscribe;

import zoomapi.OauthZoomClient;

/************************
 * Singleton Class UpdatedMessagesEvent
 ************************/

public class UpdatedMessagesEvent extends OauthEvent{
    private String channelName;
    public UpdatedMessagesEvent(OauthZoomClient client, String channelName) {
        super(client);
        this.channelName = channelName;
    }

    @Override
    public void run() {

    }
}
