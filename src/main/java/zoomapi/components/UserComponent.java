package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils.ApiClient;
import zoomapi.utils.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class UserComponent extends BaseComponent{
    public UserComponent(String base_uri, Map<String, String> config){
        super(base_uri, config);
    }

    public JSONObject listUsers(Map<String,String> params){
        return this.getRequest(String.format("/users"), params);
    }

    public JSONObject createUsers(Map<String,String> params){
        return this.postRequest(String.format("/users"), params);
    }

    public JSONObject updateUser(Map<String,String> params){
        String[] keys = new String[] {"userId"};
        Util.requireKeys(params, keys);
        return this.patchRequest(String.format("/users/%s", params.get("userId")), params);
    }

    public JSONObject deleteUser(Map<String,String> params){
        String[] keys = new String[] {"userId"};
        Util.requireKeys(params, keys);
        return this.deleteRequest(String.format("/users/%s", params.get("userId")), params);
    }

    public JSONObject getUser(Map<String,String> params){
        String[] keys = new String[] {"userId"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/users/%s", params.get("userId")), params);
    }
}
