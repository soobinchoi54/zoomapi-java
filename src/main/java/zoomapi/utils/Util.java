package zoomapi.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;

public class Util {

    public static void ignored(Exception ...exps){
        //
    }

    public static boolean isStringType(Object val){
        return val instanceof String;
    }

    public static boolean requireKeys(Map<String, String> data, String[] keys){
        for(String key: keys){
            if (!data.containsKey(key))
                throw new IllegalArgumentException(String.format("%s must be set", key));
        }
        return true;
    }

    public static boolean requireKeys(Map<String, String> data, String[] keys, boolean allow_null){
        for(String key: keys){
            if (!data.containsKey(key))
                throw new IllegalArgumentException(String.format("%s must be set", key));
            if (!allow_null && data.get(key)==null)
                throw new IllegalArgumentException(String.format("%s cannot be null", key));
        }
        return true;
    }

    public static String dateToString(Date date){
        return null;
    }

    public static String generateJwt(String key, String secret){
        return null;
    }

    public static String httpReceiver(int port, int attempt){
        // Cannot get response from Zoom due to poor network
        // please try again manually
        if(attempt == 4){
            try {
                throw new IllegalAccessException("Zoom Network failed, try again manually");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Listening to port: " + port);
        String code = null;
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line = null;
            while(reader.ready()) {
                line = reader.readLine();
                if(line.contains("code")){
                    code = line.split(" ")[1];
                }
            }
            reader.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e){
        }

        // double check to make sure getting the response
        // since the Zoom response info may take some time to arrive at our socket port
        if(code == null){
            System.out.println("Zoom response receiving failed, trying again...");
            return httpReceiver(port, attempt + 1);
        } else{
            System.out.println("Raw Authorization code: " + code);
            System.out.println("Listening ends");
            return code;
        }
    }

    public static String generate_jwt(String api_id, String api_secret){
        String token = "";
        try {
            Algorithm algorithm = Algorithm.HMAC256(api_secret);
            token = JWT.create()
                .withIssuer(api_id)
                .sign(algorithm);
        } catch (JWTCreationException e){
            System.out.println("JWT generation failed: " + e);
        }
        return token;
    }

    public static String getOauthToken(String cid, String client_secret, String port, String redirect_url, String browser_path) {
        String token = "";
        try{
            // define parameters for url request
            Map<String, String> params = new HashMap<>();
            params.put("response_type", "code");
            params.put("client_id", cid);
            params.put("redirect_uri", redirect_url);

            // send url request through HttpUrlConnection
            URL url_for_request = new URL("https://zoom.us/oauth/authorize?"+parseParams(params));
            System.out.println(url_for_request);
            HttpURLConnection conn = (HttpURLConnection) url_for_request.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            System.out.println("Status Code: " + conn.getResponseCode());
            String code = httpReceiver(Integer.valueOf(port), 1).split("=")[1];
            System.out.println("Authorization Code: " + code);
            conn.disconnect();

            // send another HttpURLConnection to get oauth2.0 token
            params = new HashMap<>();
            params.put("grant_type", "authorization_code");
            params.put("code", code);
            params.put("redirect_uri", redirect_url);

            URL url_for_token = new URL("https://zoom.us/oauth/token?"+parseParams(params));
            HttpURLConnection conn2 = (HttpURLConnection) url_for_token.openConnection();
            conn2.setRequestMethod("POST");

            // encode authentication information
            String auth = cid + ":" + client_secret;
            String authentication = Base64.getEncoder().encodeToString(auth.getBytes());
            conn2.setRequestProperty("Authorization", "Basic " + authentication);
            conn2.setRequestProperty("Content-Type", "application/json");
            conn2.setRequestProperty("Accept", "application/json");
            System.out.println("Status Code:" + conn2.getResponseCode());

            // read response
            JSONObject response = readResponse(conn2);
            token = response.getString("access_token");
            conn2.disconnect();
        } catch(IOException e){
            //
        }
        System.out.println("Oauth2.0 Token: " + token);
        return token;
    }

    public static String parseParams(Map<String, String> params) throws UnsupportedEncodingException {
        if(params == null) return "";
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry : params.entrySet()){
            if(sb.length()>0) sb.append("&");
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return sb.toString();
    }

    public static JSONObject readResponse(HttpURLConnection conn){
        JSONObject response = null;
        try{
            // read the response
            StringBuilder sb = new StringBuilder();
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while(reader2.ready()){
                // reads a line of text
                line = reader2.readLine();
                sb.append(line);
            }
            reader2.close();
            response = new JSONObject(sb.toString());
        } catch(IOException e){
            System.out.println("Error: " + e);
        }
        return response;
    }

    public static HttpURLConnection httpRequest(String url, String method, int time_out){
        try{
            URL url_for_request = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url_for_request.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(time_out);
            return conn;
        } catch(IOException e){
            return null;
        }
    }
}
