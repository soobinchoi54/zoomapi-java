package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils.ApiClient;
import zoomapi.utils.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class WebinarComponent extends BaseComponent {
    public WebinarComponent(String base_uri, Map<String, String> config){
        super(base_uri, config);
    }

    public JSONObject listWebinar(Map<String,String> params){
        String[] keys = new String[] {"userId"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/users/%s/webinars", params.get("userId")), params);
    }

    public JSONObject createWebinar(Map<String,String> params){
        String[] keys = new String[] {"userId"};
        Util.requireKeys(params, keys);
        return this.postRequest(String.format("/users/%s/webinars", params.get("userId")), params);
    }

    public JSONObject updateWebinar(Map<String,String> params){
        String[] keys = new String[] {"webinarId"};
        Util.requireKeys(params, keys);
        return this.patchRequest(String.format("/webinars/%s", params.get("webinarId")), params);
    }

    public JSONObject deleteWebinar(Map<String,String> params){
        String[] keys = new String[] {"webinarId"};
        Util.requireKeys(params, keys);
        return this.deleteRequest(String.format("/webinars/%s", params.get("webinarId")), params);
    }

    public JSONObject endWebinar(Map<String,String> params){
        String[] keys = new String[] {"webinarId"};
        Util.requireKeys(params, keys);
        return this.putRequest(String.format("/webinars/%s/status", params.get("webinarId")), params);
    }

    public JSONObject getWebinar(Map<String,String> params){
        String[] keys = new String[] {"webinarId"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/webinars/%s", params.get("webinarId")), params);
    }

    public JSONObject registerWebinar(Map<String,String> params){
        String[] keys = new String[] {"webinarId", "email", "first_name", "last_name"};
        Util.requireKeys(params, keys);
        return this.postRequest(String.format("/webinars/%s/registrants", params.get("webinarId")), params);
    }
}

