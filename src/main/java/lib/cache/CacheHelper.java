package lib.cache;

import lib.clients.OauthZoomClient;

import java.lang.reflect.Method;
import java.util.List;

/**
 * CacheHelper class to help the BOT to fetch the local
 * cache content (<S> Data </S>) from <T> Table </T>
 * **/
public class CacheHelper<T, S>{
    private Class<T> type;
    private OauthZoomClient client;
    private String clientId;

    public CacheHelper(Class<T> cls, OauthZoomClient client){
        this.type = cls;
        this.client = client;
        if(client!=null) this.clientId = client.getClientId();
    }

    /***
     * After careful consideration, add, get, delete methods should not be provided to bot-level
     * application, since cache system is not suppose to be modified by the user. As such, we
     * change these methods to be private
     * ***/
    private boolean add(S item){
        try {
            Method getInstance = this.type.getDeclaredMethod("getInstance");
            T table = (T) getInstance.invoke(null);
            Method add = this.type.getDeclaredMethod("updateData", List.class);
            add.invoke(table, item);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<S> get(String[] conditions, String[] keys){
        if(conditions.length != keys.length){
            System.out.println("Inputs don't match to each other");
            return null;
        }
        String[] conditionsWithClient = new String[conditions.length+1];
        String[] keysWithClient = new String[keys.length+1];
        conditionsWithClient[0] = "clientId";
        keysWithClient[0] = this.clientId;
        for(int i = 0; i < conditions.length; i++){
            conditionsWithClient[i+1] = conditions[i];
            keysWithClient[i+1] = keys[i];
        }

        try {
            Method getInstance = this.type.getDeclaredMethod("getInstance");
            T table = (T) getInstance.invoke(null);
            Method get = this.type.getSuperclass().getDeclaredMethod("get", String[].class, String[].class);
            return (List<S>) get.invoke(table, conditionsWithClient, keysWithClient);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static void main(String[] args){
//        // OauthZoomClient client = new OauthZoomClient("soobin", "client_secret", 1, "redirect_url", "browser_path");
//        CacheHelper<ChannelMessageTable, ChannelMessage> cache = new CacheHelper<>(ChannelMessageTable.class, null);
//        List<ChannelMessage> ans = cache.get(new String[]{}, new String[]{});
//        for(int i = 0; i < ans.size(); i++){
//            System.out.println(ans.get(i).toString());
//        }
//    }


}
