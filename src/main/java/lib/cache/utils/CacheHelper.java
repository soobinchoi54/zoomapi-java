package lib.cache.utils;

import lib.cache.databaseData.ChannelMessage;
import lib.cache.tables.ChannelMessageTable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CacheHelper class to help the BOT to update the local
 * cache content (<S> Data </S>) from <T> Table </T>
 * **/
public class CacheHelper<T, S>{
    private Class<T> type;

    public CacheHelper(Class<T> cls){
        this.type = cls;
    }

    public boolean update(Map<String, String> constraints, S[] toAdd, Class<S> klass){
        if(!constraints.containsKey("clientId")){
            System.out.println("Please specify clientId.");
            return false;
        }
        String[] conditions = new String[constraints.size()];
        String[] keys = new String[constraints.size()];

        int index = 0;
        for(Map.Entry<String, String> entry:constraints.entrySet()){
            conditions[index] = entry.getKey();
            keys[index] = entry.getValue();
            index++;
        }

        if(index!=constraints.size()) {
            System.out.println("Invalid constraints input.");
            return false;
        }

        try {
            Method getInstance = this.type.getDeclaredMethod("getInstance");
            T table = (T) getInstance.invoke(null);
            Method delete = this.type.getSuperclass().getDeclaredMethod("delete", String[].class, String[].class);
            Method add = this.type.getDeclaredMethod("add", klass);
            delete.invoke(table, conditions, keys);
            for (S item:toAdd){
                add.invoke(table, item);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}