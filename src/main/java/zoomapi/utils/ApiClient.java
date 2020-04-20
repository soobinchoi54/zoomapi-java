package zoomapi.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;


public class ApiClient{
    // Zoom Api
    protected String base_uri = null;
    protected int timeout = 1500;
    protected static Throttle throttle = new Throttle();
    protected Map<String, String> config = new HashMap<>();

    protected ApiClient(String base_uri, int timeout){
        this.base_uri = base_uri;
        this.timeout = timeout;
        throttle = new Throttle();
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

    protected JSONObject getRequest(String end_point, Map<String, String> params){
        // throttle the Request to make sure no more then 2 requests will be executed within the same 0.1 sec window
        this.throttle.throttled();
        String url = urlFor((end_point));
        try{
            // send GET request
            URL url_for_request = new URL(url+"?"+Util.parseParams(params));
            HttpURLConnection conn = (HttpURLConnection) url_for_request.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1500);
            // System.out.println(this.config.get("token"));
            conn.setRequestProperty("Authorization", String.format("Bearer %s", this.config.get("token")));
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            //read the data
            JSONObject response = Util.readResponse(conn);
            response.put("status_code", conn.getResponseCode());
            return response;
        } catch(IOException e){
            System.out.println("Get Request failed: " + e);
            return null;
        }

    }

    protected JSONObject postRequest(String end_point, Map<String, String> data){
        // throttle the Request to make sure no more then 2 requests will be executed within the same 0.1 sec window
        this.throttle.throttled();
        String url = urlFor((end_point));
        try{
            // send POST request
            URL url_for_request = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url_for_request.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(1500);
            // System.out.println(this.config.get("token"));
            conn.setRequestProperty("Authorization", String.format("Bearer %s", this.config.get("token")));
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            if(data!=null){
                conn.setDoOutput(true);

                // convert data to json object
                JSONObject body = new JSONObject();
                for(Map.Entry<String, String> e:data.entrySet()){
                    body.put(e.getKey(), e.getValue());
                }
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                // send json string
                wr.write(body.toString());
                wr.flush();
                wr.close();

            }
            //read the data
            JSONObject response = Util.readResponse(conn);
            response.put("status_code", conn.getResponseCode());
            return response;
        } catch(IOException e){
            System.out.println("Post Request failed: " + e);
            return null;
        }

    }

    protected JSONObject patchRequest(String end_point, Map<String, String> data){
        // throttle the Request to make sure no more then 2 requests will be executed within the same 0.1 sec window
        this.throttle.throttled();
        String url = urlFor((end_point));

        try{
            JSONObject body = new JSONObject();
            for(Map.Entry<String, String> e:data.entrySet()){
                body.put(e.getKey(), e.getValue());
            }

            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(1500)
                    .setConnectionRequestTimeout(1500)
                    .setSocketTimeout(1500).build();
            CloseableHttpClient client =  HttpClientBuilder.create().setDefaultRequestConfig(config).build();
            HttpPatch httpPatch = new HttpPatch(new URI(url));
            httpPatch.setHeader("Authorization", String.format("Bearer %s", this.config.get("token")));
            httpPatch.addHeader("Content-Type", "application/json");
            StringEntity response_body = new StringEntity(body.toString());
            httpPatch.setEntity(response_body);
            HttpResponse receive = client.execute(httpPatch);
            //read the data
            JSONObject response = new JSONObject();
            response.put("status_code", receive.getStatusLine().getStatusCode());
            return response;
        } catch(IOException | URISyntaxException e){
            System.out.println("Patch Request failed: " + e);
            return null;
        }


        /****************************************************************************************
         * Following code is deprecated since HttpURLConnection doesn't support patch method
         ****************************************************************************************/
//        // send PATCH request
//        URL url_for_request = new URL(url);
//        HttpURLConnection conn = (HttpURLConnection) url_for_request.openConnection();
//        conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
//        conn.setRequestMethod("POST");
//        conn.setConnectTimeout(1500);
//        // System.out.println(this.config.get("token"));
//        conn.setRequestProperty("Authorization", String.format("Bearer %s", this.config.get("token")));
//        conn.setRequestProperty("Content-Type", "application/json; utf-8");
//        conn.setRequestProperty("Accept", "application/json");
//        if(data!=null){
//            conn.setDoOutput(true);
//
//            // convert data to json object
//            JSONObject body = new JSONObject();
//            for(Map.Entry<String, String> e:data.entrySet()){
//                body.put(e.getKey(), e.getValue());
//            }
//            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            // send json string
//            wr.write(body.toString());
//            wr.flush();
//            wr.close();
//
//        }

    }

    protected JSONObject deleteRequest(String end_point, Map<String, String> params){
        // throttle the Request to make sure no more then 2 requests will be executed within the same 0.1 sec window
        this.throttle.throttled();
        String url = urlFor((end_point));
        try{
            // send DELETE request
            URL url_for_request = new URL(url+"?"+Util.parseParams(params));
            HttpURLConnection conn = (HttpURLConnection) url_for_request.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(1500);
            // System.out.println(this.config.get("token"));
            conn.setRequestProperty("Authorization", String.format("Bearer %s", this.config.get("token")));
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            //read the data
            JSONObject response = new JSONObject();
            response.put("status_code", conn.getResponseCode());
            return response;
        } catch(IOException e){
            System.out.println("Delete Request failed: " + e);
            return null;
        }
    }

    protected JSONObject putRequest(String end_point, Map<String, String> data){
        // throttle the Request to make sure no more then 2 requests will be executed within the same 0.1 sec window
        this.throttle.throttled();
        String url = urlFor((end_point));
        try{
            // send PUT request
            URL url_for_request = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url_for_request.openConnection();
            conn.setRequestMethod("PUT");
            conn.setConnectTimeout(1500);
            // System.out.println(this.config.get("token"));
            conn.setRequestProperty("Authorization", String.format("Bearer %s", this.config.get("token")));
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            if(data!=null){
                conn.setDoOutput(true);

                // convert data to json object
                JSONObject body = new JSONObject();
                for(Map.Entry<String, String> e:data.entrySet()){
                    body.put(e.getKey(), e.getValue());
                }
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                // send json string
                wr.write(body.toString());
                wr.flush();
                wr.close();

            }
            //read the data
            JSONObject response = new JSONObject();
            response.put("status_code", conn.getResponseCode());
            return response;
        } catch(IOException e){
            System.out.println("Put Request failed: " + e);
            return null;
        }

    }
}
