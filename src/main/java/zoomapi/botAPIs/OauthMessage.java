package zoomapi.botAPIs;

import org.apache.http.client.utils.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.OauthZoomClient;
import zoomapi.components.ChatMessagesComponent;
import zoomapi.components.UserComponent;
import zoomapi.utils.OauthEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OauthMessage{
    private OauthZoomClient client;
    private ChatMessagesComponent chat_messages = null;
    private UserComponent user = null;
    private String userId = "me";
    public OauthMessage(OauthZoomClient client){
        this.client = client;
        refresh();
    }

    public void setClient(OauthZoomClient client){
        this.client = client;
        refresh();
    }

    private void refresh(){
        chat_messages = (ChatMessagesComponent) this.client.getChatMessages();
        user = (UserComponent) this.client.getUser();
    }

    public boolean sendChatToGivenChannel(String to_channel, String message){
        if(chat_messages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", to_channel);
        data.put("message", message);
        return (int) chat_messages.sendMessage(data).get("status_code") == 201;
    }

    public List<String> getChatHistory(String to_channel) {
        if (chat_messages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String, String> params = new HashMap<>();
        params.put("userId", this.userId);
        params.put("to_channel", to_channel);
        List<String> history_list = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Calendar cal = Calendar.getInstance();
        String start_date = String.valueOf(user.getUser(params).get("created_at"));
        String end_date = String.valueOf(chat_messages.listMessages(params).get("date"));
        try {
            Date created_at;
            created_at = dateFormat.parse(start_date);
            cal.setTime(dateFormat.parse(end_date));
            Date current_date = dateFormat.parse(end_date);
            while (created_at.before(current_date)) {
                params.put("date", String.valueOf(current_date));
                JSONArray messages = (JSONArray) chat_messages.listMessages(params).get("messages");
                for (int i = 0; i<messages.length(); i++) {
                    String message = messages.getJSONObject(i).getString("message");
                    String date_time = messages.getJSONObject(i).getString("date_time");
                    String history = date_time + " : " + message;
                    history_list.add(history);
                }
                cal.add(Calendar.DATE, -1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return history_list;
    }

    public List<String> searchEvent(String to_channel, OauthEvent e){
        return e.happens();
    }
}
