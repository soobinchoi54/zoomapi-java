package lib.components;

import org.json.JSONObject;
import lib.utils.Util;

import java.util.Map;

public class RecordingComponent extends BaseComponent{
    public RecordingComponent(String base_uri, Map<String, String> config){
        super(base_uri, config);
    }

    public JSONObject listRecording(Map<String,String> params){
        String[] keys = new String[] {"userId"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/users/%s/recordings", params.get("userId")), params);
    }

    public JSONObject getRecording(Map<String,String> params){
        String[] keys = new String[] {"meetingId"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/meetings/%s/recordings", params.get("meetingId")), params);
    }

    public JSONObject deleteRecording(Map<String,String> params){
        String[] keys = new String[] {"meetingId"};
        Util.requireKeys(params, keys);
        return this.deleteRequest(String.format("/meetings/%s/recordings", params.get("meetingId")), params);
    }
}
