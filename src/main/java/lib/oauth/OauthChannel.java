package lib.oauth;

import lib.cache.databaseData.ChannelMessage;
import lib.cache.tables.ChannelMemberTable;
import lib.cache.tables.ChannelMessageTable;
import lib.cache.tables.ChannelTable;
import lib.cache.utils.CacheHelper;
import org.json.JSONObject;
import lib.clients.OauthZoomClient;
import lib.cache.databaseData.Channel;
import lib.cache.databaseData.ChannelMember;
import lib.components.ChatChannelsComponent;
import lib.utils.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    protected Channel getCid(String channelName){
        List<Channel> channels = listChannels();
        Channel ans = null;
        for (int i = 0; i<channels.size(); i++) {
            Channel c = channels.get(i);
            if(c.getChannelName().equals(channelName)) ans = c;
        }
        if(ans == null) {
            throw new IllegalArgumentException("Invalid Channel Name");
        }
        return ans;
    }

    protected Channel getName(String channelId){
        List<Channel> channels = listChannels();
        Channel ans = null;
        for (int i = 0; i<channels.size(); i++) {
            Channel c = channels.get(i);
            if(c.getChannelId().equals(channelId)) ans = c;
        }
        if(ans == null) {
            throw new IllegalArgumentException("Invalid Channel Name");
        }
        return ans;
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

        if(channelList.size()!=0){
            // update cache strategy: delete all, add all
            Channel[] channels = new Channel[channelList.size()];
            CacheHelper<ChannelTable, Channel> cache = new CacheHelper<>(ChannelTable.class);
            cache.update(new String[]{"clientId"}, new String[]{this.clientId}, channelList.toArray(channels), Channel.class);
        }

        return channelList;
    }

    public List<Channel> listChannels(boolean usingCache){
        if(!usingCache) return listChannels();

        // fetch from cache
        ChannelTable table = ChannelTable.getInstance().getInstance();
        List<Channel> channelList = table.get(new String[]{"clientId"}, new String[]{this.clientId});
        return channelList;
    }

    public Channel createChannel(String channelName, String channelType) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String, String> data = new HashMap<>();
        data.put("name", channelName);
        data.put("type", channelType);
        JSONObject res = chatChannels.createChannel(data);
        int statusCode = res.getInt("status_code");
        Channel c = null;
        System.out.println(statusCode);
        // websites says 201, while it is giving me 200 :)
        if(statusCode == 200){
            c = new Channel();
            Map<String, String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", res.getString("id"));
            values.put("channelName", res.getString("name"));
            values.put("channelType", String.valueOf(res.getInt("type")));
            c.setValues(values);
            // update cache strategy: delete all, add all
            CacheHelper<ChannelTable, Channel> cache = new CacheHelper<>(ChannelTable.class);
            cache.update(new String[]{"clientId", "channelName"}, new String[]{this.clientId, channelName}, new Channel[]{c}, Channel.class);
        }

        return c;
    }

    public Channel getChannel(String channelName) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(channelName).getChannelId();
        Map<String,String> params = new HashMap<>();
        params.put("channelId", cid);
        JSONObject res = chatChannels.getChannel(params);
        int statusCode = (int) res.get("status_code");
        Channel c = null;
        System.out.println(statusCode);
        if (statusCode == 200) {
            c = new Channel();
            //set value
            String channelId = res.getString("id");
            String cName = res.getString("name");
            String channelType = String.valueOf(res.getInt("type"));

            Map<String, String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", channelId);
            values.put("channelName", cName);
            values.put("channelType", channelType);
            c.setValues(values);

            // update cache
            CacheHelper<ChannelTable, Channel> cache = new CacheHelper<>(ChannelTable.class);
            cache.update(new String[]{"clientId", "channelName"}, new String[]{this.clientId, channelName}, new Channel[]{c}, Channel.class);
        }
        return c;
    }

    public Channel getChannel(String channelName, boolean usingCache){
        if(!usingCache) getChannel(channelName);

        // fetch from cache
        ChannelTable table = ChannelTable.getInstance().getInstance();
        List<Channel> channelList = table.get(new String[]{"clientId", "channelName"}, new String[]{this.clientId, channelName});
        return channelList.size()!=0?channelList.get(0):null;
    }

    public Channel updateChannel(String channelName, String newChannelName) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        Channel target = getCid(channelName);
        String cid = target.getChannelId();
        Map<String,String> data = new HashMap<>();
        data.put("channelId", cid);
        data.put("name", newChannelName);
        JSONObject res = chatChannels.updateChannel(data);
        int statusCode = (int) res.get("status_code");
        System.out.println(statusCode);
        Channel c = null;
        if (statusCode == 204){
            c = new Channel();
            //set value
            Map<String, String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", cid);
            values.put("channelName", newChannelName);
            values.put("channelType", target.getChannelType());
            c.setValues(values);

            // update cache
            CacheHelper<ChannelTable, Channel> cache = new CacheHelper<>(ChannelTable.class);
            cache.update(new String[]{"clientId", "channelName"}, new String[]{this.clientId, channelName}, new Channel[]{c}, Channel.class);
        }
        return c;
    }

    public Channel deleteChannel(String channelName) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        Channel target = getCid(channelName);
        String cid = target.getChannelId();
        Map<String,String> params = new HashMap<>();
        params.put("channelId" , cid);
        JSONObject res = chatChannels.deleteChannel(params);
        int statusCode = (int) res.get("status_code");
        Channel c = null;
        System.out.println(statusCode);
        if (statusCode == 204){
            c = new Channel();
            Map<String,String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", cid);
            values.put("channelName", channelName);
            values.put("channelType", target.getChannelType());
            c.setValues(values);
            // update cache
            CacheHelper<ChannelTable, Channel> cache = new CacheHelper<>(ChannelTable.class);
            cache.update(new String[]{"clientId", "channelName"}, new String[]{this.clientId, channelName}, new Channel[]{}, Channel.class);
        }
        return c;
    }

    /**************************************
     * Membership components: Public APIs *
     **************************************/

    public List<ChannelMember> listChannelMembers(String channelName){
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        String cid = getCid(channelName).getChannelId();
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
                values.put("channelName", channelName);
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

        if(memberList.size()!=0){
            // update cache strategy: delete all, add all
            ChannelMember[] members = new ChannelMember[memberList.size()];
            CacheHelper<ChannelMemberTable, ChannelMember> cache = new CacheHelper<>(ChannelMemberTable.class);
            cache.update(new String[]{"clientId", "channelName"}, new String[]{this.clientId, channelName}, memberList.toArray(members), ChannelMember.class);
        }
        return memberList;
    }

    public List<ChannelMember> listChannelMembers(String channelName, boolean usingCache){
        if(!usingCache) return listChannelMembers(channelName);

        // fetch from cache
        ChannelMemberTable table = ChannelMemberTable.getInstance();
        List<ChannelMember> channelList = table.get(new String[]{"clientId", "channelName"}, new String[]{this.clientId, channelName});
        return channelList;
    }

    public List<ChannelMember> inviteChannelMembers(String channelName, String[] members) {
        if(members==null||members.length>5||members.length==0) return null;
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");

        List<ChannelMember> memberList = null;
        String cid = getCid(channelName).getChannelId();
        final StringBuilder sb = new StringBuilder();
        for(String member:members){
            sb.append("email:"+member+"###");
        }
        Map<String,String> data = new HashMap<>();
        data.put("channelId", cid);
        data.put("members", sb.toString());
        JSONObject res = chatChannels.inviteChannelMembers(data);
        int statusCode = res.getInt("status_code");
        System.out.println(statusCode);
        if (statusCode == 200){
            memberList = new ArrayList<>(members.length);
            String[] ids = res.getString("ids").split(",");
            for(int i = 0; i < members.length; i++){
                ChannelMember cm = new ChannelMember();
                Map<String, String> values = new HashMap<>();
                values.put("clientId", this.clientId);
                values.put("channelId", cid);
                values.put("channelName", channelName);
                values.put("memberId", ids[i]);
                values.put("email", members[i]);
                cm.setValues(values);
                memberList.add(cm);

                // update cache
                CacheHelper<ChannelMemberTable, ChannelMember> cache = new CacheHelper<>(ChannelMemberTable.class);
                cache.update(new String[]{"clientId", "channelName", "email"}, new String[]{this.clientId, channelName, members[i]}, new ChannelMember[]{cm}, ChannelMember.class);
            }
        }

        return memberList;
    }

    public Channel joinChannel(String channelId) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        Map<String,String> data = new HashMap<>();
        data.put("channelId", channelId);
        JSONObject res = chatChannels.joinChannel(data);
        int statusCode = res.getInt("status_code");
        Channel c = null;
        System.out.println(statusCode);
        // websites says 201, while it is giving me 200 :)
        if (statusCode == 200){
            c = getName(channelId);
            // update cache
            CacheHelper<ChannelTable, Channel> cache = new CacheHelper<>(ChannelTable.class);
            cache.update(new String[]{"clientId", "channelName"}, new String[]{this.clientId, c.getChannelName()}, new Channel[]{c}, Channel.class);
        }
        return c;
    }

    public Channel leaveChannel(String channelName) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        Channel target = getCid(channelName);
        String cid = target.getChannelId();
        Map<String,String> params = new HashMap<>();
        params.put("channelId", cid);
        JSONObject res = chatChannels.leaveChannel(params);
        int statusCode = res.getInt("status_code");
        Channel c = null;
        System.out.println(statusCode);
        if (statusCode == 204){
            c = target;
            // update cache
            CacheHelper<ChannelTable, Channel> cache = new CacheHelper<>(ChannelTable.class);
            cache.update(new String[]{"clientId", "channelName"}, new String[]{this.clientId, c.getChannelName()}, new Channel[]{}, Channel.class);
        }
        return c;
    }

    public ChannelMember removeMember(String channelName, String email) {
        if (chatChannels == null) throw new IllegalStateException("Uninitialized OauthClient");
        Channel target = getCid(channelName);
        String cid = target.getChannelId();
        Map<String,String> params = new HashMap<>();
        params.put("channelId", cid);
        params.put("memberId", email);
        JSONObject res = chatChannels.removeMember(params);
        int statusCode = res.getInt("status_code");
        System.out.println(statusCode);
        ChannelMember cm = null;
        if (statusCode == 204){
            cm = new ChannelMember();
            Map<String, String> values = new HashMap<>();
            values.put("clientId", this.clientId);
            values.put("channelId", cid);
            values.put("channelName", channelName);
            values.put("email", email);
            cm.setValues(values);
            // update cache strategy: delete all, add all
            CacheHelper<ChannelMemberTable, ChannelMember> cache = new CacheHelper<>(ChannelMemberTable.class);
            cache.update(new String[]{"clientId", "channelName", "email"}, new String[]{this.clientId, channelName, email}, new ChannelMember[]{}, ChannelMember.class);
        }

        return cm;
    }
}

