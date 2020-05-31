package lib.oauth;

import org.json.JSONObject;
import lib.clients.OauthZoomClient;
import lib.cache.databaseData.Channel;
import lib.cache.databaseData.ChannelMember;
import lib.components.ChatChannelsComponent;
import lib.utils.Util;

import java.util.*;

public class OauthChannel {
    protected OauthZoomClient client;
    protected ChatChannelsComponent chat_channels =null;
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
        chat_channels = (ChatChannelsComponent) this.client.getChatChannels();
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

    public List<Channel> listChannels(){
        if (chat_channels == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> params = new HashMap<>();
        List<Channel> channelList = new ArrayList<>();
        while(true){
            JSONObject res = chat_channels.listChannels(params);
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

    public List<ChannelMember> listChannelMembers(String channelName){
        if (chat_channels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(channelName);
        Map<String,String> params = new HashMap<>();
        params.put("channelId", cid);
        List<ChannelMember> memberList = new ArrayList<>();
        while(true){
            // HTTP request get the data
            JSONObject res = chat_channels.listChannelMembers(params);
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
}
