package zoomapi.botAPIs;

import org.apache.http.client.utils.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.OauthZoomClient;
import zoomapi.components.ChatChannelsComponent;
import zoomapi.components.ChatMessagesComponent;
import zoomapi.components.UserComponent;
import zoomapi.utils.OauthCondition;

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
    private ChatChannelsComponent chat_channels = null;
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
        chat_channels = (ChatChannelsComponent) this.client.getChatChannels();
        user = (UserComponent) this.client.getUser();
    }

    public boolean sendChatToGivenChannel(String to_channel, String message){
        if(chat_messages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(to_channel);
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", cid);
        data.put("message", message);
        return (int) chat_messages.sendMessage(data).get("status_code") == 201;
    }

    public List<String> getChatHistory(String to_channel) {
        if (chat_messages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(to_channel);
        Map<String, String> params = new HashMap<>();
        params.put("userId", this.userId);
        params.put("to_channel", cid);
        List<String> history_list = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String start_date = String.valueOf(user.getUser(params).get("created_at"));
        String end_date = String.valueOf(chat_messages.listMessages(params).get("date"));
        try {
            Date created_at;
            created_at = dateFormat.parse(start_date);
            cal.setTime(dateFormat.parse(end_date));
            Date current_date = dateFormat.parse(end_date);
            while (created_at.compareTo(current_date) <= 0) {
                params.put("date", String.valueOf(current_date));
                JSONArray messages = (JSONArray) chat_messages.listMessages(params).get("messages");
                for (int i = 0; i<messages.length(); i++) {
//                    String message = messages.getJSONObject(i).getString("message");
//                    String date_time = messages.getJSONObject(i).getString("date_time");
//                    String history = date_time + " : " + message;
                    JSONObject history = messages.getJSONObject(i);
                    history_list.add(history.toString());
                }
                cal.add(Calendar.DATE, -1);
                current_date = cal.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return history_list;
    }

    public List<String> searchEvent(String to_channel, OauthCondition condition){
        List<String> history_list = getChatHistory(to_channel);
        List<String> true_list = new ArrayList<>();
        for(String history:history_list){
            JSONObject item = new JSONObject(history);
            Map<String, String> message = new HashMap();
            message.put("id", item.getString("id"));
            message.put("message", item.getString("message"));
            message.put("sender", item.getString("sender"));
            message.put("date_time", item.getString("date_time"));
            message.put("timestamp", String.valueOf(item.getInt("id")));
            if(condition.isTrue(message)) true_list.add(history);
        }
        return true_list;
    }

    private String getCid(String to_channel){
        JSONArray channels = (JSONArray) chat_channels.listChannels().get("channels");
        String cid = null;
        for (int i = 0; i<channels.length(); i++) {
            if(channels.getJSONObject(i).getString("name").equals(to_channel)) cid = channels.getJSONObject(i).getString("id");
        }
        if(cid == null) {
            throw new IllegalArgumentException("Invalid Channel Name");
        }
        return cid;
    }
}
