package zoomapi.botAPIs.subscribe;

import zoomapi.OauthZoomClient;
import zoomapi.utils.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/************************
 * Singleton Class UpdatedMessagesEvent
 ************************/

public class UpdatedMessagesEvent extends OauthEvent{
    private String channelName;
    private Set<String> messageIds;
    private volatile String fromDate;
    private volatile String toDate;
    public UpdatedMessagesEvent(OauthZoomClient client, String channelName, String fromDate, String toDate) {
        super(client);
        this.channelName = channelName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        messageIds = new HashSet<>();
    }

    private void messageIdsInit(){
        List<Message> messages = oauthMessage.getChatHistory(channelName, fromDate, toDate);
        for(int i = 0; i < messages.size(); i++){
            Message message = messages.get(i);
            String messageId = message.getId();
            this.messageIds.add(messageId);
        }
    }
    @Override
    public void run() {

    }
}
