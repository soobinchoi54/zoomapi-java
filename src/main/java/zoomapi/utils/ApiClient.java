package zoomapi.utils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;


public class ApiClient{
    // Zoom Api
    protected String base_uri = null;
    protected int timeout = 15;
    protected Map<String, String> config = new HashMap<>();

    protected ApiClient(String base_uri, int timeout){
        this.base_uri = base_uri;
        this.timeout = timeout;
    }

    protected int getTimeout(){
        return timeout;
    }

    protected void setTimeout(int val){
        try{
            val = (int) val;
        }catch(Exception e){
            throw new IllegalArgumentException("timeout value must be an integer");
        }
        this.timeout = val;
    }

    protected String getBaseUri(){
        return base_uri;
    }

    protected void setBaseUri(String val){
        if(val != null && val.endsWith("/")){
            val = val.substring(0, val.length()-1);
        }
        this.base_uri = val;
    }

    protected String urlFor(String end_point){
        if(!end_point.startsWith("/")) end_point = String.format("/%s", end_point);
        if(end_point.endsWith("/")) end_point = end_point.substring(0, end_point.length()-1);
        return String.format("%1$s%2$s", base_uri, end_point);
    }

    protected JSONObject getRequest(String end_point, Map<String, String> params) throws IOException {
        String url = urlFor((end_point));

        // send GET request
        URL url_for_request = new URL(url+"?"+Util.parseParams(params));
        HttpURLConnection conn = (HttpURLConnection) url_for_request.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(150);
        System.out.println(this.config.get("token"));
        conn.setRequestProperty("Authorization", String.format("Bearer %s", this.config.get("token")));
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");

        //read the data
        JSONObject response = Util.readResponse(conn);
        return response;
    }

    protected JSONObject postRequest(){
        return null;
    }

    protected JSONObject patchRequest(){
        return null;
    }

    protected JSONObject deleteRequest(){
        return null;
    }

    protected JSONObject putRequest(){
        return null;
    }
}
