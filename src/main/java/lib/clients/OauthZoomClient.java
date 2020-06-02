package lib.clients;

import lib.cache.databaseData.Credential;
import lib.cache.tables.CredentialTable;
import lib.components.BaseComponent;
import lib.utils.Util;
import lib.components.ChatChannelsComponent;
import lib.components.ChatMessagesComponent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OauthZoomClient extends ZoomClient{
    // Zoom Oauth api java client

    public OauthZoomClient(String client_id, String client_secret, int PORT, String redirect_url, String browser_path){
        super(client_id, client_secret);

        // specify config details
        this.config.put("client_id", client_id);
        this.config.put("client_secret", client_secret);
        this.config.put("PORT", String.valueOf(PORT));
        this.config.put("redirect_url", redirect_url);
        this.config.put("browser_path", browser_path);
        refreshToken();
        this.components.put("chat_channels", new ChatChannelsComponent(this.base_uri, this.config));
        this.components.put("chat_messages", new ChatMessagesComponent(this.base_uri, this.config));
    }

    public OauthZoomClient(String client_id, String client_secret, int PORT, String redirect_url, String browser_path, String data_type, int time_out){
        super(client_id, client_secret, data_type, time_out);
        this.config.put("client_id", client_id);
        this.config.put("client_secret", client_secret);
        this.config.put("PORT", String.valueOf(PORT));
        this.config.put("redirect_url", redirect_url);
        this.config.put("browser_path", browser_path);
//        this.config.put("token", Util.getOauthToken(this.config.get("client_id"), this.config.get("client_secret"), this.config.get("PORT"), this.config.get("redirect_url"), this.config.get("browser_path")));
        refreshToken();
        this.components.put("chat_channels", new ChatChannelsComponent(this.base_uri, this.config));
        this.components.put("chat_messages", new ChatMessagesComponent(this.base_uri, this.config));
    }

    protected void refreshToken(){
        String token = null;
        // get current time
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();

        // get cache
        CredentialTable table = CredentialTable.getInstance();
        List<Credential> data = table.get(new String[]{"clientId", "userSecret"}, new String[]{this.config.get("client_id"), this.config.get("client_secret")});
        if(data.size()!=0){
            // compare timeStamp
            Credential user =  data.get(0);
            String stamp = user.getTimeStamp();
            Date stampDate = new Date();
            try {
                stampDate = df.parse(stamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            calendar.setTime(stampDate);
            calendar.add(Calendar.MINUTE, 40);
            Date after =  calendar.getTime(); // the timeStamp time + 40 minutes

            // compare `after` with now
            if(now.before(after)) { // less than 40 minutes
                token = user.getOauthToken();
                this.config.put("token", token);
                System.out.println("Token: "+token);
                return;
            }
        }

        // no cache data or timeStamp more than 30 minutes
        System.out.println(this.config.get("client_id")+" "+this.config.get("client_secret")+" "+this.config.get("PORT")+" "+this.config.get("redirect_url")+" "+this.config.get("browser_path"));
        token = Util.getOauthToken(this.config.get("client_id"), this.config.get("client_secret"), this.config.get("PORT"), this.config.get("redirect_url"), this.config.get("browser_path"));

        // token accessing succeeded
        if(token.length() > 1){

            // update existing Credential
            if(data.size()!=0) table.update(new String[]{"oauthToken", "timeStamp"}, new String[]{token, df.format(now)}, new String[]{"clientId", "userSecret"}, new String[]{this.config.get("client_id"), this.config.get("client_secret")});
            else{ // add new Credential
                Credential c = new Credential();
                // add one
                Map<String, String> values = new HashMap<>();
                values.put("clientId", this.config.get("client_id"));
                values.put("userSecret", this.config.get("client_secret"));
                values.put("oauthToken", token);
                values.put("timeStamp", df.format(now));
                c.setValues(values);
                table.add(c);
            }
            this.config.put("token", token);
        }
        else{
            // token accessing failed
            System.out.println("Authorization failed");
        }

    }

    public String getRedirectUrl(){
        return this.config.get("redirect_url");
    }

    public void setRedirectUrl(String redirect_url){
        this.config.put("redirect_url", redirect_url);
        refreshToken();
    }

    public BaseComponent getChatChannels(){
        return this.components.get("chat_channels");
    }

    public BaseComponent getChatMessages(){
        return this.components.get("chat_messages");
    }

    public String getClientId() {return this.config.get("client_id");}
}
