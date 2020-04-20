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
        String[] keys1 = new String[] {"userId", "to_channel"};
        String[] keys2 = new String[] {"userId", "to_contact"};
        if (params.containsKey("to_channel")) Util.requireKeys(params, keys1);
        else Util.requireKeys(params, keys2);
        return this.getRequest(String.format("/chat/users/%s/messages", params.get("userId")), params);
    }
    public JSONObject sendMessage(Map<String,String> data){
        String[] keys1 = new String[] {"message", "to_channel"};
        String[] keys2 = new String[] {"message", "to_contact"};
        if (data.containsKey("to_channel")) Util.requireKeys(data, keys1);
        else Util.requireKeys(data, keys2);
        return this.postRequest(String.format("/chat/users/me/messages"), data);
    }
    public JSONObject updateMessage(Map<String, String> data){
        String[] keys1 = new String[]{"messageId", "to_channel", "message"};
        String[] keys2 = new String[]{"messageId", "to_contact", "message"};
        if(data.containsKey("to_channel")) Util.requireKeys(data, keys1);
        else Util.requireKeys(data, keys2);
        return this.putRequest(String.format("/chat/users/me/messages/%s", data.get("messageId")), data);
    }
    public JSONObject deleteMessage(Map<String,String> params){
        String[] keys1 = new String[] {"messageId", "to_channel"};
        String[] keys2 = new String[] {"messageId", "to_contact"};
        if (params.containsKey("to_channel")) Util.requireKeys(params, keys1);
        else Util.requireKeys(params, keys2);
        return this.deleteRequest(String.format("/chat/users/me/messages/%s", params.get("messageId")), params);
    }
}