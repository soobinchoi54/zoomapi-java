package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils.ApiClient;
import zoomapi.utils.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class MeetingComponent extends BaseComponent{
    public MeetingComponent(String base_uri, Map<String, String> config){
        super(base_uri, config);
    }

    public JSONObject listMeetings(Map<String,String> params){
        String[] keys = new String[] {"userId"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/users/%s/meetings", params.get("userId")), params);
    }

    public JSONObject createMeeting(Map<String,String> params){
        String[] keys = new String[] {"userId", "start_time"}; //Scheduled Meeting
        Util.requireKeys(params, keys);
        return this.postRequest(String.format("/users/%s/meetings", params.get("userId")), params);
    }

    public JSONObject getMeeting(Map<String,String> params){
        String[] keys = new String[] {"meetingId"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/meetings/%s", params.get("meetingId")), params);
    }

    public JSONObject updateMeeting(Map<String,String> params){
        String[] keys = new String[] {"meetingId", "start_time"};
        Util.requireKeys(params, keys);
        return this.patchRequest(String.format("/meetings/%s", params.get("meetingId")), params);
    }

    public JSONObject deleteMeeting(Map<String,String> params){
        String[] keys = new String[] {"meetingId"};
        Util.requireKeys(params, keys);
        return this.deleteRequest(String.format("/meetings/%s", params.get("meetingId")), params);
    }
}
