package zoomapi.cache;

import zoomapi.cache.databaseData.Channel;
import zoomapi.cache.databaseData.Credential;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChannelTable extends DatabaseTableHelper<Channel> {
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
        String sql = "INSERT INTO " + typeName + " (userId, channelId, channelName, channelType) " +  " VALUES(?,?,?,?)";
        try{
            conn = DriverManager.getConnection(databasePath);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, toAdd.getUserId());
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
