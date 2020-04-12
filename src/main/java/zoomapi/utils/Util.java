package zoomapi.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

public class Util {

    public static void ignored(Exception ...exps){
        //
    }

    public static boolean isStringType(Object val){
        return val instanceof String;
    }

    public static boolean requireKeys(String d, String ...keys){
        for(String k:keys){
            if (!d.contains(k))
                throw new IllegalArgumentException(String.format("%s must be set", k));
        }
        return true;
    }

    public static boolean requireKeys(boolean allow_null, String d, String... keys){
        for(String k:keys){
            if (!d.contains(k))
                throw new IllegalArgumentException(String.format("%s must be set", k));
            if (!allow_null && k==null)
                throw new IllegalArgumentException(String.format("%s cannot be null", k));
        }
        return true;
    }

    public static String dateToString(Date date){
        return null;
    }

    public static String generateJwt(String key, String secret){
        return null;
    }

    public static String httpReceiver(String port){
        return null;
    }

    public static String getOauthToken(String cid, String client_secret, String port, String redirect_url, String browser_path){
        return null;
    }

    public static String parseMapToString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry : params.entrySet()){
            if(sb.length()>0) sb.append("&");
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return sb.toString();
    }
}
