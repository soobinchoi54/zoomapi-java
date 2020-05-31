package lib.subscription;

import lib.clients.OauthZoomClient;
import lib.oauth.OauthMessage;
import lib.cache.databaseData.ChannelMessage;

import java.util.*;

/************************
 * Singleton Class UpdatedMessagesEvent
 ************************/

public class UpdatedMessagesEventHandler extends OauthEventHandler{
    private String channelName;
    private Map<String, String> messages;
    private String fromDate;
    private String toDate;
    private OauthMessage oauthMessage;
    public UpdatedMessagesEventHandler(OauthZoomClient client, String channelName, String fromDate, String toDate) {
        super(client);
        this.oauthMessage = new OauthMessage(client);
        this.channelName = channelName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        messages = new HashMap<>();
    }

    private void messagesInit(){
        List<ChannelMessage> messages = oauthMessage.getChannelMessages(channelName, fromDate, toDate);
        for(int i = 0; i < messages.size(); i++){
            ChannelMessage message = messages.get(i);
            this.messages.put(message.getMessageId(), message.getMessage());
        }
        System.out.println("New updates listener starts working...");
    }
    @Override
    public void run() {
        messagesInit();
        while(this.work){
            // System.out.println("checking message updates");
            List<ChannelMessage> messages = oauthMessage.getChannelMessages(channelName, this.fromDate, this.toDate);
            Map<String, String> newMessages = new HashMap<>();
            for(int i = 0; i < messages.size(); i++){
                ChannelMessage message = messages.get(i);
                String messageId = message.getMessageId();
                String messageBody = message.getMessage();
                newMessages.put(messageId,messageBody);
                if(this.messages.containsKey(messageId)){
                    String previousBody = this.messages.get(messageId);
                    if(!previousBody.equals(messageBody))
                    SubscribeAgency.announce(SubscribeAgency.NOTIFY_MESSAGE_UPDATES, this.channelName, message);
                }else continue;
            }
            this.messages = newMessages;
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
