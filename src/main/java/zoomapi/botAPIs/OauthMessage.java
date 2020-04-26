package zoomapi.botAPIs;

import zoomapi.OauthZoomClient;
import zoomapi.components.ChatMessagesComponent;
import zoomapi.utils.OauthEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OauthMessage{
    OauthZoomClient client;
    ChatMessagesComponent chat_messages = null;
    public OauthMessage(OauthZoomClient client){
        this.client = client;
        refresh();
    }

    private void refresh(){
        chat_messages = (ChatMessagesComponent) this.client.getChatMessages();
    }


    public boolean sendChatToGivenChannel(String to_channel, String message){
        if(chat_messages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>(){
            {
                put("to_channel", to_channel);
                put("message", message);
            }
        };
        return (int) chat_messages.sendMessage(data).get("status_code") == 201;
    }

    public List<String> getChatHistory(String to_channel){
        return null;
    }

    public List<String> searchEvent(String to_channel, OauthEvent e){
        return e.happens();
    }
}
