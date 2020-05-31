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


    /****************************************
     * Check if `fromDate` is before `toDate`
     ****************************************/
    private void checkValidation(String fromDate, String toDate){
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDateFormat = dateFormat.parse(fromDate);
            Date toDateFormat = dateFormat.parse(toDate);
            if(fromDateFormat.compareTo(toDateFormat) > 0) throw new IllegalArgumentException("illegal from/to date input");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    /************************************************************************************************
     *****************                         Public APIs              *****************************
     ************************************************************************************************/

    /*********************************************************
     * List Messages from Channel between `fromDate` `toDate`
     * fromDate format: yyyy-MM-dd
     * toDate format: yyyy-MM-dd
     * *******************************************************/
    public List<ChannelMessage> getChannelMessages(String toChannel, String fromDate, String toDate) {
        // check validation of Dates
        checkValidation(fromDate, toDate);

        if (chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(toChannel);
        Map<String, String> params = new HashMap<>();
        params.put("userId", this.userId);
        params.put("to_channel", cid);
        params.put("page_size", "50");
        List<ChannelMessage> historyList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDateFormat = dateFormat.parse(fromDate);
            Date toDateFormat = dateFormat.parse(toDate);
            cal.setTime(fromDateFormat);
            while (fromDateFormat.compareTo(toDateFormat) <= 0) {
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
                    historyList.add(m);
                }
                if(res.getString("next_page_token").length()>1){
                    params.put("next_page_token", res.getString("next_page_token"));
                }
                //increment from start_date -> to_date until while loop ends
                else{
                    params.remove("next_page_token");
                    cal.add(Calendar.DATE, 1);
                    fromDateFormat = cal.getTime();
                    fromDate = dateFormat.format(fromDateFormat);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(historyList.size() != 0){
            // update cache
            // ...
        }

        return historyList;
    }

    public List<ChannelMessage> getChannelMessages(String toChannel, String fromDate, String toDate, boolean usingCache){
        if(!usingCache) return getChannelMessages(toChannel, fromDate, toDate);

        // check validation of Dates
        checkValidation(fromDate, toDate);
        ChannelMessageTable table = ChannelMessageTable.getInstance();
        List<ChannelMessage> historyList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDateFormat = dateFormat.parse(fromDate);
            Date toDateFormat = dateFormat.parse(toDate);
            cal.setTime(fromDateFormat);
            while (fromDateFormat.compareTo(toDateFormat) <= 0) {
                List<ChannelMessage> messages = table.get(new String[]{"clientId", "channelId", "dateTime"}, new String[]{this.clientId, toChannel, fromDate});
                historyList.addAll(messages);

                // update fromDate++
                cal.add(Calendar.DATE, 1);
                fromDateFormat = cal.getTime();
                fromDate = dateFormat.format(fromDateFormat);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return historyList;
    }

    /****************************************************
     * List Messages sent to a Member `fromDate` `toDate`
     * fromDate format: yyyy-MM-dd
     * toDate format: yyyy-MM-dd
     * **************************************************/
    public List<ChannelMessage> getMemberMessages(String toMember, String fromDate, String toDate) {
        // check validation of Dates
        checkValidation(fromDate, toDate);

        if (chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(toMember);
        Map<String, String> params = new HashMap<>();
        params.put("userId", this.userId);
        params.put("to_contact", cid);
        params.put("page_size", "50");
        List<ChannelMessage> historyList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDateFormat = dateFormat.parse(fromDate);
            Date toDateFormat = dateFormat.parse(toDate);
            cal.setTime(fromDateFormat);
            while (fromDateFormat.compareTo(toDateFormat) <= 0) {
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
                    historyList.add(m);
                }
                if(res.getString("next_page_token").length()>1){
                    params.put("next_page_token", res.getString("next_page_token"));
                }
                //increment from start_date -> to_date until while loop ends
                else{
                    params.remove("next_page_token");
                    cal.add(Calendar.DATE, 1);
                    fromDateFormat = cal.getTime();
                    fromDate = dateFormat.format(fromDateFormat);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(historyList.size() != 0){
            // update cache
            // ...
        }

        return historyList;
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
//        List<ChannelMessage> historyList = new ArrayList<>();
//        Calendar cal = Calendar.getInstance();
//
//        try {
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date fromDateFormat = dateFormat.parse(fromDate);
//            Date toDateFormat = dateFormat.parse(toDate);
//            cal.setTime(fromDateFormat);
//            while (fromDateFormat.compareTo(toDateFormat) <= 0) {
//                List<ChannelMessage> messages = table.get(new String[]{"clientId", "contactId", "dateTime"}, new String[]{this.clientId, toMember, fromDate});
//                historyList.addAll(messages);
//
//                // update fromDate++
//                cal.add(Calendar.DATE, 1);
//                fromDateFormat = cal.getTime();
//                fromDate = dateFormat.format(fromDateFormat);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return historyList;
    }

    /************************************
     * Send a chat message to a Channel
     * `toChannel`: name of the channel
     ************************************/
    public boolean sendChatToChannel(String toChannel, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(toChannel);
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", cid);
        data.put("message", message);
        JSONObject res = chatMessages.sendMessage(data);

        return (int)res.get("status_code")==201;
    }

    /************************************
     * Send a chat message to a Member
     * `toMember`: email of the Member
     ************************************/
    public boolean sendChatToMember(String toMember, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");

        Map<String,String> data = new HashMap<>();
        data.put("to_contact", toMember);
        data.put("message", message);
        JSONObject res = chatMessages.sendMessage(data);

        return (int)res.get("status_code")==201;
    }

    /***************************************
     * Update a chat message sent to Channel
     * `toChannel`: name of the channel
     ***************************************/
    public boolean updateMessageFromChannel(String toChannel, String messageId, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", toChannel);
        data.put("messageId", messageId);
        data.put("message", message);
        JSONObject res = chatMessages.updateMessage(data);

        return (int)res.get("status_code")==204;
    }

    /***************************************
     * Update a chat message sent to Member
     * `toMember`: email of the Member
     ***************************************/
    public boolean updateMessageSentToMember(String toMember, String messageId, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_contact", toMember);
        data.put("messageId", messageId);
        data.put("message", message);
        JSONObject res = chatMessages.updateMessage(data);

        return (int)res.get("status_code")==204;
    }

    /***************************************
     * Delete a chat message sent to Channel
     * `toChannel`: name of the channel
     ***************************************/
    public boolean deleteMessageFromChannel(String toChannel, String messageId){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", toChannel);
        data.put("messageId", messageId);
        JSONObject res = chatMessages.updateMessage(data);

        return (int)res.get("status_code")==204;
    }

    /***************************************
     * Delete a chat message sent to Member
     * `toMember`: email of the Member
     ***************************************/
    public boolean deleteMessageSentToMember(String toMember, String messageId){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_contact", toMember);
        data.put("messageId", messageId);
        JSONObject res = chatMessages.updateMessage(data);

        return (int)res.get("status_code")==204;
    }

    /************************************************************************
     * Search an event <T> OauthCondition </T> within a Channel,
     * return a list of chat messages that fall under <T> OauthCondition </T>
     ************************************************************************/
    public List<ChannelMessage> searchEvent(String toChannel, String fromDate, String toDate, OauthCondition condition){
        List<ChannelMessage> historyList = getChannelMessages(toChannel, fromDate, toDate);
        List<ChannelMessage> trueList = new ArrayList<>();
        for(ChannelMessage history:historyList){
            if(condition.isTrue(history)) trueList.add(history);
        }
        return trueList;
    }

    public List<ChannelMessage> searchEvent(String toChannel, String fromDate, String toDate, OauthCondition condition, boolean usingCache){
        if(!usingCache) return searchEvent(toChannel, fromDate, toDate, condition);
        return null;
    }
}