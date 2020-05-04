package zoomapi.botAPIs.subscribe;

import zoomapi.OauthZoomClient;
import zoomapi.botAPIs.OauthMessage;
import zoomapi.utils.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/************************
 * Singleton Class NewMessagesEvent
 ************************/

public class NewMessagesEventHandler extends OauthEventHandler{
    private String channelName;
    private Set<String> messageIds;
    private String fromDate;
    private String toDate;
    private OauthMessage oauthMessage;
    public NewMessagesEventHandler(OauthZoomClient client, String channelName, String fromDate, String toDate) {
        super(client);
        this.oauthMessage = new OauthMessage(client);
        this.channelName = channelName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        messageIds = new HashSet<>();
    }

    private void messageIdsInit(){
        List<Message> messages = oauthMessage.getChatHistory(channelName, this.fromDate, this.toDate);
        for(int i = 0; i < messages.size(); i++){
            Message message = messages.get(i);
            String messageId = message.getId();
            this.messageIds.add(messageId);
        }
    }

    @Override
    public void run() {
        messageIdsInit();
        while(this.work){
            // System.out.println("checking new messages");
            List<Message> messages = oauthMessage.getChatHistory(channelName, this.fromDate, this.toDate);
            Set<String> newMessageIds = new HashSet<>();
            for(int i = 0; i < messages.size(); i++){
                Message message = messages.get(i);
                String messageId = message.getId();
                newMessageIds.add(messageId);
                if(!this.messageIds.contains(messageId)){
                    this.messageIds.add(messageId);
                    SubscribeAgency.announce(SubscribeAgency.NOTIFY_NEW_MESSAGES, this.channelName, message);
                }else continue;
            }
            this.messageIds = newMessageIds;
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
