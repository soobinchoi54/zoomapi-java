package zoomapi.components;
import jdk.security.jarsigner.JarSigner;
import org.json.JSONObject;
import zoomapi.utils.ApiClient;
import zoomapi.utils.Util;
import java.io.IOException;
import java.util.Map;
public class ChatMessagesComponent extends BaseComponent{
    public ChatMessagesComponent(String base_uri, Map<String, String> config){
        super(base_uri, config);
    }
    public JSONObject listMessages(Map<String,String> params){
        if (params.containsKey("to_channel")) Util.requireKeys(params, new String[] {"userId", "to_channel"});
        else Util.requireKeys(params, new String[] {"userId", "to_contact"});
        return this.getRequest(String.format("/chat/users/%s/messages", params.get("userId")), params);
    }
    public JSONObject sendMessage(Map<String,String> data){
        if (data.containsKey("to_channel")) Util.requireKeys(data, new String[] {"message", "to_channel"});
        else Util.requireKeys(data, new String[] {"message", "to_contact"});
        return this.postRequest(String.format("/chat/users/me/messages"), data);
    }
    public JSONObject updateMessage(Map<String, String> data){
        if(data.containsKey("to_channel")) Util.requireKeys(data, new String[]{"messageId", "to_channel", "message"});
        else Util.requireKeys(data, new String[]{"messageId", "to_contact", "message"});
        return this.putRequest(String.format("/chat/users/me/messages/%s", data.get("messageId")), data);
    }
    public JSONObject deleteMessage(Map<String,String> params){
        if (params.containsKey("to_channel")) Util.requireKeys(params, new String[] {"messageId", "to_channel"});
        else Util.requireKeys(params, new String[] {"messageId", "to_contact"});
        return this.deleteRequest(String.format("/chat/users/me/messages/%s", params.get("messageId")), params);
    }
}