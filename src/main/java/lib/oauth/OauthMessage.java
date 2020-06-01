package lib.oauth;

import lib.cache.databaseData.Channel;
import lib.cache.tables.ChannelMessageTable;
import lib.cache.tables.ChannelTable;
import lib.cache.utils.CacheHelper;
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
import java.time.Instant;
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
        String cid = getCid(toChannel).getChannelId();
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

            // yyyy=MM-ddTHH:mm:ssZ in HTTP
            DateFormat zoomFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
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
                    if(dateTime!=null) {
                        Date zoomTime = zoomFormat.parse(dateTime);
                        dateTime = dateFormat.format(zoomTime);
                    }

                    // set values for ADS
                    Map<String, String> values = new HashMap<>();
                    values.put("clientId", this.clientId);
                    values.put("channelId", cid);
                    values.put("channelName", toChannel);
                    values.put("messageId", messageId);
                    values.put("message", message);
                    values.put("sender", sender);
                    values.put("dateTime", dateTime);
                    ChannelMessage m = new ChannelMessage();
                    m.setValues(values);

                    // update cache strategy: delete all, add all
                    CacheHelper<ChannelMessageTable, ChannelMessage> cache = new CacheHelper<>(ChannelMessageTable.class);
                    cache.update(new String[]{"clientId", "channelName", "messageId"}, new String[]{clientId, toChannel, messageId}, new ChannelMessage[]{m}, ChannelMessage.class);
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

        return historyList;
    }

    public List<ChannelMessage> getChannelMessages(String toChannel, String fromDate, String toDate, boolean usingCache){
        if(!usingCache) return getChannelMessages(toChannel, fromDate, toDate);

        // fetch from cache
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
                List<ChannelMessage> messages = table.get(new String[]{"clientId", "channelName", "dateTime"}, new String[]{this.clientId, toChannel, fromDate});
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
     * In cache, we treat a member as a special channel
     * **************************************************/
    public List<ChannelMessage> getMemberMessages(String toMember, String fromDate, String toDate) {
        // check validation of Dates
        checkValidation(fromDate, toDate);

        if (chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String, String> params = new HashMap<>();
        params.put("userId", this.userId);
        params.put("to_contact", toMember);
        params.put("page_size", "50");
        List<ChannelMessage> historyList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDateFormat = dateFormat.parse(fromDate);
            Date toDateFormat = dateFormat.parse(toDate);
            cal.setTime(fromDateFormat);

            // yyyy=MM-ddTHH:mm:ssZ in HTTP
            DateFormat zoomFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
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
                    if(dateTime!=null) {
                        Date zoomTime = zoomFormat.parse(dateTime);
                        dateTime = dateFormat.format(zoomTime);
                    }

                    // set values for ADS
                    Map<String, String> values = new HashMap<>();
                    values.put("clientId", this.clientId);
                    values.put("channelId", toMember);
                    values.put("channelName", toMember);
                    values.put("messageId", messageId);
                    values.put("message", message);
                    values.put("sender", sender);
                    values.put("dateTime", dateTime);
                    ChannelMessage m = new ChannelMessage();
                    m.setValues(values);

                    // update cache strategy: delete all, add all
                    CacheHelper<ChannelMessageTable, ChannelMessage> cache = new CacheHelper<>(ChannelMessageTable.class);
                    cache.update(new String[]{"clientId", "channelName", "messageId"}, new String[]{clientId, toMember, messageId}, new ChannelMessage[]{m}, ChannelMessage.class);
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

        return historyList;
    }

    public List<ChannelMessage> getMemberMessages(String toMember, String fromDate, String toDate, boolean usingCache){
        if(!usingCache) return getMemberMessages(toMember, fromDate, toDate);

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
                List<ChannelMessage> messages = table.get(new String[]{"clientId", "channelName", "dateTime"}, new String[]{this.clientId, toMember, fromDate});
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

    /************************************
     * Send a chat message to a Channel
     * `toChannel`: name of the channel
     ************************************/
    public ChannelMessage sendChatToChannel(String toChannel, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(toChannel).getChannelId();
        Map<String,String> data = new HashMap<>();
        data.put("to_channel", cid);
        data.put("message", message);
        JSONObject res = chatMessages.sendMessage(data);
        int statusCode = res.getInt("status_code");
        ChannelMessage cm = null;
        if (statusCode == 201) {
            cm = new ChannelMessage();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            Map<String, String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", cid);
            values.put("channelName", toChannel);
            values.put("messageId", res.getString("id"));
            values.put("message", message);
            values.put("dateTime", df.format(now));
            cm.setValues(values);

            CacheHelper<ChannelMessageTable, ChannelMessage> cache = new CacheHelper<>(ChannelMessageTable.class);
            cache.update(new String[]{"clientId", "channelName", "message"}, new String[]{clientId, toChannel, message}, new ChannelMessage[]{cm}, ChannelMessage.class);
        }
        return cm;
    }

    /************************************
     * Send a chat message to a Member
     * `toMember`: email of the Member
     * No table for MemberMessage, return empty Object for now
     ************************************/
    public ChannelMessage sendChatToMember(String toMember, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_contact", toMember);
        data.put("message", message);
        JSONObject res = chatMessages.sendMessage(data);
        int statusCode = res.getInt("status_code");
        ChannelMessage cm = null;
        if (statusCode == 201) {
            cm = new ChannelMessage();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            Map<String, String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", toMember);
            values.put("channelName", toMember);
            values.put("messageId", res.getString("id"));
            values.put("message", message);
            values.put("dateTime", df.format(now));
            cm.setValues(values);

            CacheHelper<ChannelMessageTable, ChannelMessage> cache = new CacheHelper<>(ChannelMessageTable.class);
            cache.update(new String[]{"clientId", "channelName", "message"}, new String[]{clientId, toMember, message}, new ChannelMessage[]{cm}, ChannelMessage.class);
        }
        return cm;
    }

    /***************************************
     * Update a chat message sent to Channel
     * `toChannel`: name of the channel
     ***************************************/
    public ChannelMessage updateMessageFromChannel(String toChannel, String messageId, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        String cid = getCid(toChannel).getChannelId();
        data.put("to_channel", cid);
        data.put("messageId", messageId);
        data.put("message", message);
        JSONObject res = chatMessages.updateMessage(data);
        int statusCode = res.getInt("status_code");
        ChannelMessage cm = null;
        if (statusCode == 204) {
            cm = new ChannelMessage();

            ChannelMessageTable table = ChannelMessageTable.getInstance();
            List<ChannelMessage> lists = table.get(new String[]{"clientId", "channelName", "messageId"}, new String[]{this.clientId, toChannel, messageId});
            if(lists.size()!=0) {
                cm = lists.get(0);
                // if we have this ChannelMessage in cache, we update
                // if we don't have, we do nothing, since there is not way to know the dateTime of that message
                // Not even from the http response from the Zoom server
                table.update(new String[]{"message"}, new String[]{message}, new String[]{"clientId", "channelName", "messageId"}, new String[]{this.clientId, toChannel, messageId});
            }
        }
        return cm;
    }

    /***************************************
     * Update a chat message sent to Member
     * `toMember`: email of the Member
     * No table for MemberMessage, return empty Object for now
     ***************************************/
    public ChannelMessage updateMessageSentToMember(String toMember, String messageId, String message){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_contact", toMember);
        data.put("messageId", messageId);
        data.put("message", message);
        JSONObject res = chatMessages.updateMessage(data);
        int statusCode = res.getInt("status_code");
        ChannelMessage cm = null;
        if (statusCode == 204) {
            cm = new ChannelMessage();
            ChannelMessageTable table = ChannelMessageTable.getInstance();
            List<ChannelMessage> lists = table.get(new String[]{"clientId", "channelName", "messageId"}, new String[]{this.clientId, toMember, messageId});
            if(lists.size()!=0) {
                cm = lists.get(0);
                // if we have this ChannelMessage in cache, we update
                // if we don't have, we do nothing
                table.update(new String[]{"message"}, new String[]{message}, new String[]{"clientId", "channelName", "messageId"}, new String[]{this.clientId, toMember, messageId});
            }
        }
        return cm;
    }

    /***************************************
     * Delete a chat message sent to Channel
     * `toChannel`: name of the channel
     ***************************************/
    public ChannelMessage deleteMessageFromChannel(String toChannel, String messageId){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        String cid = getCid(toChannel).getChannelId();
        data.put("to_channel", cid);
        data.put("messageId", messageId);
        JSONObject res = chatMessages.deleteMessage(data);
        int statusCode = res.getInt("status_code");
        ChannelMessage cm = null;
        if (statusCode == 204) {
            cm = new ChannelMessage();

            Map<String, String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", cid);
            values.put("channelName", toChannel);
            values.put("messageId", messageId);
            cm.setValues(values);

            CacheHelper<ChannelMessageTable, ChannelMessage> cache = new CacheHelper<>(ChannelMessageTable.class);
            cache.update(new String[]{"clientId", "channelName", "messageId"}, new String[]{clientId, toChannel, messageId}, new ChannelMessage[]{}, ChannelMessage.class);
        }
        return cm;
    }

    /***************************************
     * Delete a chat message sent to Member
     * `toMember`: email of the Member
     * No table for MemberMessage, return empty Object for now
     ***************************************/
    public ChannelMessage deleteMessageSentToMember(String toMember, String messageId){
        if(chatMessages == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("to_contact", toMember);
        data.put("messageId", messageId);
        JSONObject res = chatMessages.deleteMessage(data);
        int statusCode = res.getInt("status_code");
        ChannelMessage cm = null;
        if (statusCode == 204) {
            cm = new ChannelMessage();

            Map<String, String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", toMember);
            values.put("channelName", toMember);
            values.put("messageId", messageId);
            cm.setValues(values);

            CacheHelper<ChannelMessageTable, ChannelMessage> cache = new CacheHelper<>(ChannelMessageTable.class);
            cache.update(new String[]{"clientId", "channelName", "messageId"}, new String[]{clientId, toMember, messageId}, new ChannelMessage[]{}, ChannelMessage.class);
        }
        return cm;
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

        List<ChannelMessage> historyList = getChannelMessages(toChannel, fromDate, toDate, true);
        List<ChannelMessage> trueList = new ArrayList<>();
        for(ChannelMessage history:historyList){
            if(condition.isTrue(history)) trueList.add(history);
        }
        return trueList;
    }
}