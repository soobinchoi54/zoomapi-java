package lib.cache.tables;

import lib.cache.databaseData.Channel;
import lib.cache.databaseData.ChannelMember;
import lib.cache.databaseData.ChannelMessage;
import lib.cache.databaseData.Credential;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/***************************************
 * A generic typed TableHelper to help
 * implement tables of <T> DataType </T>
 ***************************************/
public class TableHelper<T>{
    protected Class<T> type;
    protected String databasePath;
    protected Field[] fields;
    protected String typeName;
    protected Connection conn;
    public TableHelper(Class<T> cls){
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("src/main/java/lib/cache/tables/config.ini"));
            databasePath = p.getProperty("database_path");
        } catch (IOException e) {
            e.printStackTrace();
            databasePath = null;
        }
        this.type = cls;
        this.typeName = type.getSimpleName();
        this.fields = type.getDeclaredFields();
        createTableIfAbsent();

    }
    public Class getType(){
        return type.getClass();
    }

    private void createTableIfAbsent() {
        if(this.fields.length == 0) return;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + typeName + " (_id INTEGER PRIMARY KEY AUTOINCREMENT");
        for(Field f: fields){
            sb.append(", " + f.getName() + " TEXT");
        }
        sb.append(");");
        String sql = sb.toString();
        try{
            this.conn = DriverManager.getConnection(databasePath);
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /****************************************************************
     * Public APIs
     ****************************************************************/

    public void clearCache(){
        if(this.fields.length == 0) return;
        String drop = "DROP TABLE IF EXISTS " + this.typeName;
        try{
            this.conn = DriverManager.getConnection(databasePath);
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(drop);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        createTableIfAbsent();
    }

    public List<T> get(String condition, String key){
        return get(new String[]{condition}, new String[]{key});
    }

    public List<T> get(String[] conditions, String[] keys) {
        if(conditions.length != keys.length){
            System.out.println("Inputs don't match to each other");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + typeName + " WHERE ");
        for(int i = 0; i<conditions.length; i++){
            if(i!=0) sb.append(" AND ");
            sb.append(String.format("%1$s = '%2$s' ", conditions[i], keys[i]));
        }
        List<T> ans = new ArrayList<>();
        try {
            this.conn = DriverManager.getConnection(databasePath);
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sb.toString());
            // loop through the result set
            
            while (rs.next()) {
                T item = this.type.getDeclaredConstructor().newInstance();
                Map<String, String> values = new HashMap<>();
                for(Field f: fields){
                    values.put(f.getName(), rs.getString(f.getName()));
                }
                values.put("id", String.valueOf(rs.getInt("_id")));
                Method setValues = this.type.getDeclaredMethod("setValues", Map.class);
                setValues.invoke(item, values);
                ans.add(item);
            }
            stmt.close();
            conn.close();
            return ans;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<T> getAll(){
        String sql = "SELECT * FROM " + this.typeName;
        List<T> ans = new ArrayList<>();
        try {
            this.conn = DriverManager.getConnection(databasePath);
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set

            while (rs.next()) {
                T item = this.type.getDeclaredConstructor().newInstance();
                Map<String, String> values = new HashMap<>();
                for(Field f: fields){
                    values.put(f.getName(), rs.getString(f.getName()));
                }
                values.put("id", String.valueOf(rs.getInt("_id")));
                Method setValues = this.type.getDeclaredMethod("setValues", Map.class);
                setValues.invoke(item, values);
                ans.add(item);
            }
            stmt.close();
            conn.close();
            return ans;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean update(String column, String value, String condition, String key){
        return update(new String[]{column}, new String[]{value}, new String[]{condition}, new String[]{key});
    }

    public boolean update(String[] columns, String[] values, String[] conditions, String[] keys){
        if(conditions.length != keys.length || columns.length != values.length){
            System.out.println("Inputs don't match to each other");
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE " + this.typeName + " SET ");
        for(int i = 0; i<columns.length; i++){
            if(i!=0) sb.append(" , ");
            sb.append(String.format("%1$s = '%2$s' ", columns[i], values[i]));
        }
        sb.append(" WHERE ");
        for(int i = 0; i<conditions.length; i++){
            if(i!=0) sb.append(" AND ");
            sb.append(String.format("%1$s = '%2$s' ", conditions[i], keys[i]));
        }
        try{
            conn = DriverManager.getConnection(databasePath);
            Statement stmt = conn.createStatement();
            // update with the corresponding parameter
            stmt.executeUpdate(sb.toString());
            stmt.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String condition, String key){
        return delete(new String[]{condition}, new String[]{key});
    }
    public boolean delete(String[] conditions, String[] keys){
        if(conditions.length != keys.length){
            System.out.println("Inputs don't match to each other");
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM " + this.typeName + " WHERE ");
        for(int i = 0; i<conditions.length; i++){
            if(i!=0) sb.append(" AND ");
            sb.append(String.format("%1$s = '%2$s' ", conditions[i], keys[i]));
        }
        try{
            conn = DriverManager.getConnection(databasePath);
            Statement stmt = conn.createStatement();
            // update with the corresponding parameter
            stmt.executeUpdate(sb.toString());
            stmt.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args){
        testCredentialTable();
        testChannelTable();
        testChannelMemberTable();
        testChannelMessageTable();
    }

    private static void testCredentialTable(){
        System.out.println("=====CredentialTable======");
        CredentialTable ct = CredentialTable.getInstance();
        List<Credential> all = ct.getAll();
        for(int i =0; i < all.size(); i++){
            System.out.println(all.get(i).toString());
        }
    }

    private static void testChannelTable(){
        System.out.println("=====ChannelTable======");
        ChannelTable ct = ChannelTable.getInstance();
        List<Channel> all = ct.getAll();
        for(int i =0; i < all.size(); i++){
            System.out.println(all.get(i).toString());
        }
    }

    private static void testChannelMemberTable(){
        System.out.println("=====ChannelMemberTable======");
        ChannelMemberTable cmt = ChannelMemberTable.getInstance();
        List<ChannelMember> all = cmt.getAll();
        for(int i =0; i < all.size(); i++){
            System.out.println(all.get(i).toString());
        }
    }


    private static void testChannelMessageTable() {
        System.out.println("=====ChannelMessageTable======");
        ChannelMessageTable cmt = ChannelMessageTable.getInstance();
        List<ChannelMessage> all = cmt.getAll();
        for(int i =0; i < all.size(); i++){
            System.out.println(all.get(i).toString());
        }
    }
}
