package lib.cache.tables;

import lib.cache.databaseData.Channel;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/****
 * Singleton Table Class
 * ***/
public class ChannelTable extends TableHelper<Channel> {
    private static ChannelTable instance = null;
    private ChannelTable(Class<Channel> cls) {
        super(cls);
    }

    public static ChannelTable getInstance(){
        if (instance == null) instance = new ChannelTable(Channel.class);
        return instance;
    }

    /****************************************************************
     * Public APIs
     ****************************************************************/

    public void add(Channel toAdd){
        String sql = "INSERT INTO " + typeName + " (clientId, channelId, channelName, channelType) " +  " VALUES(?,?,?,?)";
        try{
            conn = DriverManager.getConnection(databasePath);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, toAdd.getClientId());
            pstmt.setString(2, toAdd.getChannelId());
            pstmt.setString(3, toAdd.getChannelName());
            pstmt.setString(4, toAdd.getChannelType());
            pstmt.executeUpdate();
            pstmt.clearParameters();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
