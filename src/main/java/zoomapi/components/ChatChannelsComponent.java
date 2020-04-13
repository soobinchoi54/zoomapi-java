package zoomapi.components;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class ChatChannelsComponent extends BaseComponent{
    public ChatChannelsComponent(String base_uri, Map<String, String> config){
        super(base_uri, config);
    }

    public JSONObject listChannels() throws IOException {
        return this.getRequest("/chat/users/me/channels", null);
    }
}
