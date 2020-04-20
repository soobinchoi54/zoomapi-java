package zoomapi.components;

import org.json.JSONObject;
import zoomapi.utils.ApiClient;
import zoomapi.utils.Util;

import java.io.IOException;
import java.util.Map;

public class ReportComponent extends BaseComponent{
    public ReportComponent(String base_uri, Map<String, String> config){
        super(base_uri, config);
    }

    public JSONObject getUserReport(Map<String,String> params) throws IOException {
        String[] keys = new String[] {"userId", "start_time", "end_time"};
        Util.requireKeys(params, keys);
        params.put("from", params.get("start_time"));
        params.put("to", params.get("end_time"));
        return this.getRequest(String.format("/report/users/%s/meetings", params.get("userId")), params);
    }

    public JSONObject getAccountReport(Map<String,String> params) throws IOException {
        String[] keys = new String[] {"from", "to"};
        Util.requireKeys(params, keys);
        return this.getRequest(String.format("/report/users", params.get("userId")), params);
    }
}
