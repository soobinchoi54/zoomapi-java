package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils.ApiClient;

import java.io.IOException;
import java.util.Map;

public class UserComponent extends BaseComponent{
    public UserComponent(String base_uri, Map<String, String> config){
        super(base_uri, config);
    }
    public JSONObject listUser() throws IOException {
        return this.getRequest("/users/me", null);
    }
}
