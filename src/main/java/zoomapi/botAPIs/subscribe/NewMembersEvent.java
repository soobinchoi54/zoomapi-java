package zoomapi.botAPIs.subscribe;

import zoomapi.OauthZoomClient;

/************************
 * Singleton Class NewMembersEvent
 ************************/

public class NewMembersEvent extends OauthEvent {
    private String channelName;
    public NewMembersEvent(OauthZoomClient client, String channelName) {
        super(client);
        this.channelName = channelName;
    }

    @Override
    public void run() {

    }
}
