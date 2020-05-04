package zoomapi.botAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import zoomapi.OauthZoomClient;
import zoomapi.components.ChatChannelsComponent;
import zoomapi.components.ChatMessagesComponent;
import zoomapi.utils.Member;
import zoomapi.utils.Util;

import java.util.*;

public class OauthChannel {
    OauthZoomClient client;
    private ChatChannelsComponent chat_channels =null;
    private String userId = "me";

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
    }


    public List<Member> listChannelMembers(String channelName){
        if (chat_channels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(channelName);
        Map<String,String> params = new HashMap<>();
        params.put("channelId", cid);
        JSONObject res = chat_channels.listChannelMembers(params);
        List<Member> member_list = new ArrayList<>();
        List<JSONObject> listObjs = parseJsonData(res, "members");
        Member mem;
        for (int i = 0; i<listObjs.size(); i++) {
            String id = listObjs.get(i).getString("id");
            String email = listObjs.get(i).getString("email");
            String first_name = listObjs.get(i).getString("first_name");
            String last_name = listObjs.get(i).getString("last_name");
            String role = listObjs.get(i).getString("role");
            mem = new Member(id, email, first_name, last_name, role);
            member_list.add(mem);
        }
        if(res.getString("next_page_token").length()>1){
            params.put("next_page_token", res.getString("next_page_token"));
        }
        else{
            params.remove("next_page_token");
        }
        return member_list;
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
