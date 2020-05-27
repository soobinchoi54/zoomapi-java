package zoomapi.cache;

import zoomapi.cache.databaseData.ChannelMember;
import zoomapi.cache.databaseData.ChannelMessage;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChannelMessageTable extends DatabaseTableHelper<ChannelMessage> {
    private static ChannelMessageTable instance = null;
    private ChannelMessageTable(Class<ChannelMessage> cls) {
        super(cls);
    }

    public static ChannelMessageTable getInstance(){
        if (instance == null) instance = new ChannelMessageTable(ChannelMessage.class);
        return instance;
    }

    /****************************************************************
     * Public APIs
     ****************************************************************/

    public void add(ChannelMessage toAdd){
        String sql = "INSERT INTO " + typeName + " (userId, channelId, messageId, message, sender, dateTime) " +  " VALUES(?,?,?,?,?,?)";
        try{
            conn = DriverManager.getConnection(databasePath);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, toAdd.getUserId());
            pstmt.setString(2, toAdd.getChannelId());
            pstmt.setString(3, toAdd.getMessageId());
            pstmt.setString(4, toAdd.getMessage());
            pstmt.setString(5, toAdd.getSender());
            pstmt.setString(6, toAdd.getDateTime());
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
