package lib.oauth;

import org.json.JSONObject;
import lib.clients.OauthZoomClient;
import lib.cache.databaseData.ChannelMessage;
import lib.components.ChatChannelsComponent;
import lib.components.ChatMessagesComponent;
import lib.utils.OauthCondition;
import lib.utils.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OauthMessage extends OauthChannel{
    private ChatMessagesComponent chat_messages = null;

    public OauthMessage(OauthZoomClient client){
        super(client);
        refresh();
    }

    public void setClient(OauthZoomClient client){
        this.client = client;
        refresh();
    }

    private void refresh(){
        this.chat_messages = (ChatMessagesComponent) this.client.getChatMessages();
        this.chat_channels = (ChatChannelsComponent) this.client.getChatChannels();
        this.clientId = this.client.getClientId();
    }

    public boolean sendChatToGivenChannel(String to_channel, String message){
        if(chat_messages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(to_channel);
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", cid);
        data.put("message", message);
        return (int) chat_messages.sendMessage(data).get("status_code") == 201;
    }

    public List<ChannelMessage> getChannelMessages(String toChannel, String from_date, String to_date) {
        checkValidation(from_date, to_date);
        if (chat_messages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(toChannel);
        Map<String, String> params = new HashMap<>();
        params.put("userId", this.userId);
        params.put("to_channel", cid);
        params.put("page_size", "50");
        List<ChannelMessage> history_list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date from_date_format = dateFormat.parse(from_date);
            Date to_date_format = dateFormat.parse(to_date);
            cal.setTime(from_date_format);
            while (from_date_format.compareTo(to_date_format) <= 0) {
                params.put("date", from_date);
                JSONObject res = chat_messages.listMessages(params);
                List<JSONObject> listObjs = Util.parseJsonData(res, "messages");
                for (int i = 0; i<listObjs.size(); i++) {
                    // HTTP request to get data
                    JSONObject obj = listObjs.get(i);
                    String messageId = obj.has("id") ? obj.getString("id"):null;
                    String message = obj.has("message") ? obj.getString("message"):null;
                    String sender = obj.has("sender") ? obj.getString("sender"):null;
                    String dateTime = obj.has("date_time") ? obj.getString("date_time"):null;

                    // set values for ADS
                    Map<String, String> values = new HashMap<>();
                    values.put("clientId", this.clientId);
                    values.put("channelId", cid);
                    values.put("messageId", messageId);
                    values.put("message", message);
                    values.put("sender", sender);
                    values.put("dateTime", dateTime);
                    ChannelMessage m = new ChannelMessage();
                    m.setValues(values);
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

    private void checkValidation(String fromDate, String toDate){
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date from_date_format = dateFormat.parse(fromDate);
            Date to_date_format = dateFormat.parse(toDate);
            if(from_date_format.compareTo(to_date_format) > 0) throw new IllegalArgumentException("illegal from/to date input");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public List<ChannelMessage> searchEvent(String to_channel, String from_date, String to_date, OauthCondition condition){
        List<ChannelMessage> history_list = getChannelMessages(to_channel, from_date, to_date);
        List<ChannelMessage> true_list = new ArrayList<>();
        for(ChannelMessage history:history_list){
            if(condition.isTrue(history)) true_list.add(history);
        }
        return true_list;
    }

}