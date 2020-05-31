package lib.oauth;

import lib.cache.tables.ChannelMessageTable;
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
    private ChatMessagesComponent chatMessages = null;

    public OauthMessage(OauthZoomClient client){
        super(client);
        refresh();
    }

    private void refresh(){
        this.chatMessages = (ChatMessagesComponent) this.client.getChatMessages();
        this.chatChannels = (ChatChannelsComponent) this.client.getChatChannels();
        this.clientId = this.client.getClientId();
    }


    /***
     * Check if `fromDate` before `toDate`
     * */
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


    /************************************************************************************************
     *****************                         Public APIs              *****************************
     ************************************************************************************************/

    /**************************************************
     * List Messages of a Channel `fromDate` `toDate`
     * fromDate format: yyyy-MM-dd
     * toDate format: yyyy-MM-dd
     * ************************************************/
    public List<ChannelMessage> getChannelMessages(String toChannel, String fromDate, String toDate) {
        // check validation of Dates
        checkValidation(fromDate, toDate);

        if (chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(toChannel);
        Map<String, String> params = new HashMap<>();
        params.put("userId", this.userId);
        params.put("to_channel", cid);
        params.put("page_size", "50");
        List<ChannelMessage> history_list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date from_date_format = dateFormat.parse(fromDate);
            Date to_date_format = dateFormat.parse(toDate);
            cal.setTime(from_date_format);
            while (from_date_format.compareTo(to_date_format) <= 0) {
                params.put("date", fromDate);
                JSONObject res = chatMessages.listMessages(params);

                // if failed, return null
                if(res.getInt("status_code")!=200) return null;

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
                    fromDate = dateFormat.format(from_date_format);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(history_list.size() != 0){
            // update cache
            // ...
        }

        return history_list;
    }

    public List<ChannelMessage> getChannelMessages(String toChannel, String fromDate, String toDate, boolean usingCache){
        if(!usingCache) return getChannelMessages(toChannel, fromDate, toDate);

        // check validation of Dates
        checkValidation(fromDate, toDate);
        ChannelMessageTable table = ChannelMessageTable.getInstance();
        List<ChannelMessage> history_list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date from_date_format = dateFormat.parse(fromDate);
            Date to_date_format = dateFormat.parse(toDate);
            cal.setTime(from_date_format);
            while (from_date_format.compareTo(to_date_format) <= 0) {
                List<ChannelMessage> messages = table.get(new String[]{"clientId", "channelId", "dateTime"}, new String[]{this.clientId, toChannel, fromDate});
                history_list.addAll(messages);

                // update fromDate++
                cal.add(Calendar.DATE, 1);
                from_date_format = cal.getTime();
                fromDate = dateFormat.format(from_date_format);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return history_list;
    }

    /**************************************************
     * List Messages of a Member `fromDate` `toDate`
     * fromDate format: yyyy-MM-dd
     * toDate format: yyyy-MM-dd
     * ************************************************/
    public List<ChannelMessage> getMemberMessages(String toMember, String fromDate, String toDate) {
        // check validation of Dates
        checkValidation(fromDate, toDate);

        if (chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(toMember);
        Map<String, String> params = new HashMap<>();
        params.put("userId", this.userId);
        params.put("to_contact", cid);
        params.put("page_size", "50");
        List<ChannelMessage> history_list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date from_date_format = dateFormat.parse(fromDate);
            Date to_date_format = dateFormat.parse(toDate);
            cal.setTime(from_date_format);
            while (from_date_format.compareTo(to_date_format) <= 0) {
                params.put("date", fromDate);
                JSONObject res = chatMessages.listMessages(params);

                // if failed, return null
                if(res.getInt("status_code")!=200) return null;

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
                    fromDate = dateFormat.format(from_date_format);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(history_list.size() != 0){
            // update cache
            // ...
        }

        return history_list;
    }

    /********************
     * Coming Soon
     * ******************/
    public List<ChannelMessage> getMemberMessages(String toMember, String fromDate, String toDate, boolean usingCache){
        if(!usingCache) return getMemberMessages(toMember, fromDate, toDate);
        return null;
//        // check validation of Dates
//        checkValidation(fromDate, toDate);
//        ChannelMessageTable table = ChannelMessageTable.getInstance();
//        List<ChannelMessage> history_list = new ArrayList<>();
//        Calendar cal = Calendar.getInstance();
//
//        try {
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date from_date_format = dateFormat.parse(fromDate);
//            Date to_date_format = dateFormat.parse(toDate);
//            cal.setTime(from_date_format);
//            while (from_date_format.compareTo(to_date_format) <= 0) {
//                List<ChannelMessage> messages = table.get(new String[]{"clientId", "contactId", "dateTime"}, new String[]{this.clientId, toMember, fromDate});
//                history_list.addAll(messages);
//
//                // update fromDate++
//                cal.add(Calendar.DATE, 1);
//                from_date_format = cal.getTime();
//                fromDate = dateFormat.format(from_date_format);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return history_list;
    }

    /************************************
     * Send one chat message to Channel
     * `toChannel`: name of the channel
     * **********************************/
    public boolean sendChatToGivenChannel(String toChannel, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(toChannel);
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", cid);
        data.put("message", message);
        JSONObject res = chatMessages.sendMessage(data);

        return (int)res.get("status_code")==201;
    }

    /************************************
     * Send one chat message to Member
     * `toMember`: email of the Member
     * **********************************/
    public boolean sendChatToGivenMember(String toMember, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");

        Map<String,String> data = new HashMap<>();
        data.put("to_contact", toMember);
        data.put("message", message);
        JSONObject res = chatMessages.sendMessage(data);

        return (int)res.get("status_code")==201;
    }

    /************************************
     * update one chat message to Channel
     * `toChannel`: name of the channel
     * **********************************/
    public boolean updateMessageToGivenChannel(String toChannel, String messageId, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", toChannel);
        data.put("messageId", messageId);
        data.put("message", message);
        JSONObject res = chatMessages.updateMessage(data);

        return (int)res.get("status_code")==204;
    }

    /************************************
     * update one chat message to Member
     * `toMember`: email of the Member
     * **********************************/
    public boolean updateMessageToGivenMember(String toMember, String messageId, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_contact", toMember);
        data.put("messageId", messageId);
        data.put("message", message);
        JSONObject res = chatMessages.updateMessage(data);

        return (int)res.get("status_code")==204;
    }

    /************************************
     * delete one chat message to Channel
     * `toChannel`: name of the channel
     * **********************************/
    public boolean deleteMessageToGivenChannel(String toChannel, String messageId){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", toChannel);
        data.put("messageId", messageId);
        JSONObject res = chatMessages.updateMessage(data);

        return (int)res.get("status_code")==204;
    }

    /************************************
     * delete one chat message to Member
     * `toMember`: email of the Member
     * **********************************/
    public boolean deleteMessageToGivenMember(String toMember, String messageId){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_contact", toMember);
        data.put("messageId", messageId);
        JSONObject res = chatMessages.updateMessage(data);

        return (int)res.get("status_code")==204;
    }

    /********************************************************************
     * Search an event <T> OauthCondition </T> within a channel,
     * return a list of chat messages apply to that <T> OauthCondition </T>
     ********************************************************************/
    public List<ChannelMessage> searchEvent(String toChannel, String fromDate, String toDate, OauthCondition condition){
        List<ChannelMessage> history_list = getChannelMessages(toChannel, fromDate, toDate);
        List<ChannelMessage> true_list = new ArrayList<>();
        for(ChannelMessage history:history_list){
            if(condition.isTrue(history)) true_list.add(history);
        }
        return true_list;
    }

    public List<ChannelMessage> searchEvent(String toChannel, String fromDate, String toDate, OauthCondition condition, boolean usingCache){
        if(!usingCache) return searchEvent(toChannel, fromDate, toDate, condition);
        return null;
    }
}