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
 * Singleton Class NewMessagesEvent
 ************************/

public class NewMessagesEvent extends OauthEvent{
    private String channelName;
    private Set<String> messageIds;
    private String fromDate;
    private String toDate;
    public NewMessagesEvent(OauthZoomClient client, String channelName, String fromDate, String toDate) {
        super(client);
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
            System.out.println("new message checking");
            List<Message> messages = oauthMessage.getChatHistory(channelName, this.fromDate, this.toDate);
            for(int i = 0; i < messages.size(); i++){
                Message message = messages.get(i);
                String messageId = message.getId();
                if(!this.messageIds.contains(messageId)){
                    this.messageIds.add(messageId);
                    SubscribeAgency.announce(SubscribeAgency.NOTIFY_NEW_MESSAGES, this.channelName, message);
                }else continue;
            }
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
