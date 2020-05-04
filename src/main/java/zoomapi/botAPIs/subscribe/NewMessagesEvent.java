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
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public NewMessagesEvent(OauthZoomClient client, String channelName) {
        super(client);
        this.channelName = channelName;
        messageIds = new HashSet<>();
    }

    private void messageIdsInit(){
        Date currentDate = new Date();
        String date = dateFormat.format(currentDate);
        List<Message> messages = oauthMessage.getChatHistory(channelName, date, "2020-05-04");
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
            Date currentDate = new Date();
            String date = dateFormat.format(currentDate);
            List<Message> messages = oauthMessage.getChatHistory(channelName, date, "2020-05-04");
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
