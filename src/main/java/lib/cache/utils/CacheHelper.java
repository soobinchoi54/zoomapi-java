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

    private boolean checkValidation(String [] conditions){
        for(String condition: conditions){
            if(condition.equals("clientId")) return true;
        }
        return false;
    }
    public boolean update(String[] conditions, String[] keys, S[] toAdd, Class<S> klass){
        if(conditions.length != keys.length){
            System.out.println("Input don't match with each other");
            return false;
        }
        if(!checkValidation(conditions)) {
            System.out.println("Please specify clientId");
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