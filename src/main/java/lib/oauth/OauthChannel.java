package lib.oauth;

import org.json.JSONObject;
import lib.clients.OauthZoomClient;
import lib.cache.databaseData.Channel;
import lib.cache.databaseData.ChannelMember;
import lib.components.ChatChannelsComponent;
import lib.utils.Util;

import java.util.*;

/***********************************************************
 * OauthChannel class with channel related components
 * from lib/components/ChatChannelsComponent.java
 * translated into Oauth methods returning Lists of
 * Channel and ChannelMember objects rather than JSONObjects
 ***********************************************************/
public class OauthChannel {
    protected OauthZoomClient client;
    protected ChatChannelsComponent chatChannels =null;
    protected String userId = "me";
    protected String clientId;

    public OauthChannel(OauthZoomClient client) {
        this.client = client;
        refresh();
    }

    public void setClient(OauthZoomClient client){
        this.client = client;
        refresh();
    }

    private void refresh(){
        chatChannels = (ChatChannelsComponent) this.client.getChatChannels();
        this.clientId = client.getClientId();
    }

    protected String getCid(String to_channel){
        List<Channel> channels = listChannels();
        String cid = null;
        for (int i = 0; i<channels.size(); i++) {
            Channel c = channels.get(i);
            if(c.getChannelName().equals(to_channel)) cid = c.getChannelId();
        }
        if(cid == null) {
            throw new IllegalArgumentException("Invalid Channel Name");
        }
        return cid;
    }

    /***********************************
     * Channel components: Public APIs *
     ***********************************/

    public List<Channel> listChannels(){
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> params = new HashMap<>();
        List<Channel> channelList = new ArrayList<>();
        while(true){
            JSONObject res = chatChannels.listChannels(params);
            List<JSONObject> listObjs = Util.parseJsonData(res, "channels");
            for (int i = 0; i<listObjs.size(); i++) {
                String channelId = listObjs.get(i).getString("id");
                String channelName = listObjs.get(i).getString("name");
                String channelType = String.valueOf(listObjs.get(i).getInt("type"));

                // set values of ADS
                Map<String, String> values = new HashMap<>();
                values.put("clientId", this.clientId);
                values.put("channelId", channelId);
                values.put("channelName", channelName);
                values.put("channelType", channelType);

                Channel c = new Channel();
                c.setValues(values);
                channelList.add(c);
            }
            if(res.getString("next_page_token").length()>1){
                params.put("next_page_token", res.getString("next_page_token"));
            }
            else{
                break;
            }
        }
        return channelList;
    }

    public boolean createChannel(String cName, String cType) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String, String> data = new HashMap<>();
        data.put("channelName", cName);
        data.put("channelType", cType);
        JSONObject res = chatChannels.createChannel(data);
        int statusCode = res.getInt("status_code");
        return (statusCode == 201);
    }

    public Channel getChannel(String cName) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(cName);
        Map<String,String> params = new HashMap<>();
        params.put("channelId", cid);
        JSONObject res = chatChannels.getChannel(params);
        int statusCode = (int) res.get("status_code");
        Channel c = null;
        if (statusCode == 200) {
            c = new Channel();
            //set value
            String channelId = res.getString("id");
            String channelName = res.getString("name");
            String channelType = String.valueOf(res.getInt("type"));

            Map<String, String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", channelId);
            values.put("channelName", channelName);
            values.put("channelType", channelType);

            c.setValues(values);

            // update cache
        }
        return c;
    }

    public boolean updateChannel(String cName, String newCName) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(cName);
        Map<String,String> data = new HashMap<>();
        data.put("channelId", cid);
        data.put("channelName", newCName);
        JSONObject res = chatChannels.updateChannel(data);
        int statusCode = (int) res.get("status_code");
        return (statusCode == 204);
    }

    public boolean deleteChannel(String cName) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(cName);
        Map<String,String> params = new HashMap<>();
        params.put("channelId" , cid);
        JSONObject res = chatChannels.deleteChannel(params);
        int statusCode = (int) res.get("status_code");
        return (statusCode == 204);
    }

    /**************************************
     * Membership components: Public APIs *
     **************************************/

    public List<ChannelMember> listChannelMembers(String channelName){
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(channelName);
        Map<String,String> params = new HashMap<>();
        params.put("channelId", cid);
        List<ChannelMember> memberList = new ArrayList<>();
        while(true){
            // HTTP request get the data
            JSONObject res = chatChannels.listChannelMembers(params);
            List<JSONObject> listObjs = Util.parseJsonData(res, "members");
            for (int i = 0; i<listObjs.size(); i++) {
                String memberId = listObjs.get(i).getString("id");
                String email = listObjs.get(i).getString("email");
                String firstName = listObjs.get(i).getString("first_name");
                String lastName = listObjs.get(i).getString("last_name");

                // set values of ADS
                Map<String, String> values = new HashMap<>();
                values.put("clientId", this.clientId);
                values.put("channelId", cid);
                values.put("memberId", memberId);
                values.put("email", email);
                values.put("firstName", firstName);
                values.put("lastName", lastName);

                ChannelMember mem = new ChannelMember();
                mem.setValues(values);
                memberList.add(mem);
            }
            if(res.getString("next_page_token").length()>1){
                params.put("next_page_token", res.getString("next_page_token"));
            }
            else{
                break;
            }
        }
        return memberList;
    }

    public boolean inviteChannelMembers(String cName, String members) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(cName);
        Map<String,String> data = new HashMap<>();
        data.put("channleId", cid);
        data.put("members", members);
        JSONObject res = chatChannels.inviteChannelMembers(data);
        int statusCode = res.getInt("status_code");
        return (statusCode == 201);
    }

    public boolean joinChannel(String cName) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(cName);
        Map<String,String> data = new HashMap<>();
        data.put("channelId", cid);
        JSONObject res = chatChannels.joinChannel(data);
        int statusCode = res.getInt("status_code");
        return (statusCode == 201);
    }

    public boolean leaveChannel(String cName) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(cName);
        Map<String,String> params = new HashMap<>();
        params.put("channelId", cid);
        JSONObject res = chatChannels.leaveChannel(params);
        int statusCode = res.getInt("status_code");
        return (statusCode == 204);
    }

    public boolean removeMember(String cName, String memberId) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(cName);
        Map<String,String> params = new HashMap<>();
        params.put("channelId", cid);
        params.put("memberId", memberId);
        JSONObject res = chatChannels.removeMember(params);
        int statusCode = res.getInt("status_code");
        return (statusCode == 204);
    }
}
