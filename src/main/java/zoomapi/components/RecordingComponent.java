package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils.ApiClient;
import zoomapi.utils.Util;

import java.io.IOException;
import java.util.Map;

public class RecordingComponent extends BaseComponent{
    public RecordingComponent(String base_uri, Map<String, String> config){
        super(base_uri, config);
    }

    public JSONObject listRecording(Map<String,String> params) throws IOException {
        String[] keys = new String[] {"userId", "from", "to"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/users/%s/recordings", params.get("userId")), params);
    }

    public JSONObject getRecording(Map<String,String> params) throws IOException {
        String[] keys = new String[] {"meetingId"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/meetings/%s/recordings", params.get("meetingId")), params);
    }

    public JSONObject deleteRecording(Map<String,String> params) throws IOException {
        String[] keys = new String[] {"meetingId"};
        Util.requireKeys(params, keys);
        return this.deleteRequest(String.format("/meetings/%s/recordings", params.get("meetingId")), params);
    }
}
