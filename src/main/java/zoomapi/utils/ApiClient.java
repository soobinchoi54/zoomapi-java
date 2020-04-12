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
    private String base_uri = null;
    private int timeout = 15;
    private Map<String, Object> config = new HashMap<>();
    public ApiClient(String ...kwargs){

    }

    public ApiClient(String base_uri, int timeout, String ...kwargs){
        this.base_uri = base_uri;
        this.timeout = timeout;
    }

    public int getTimeout(){
        return timeout;
    }

    public void setTimeout(int val){
        try{
            val = (int) val;
        }catch(Exception e){
            throw new IllegalArgumentException("timeout value must be an integer");
        }
        this.timeout = val;
    }

    public String getBaseUri(){
        return base_uri;
    }

    public void setBaseUri(String val){
        if(val != null && val.endsWith("/")){
            val = val.substring(0, val.length()-1);
        }
        this.base_uri = val;
    }

    public String urlFor(String end_point){
        if(!end_point.startsWith("/")) end_point = String.format("/%s", end_point);
        if(end_point.endsWith("/")) end_point = end_point.substring(0, end_point.length()-1);
        return String.format("%1$s%2$s", base_uri, end_point);
    }

    public JSONObject getRequest(String end_point, Map<String, String> params) throws IOException {
        URL url_for_request = new URL(urlFor(end_point));
        HttpURLConnection conn = (HttpURLConnection) url_for_request.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(this.timeout);
        conn.setRequestProperty("Authorization", String.format("Bearer %s", this.config.get("token")));
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");

        if(params != null){
            conn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(Util.parseMapToString(params));
            out.flush();
            out.close();
        }

        //read the data
        InputStream is = new BufferedInputStream(conn.getInputStream());
        JSONObject response = new JSONObject(is.toString());
        return response;
    }

    public JSONObject postRequest(){
        return null;
    }

    public JSONObject patchRequest(){
        return null;
    }

    public JSONObject deleteRequest(){
        return null;
    }

    public JSONObject putRequest(){
        return null;
    }
}
