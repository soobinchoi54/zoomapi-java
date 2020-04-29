package zoomapi.botAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import zoomapi.OauthZoomClient;
import zoomapi.components.ChatChannelsComponent;
import zoomapi.components.ChatMessagesComponent;
import zoomapi.utils.Message;
import zoomapi.utils.OauthCondition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OauthMessage{
    private OauthZoomClient client;
    private ChatMessagesComponent chat_messages = null;
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
    }

    public boolean sendChatToGivenChannel(String to_channel, String message){
        if(chat_messages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(to_channel);
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", cid);
        data.put("message", message);
        return (int) chat_messages.sendMessage(data).get("status_code") == 201;
    }

    public List<Message> getChatHistory(String to_channel, String from_date, String to_date) {
        if (chat_messages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(to_channel);
        Map<String, String> params = new HashMap<>();
        params.put("userId", this.userId);
        params.put("to_channel", cid);
        params.put("page_size", "50");
        List<Message> history_list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        try {
            Date from_date_format = new SimpleDateFormat("yyyy-mm-dd").parse(from_date);
            Date to_date_format = new SimpleDateFormat("yyyy-mm-dd").parse(to_date);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            cal.setTime(from_date_format);
            while (from_date_format.compareTo(to_date_format) <= 0) {
                params.put("date", from_date);
                JSONObject res = chat_messages.listMessages(params);
                List<JSONObject> listObjs = parseJsonData(res, "messages");
                Message m;
                for (int i = 0; i<listObjs.size(); i++) {
                    String id = listObjs.get(i).getString("id");
                    String message = listObjs.get(i).getString("message");
                    String sender = listObjs.get(i).getString("sender");
                    String date_time = listObjs.get(i).getString("date_time");
                    int timestamp = listObjs.get(i).getInt("timestamp");
                    m = new Message(id, message, sender, date_time, timestamp);
                    history_list.add(m);
                }
                if(res.getString("next_page_token").length()>1){
                    params.put("next_page_token", res.getString("next_page_token"));
                }
                //increment from start_date -> to_date until while loop ends
                else{
                    params.remove("next_page_token");
                    cal.add(Calendar.DATE, 1);
                    from_date_format = cal.getTime();
                    from_date = dateFormat.format(from_date_format);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return history_list;
    }

    public List<Message> searchEvent(String to_channel, String from_date, String to_date, OauthCondition condition){
        List<Message> history_list = getChatHistory(to_channel, from_date, to_date);
        List<Message> true_list = new ArrayList<>();
        for(Message history:history_list){
            JSONObject item = new JSONObject(history);
            Map<String, String> message = new HashMap();
            message.put("id", item.getString("id"));
            message.put("message", item.getString("message"));
            message.put("sender", item.getString("sender"));
            message.put("date_time", item.getString("date_time"));
            message.put("timestamp", item.getString("id"));
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

    public static List<JSONObject> parseJsonData(JSONObject obj, String pattern)throws JSONException {

        List<JSONObject> listObjs = new ArrayList<>();
        JSONArray all_messages = obj.getJSONArray (pattern);
        for (int i = 0; i < all_messages.length(); ++i) {
            final JSONObject site = all_messages.getJSONObject(i);
            listObjs.add(site);
        }
        return listObjs;
    }
}