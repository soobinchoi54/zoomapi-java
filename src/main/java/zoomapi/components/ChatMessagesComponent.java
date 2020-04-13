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
    public JSONObject listMessages(Map<String,String> params) throws IOException {
        String[] keys = new String[] {"userId"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/chat/users/%s/messages", params.get("userId")), params);
    }
    public JSONObject sendMessage(Map<String,String> data) throws IOException {
        String[] keys = new String[] {"messageId"};
        Util.requireKeys(data, keys);
        return this.postRequest(String.format("/chat/users/me/messages"), data);
    }
    public JSONObject updateMessage(Map<String, String> data) throws IOException{
        String[] keys1 = new String[]{"messageId", "to_channel", "message"};
        String[] keys2 = new String[]{"messageId", "to_contact", "message"};
        if(data.containsKey("to_channel")) Util.requireKeys(data, keys1);
        else Util.requireKeys(data, keys2);
        return this.putRequest(String.format("/chat/users/me/messages/%s", data.get("messageId")), data);
    }
    public JSONObject deleteMessage(Map<String,String> params) throws IOException {
        String[] keys = new String[] {"messageId"};
        Util.requireKeys(params, keys);
        return this.deleteRequest(String.format("/chat/users/me/messages/s%", params.get("messageId")), params);
    }
}