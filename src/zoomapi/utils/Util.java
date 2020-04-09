package zoomapi.utils;

import java.util.Date;

public class Util {

    public static void ignored(Exception ...exps){
        //
    }

    public static boolean isStringType(Object val){
        return val instanceof String;
    }

    public static boolean requireKeys(String d, String[] keys){
        for(String k:keys){
            if (!d.contains(k))
                throw new IllegalArgumentException(String.format("%s must be set", k));
        }
        return true;
    }

    public static boolean requireKeys(String d, String[] keys, boolean allow_null){
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

    public static ? generateJwt(String key, String secret){

    }

    public static ? httpReceiver(String port){

    }

    public static ? getOauthToken(String cid, String client_secret, String port, String redirect_url, String browser_path){

    }
}
