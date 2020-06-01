package lib.cache.tables;

import lib.cache.databaseData.ChannelMessage;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/****
 * Singleton Table Class
 * ***/
public class ChannelMessageTable extends TableHelper<ChannelMessage> {
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
        String sql = "INSERT INTO " + typeName + " (clientId, channelId, channelName, messageId, message, sender, dateTime) " +  " VALUES(?,?,?,?,?,?,?)";
        try{
            conn = DriverManager.getConnection(databasePath);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, toAdd.getClientId());
            pstmt.setString(2, toAdd.getChannelId());
            pstmt.setString(3, toAdd.getChannelName());
            pstmt.setString(4, toAdd.getMessageId());
            pstmt.setString(5, toAdd.getMessage());
            pstmt.setString(6, toAdd.getSender());
            pstmt.setString(7, toAdd.getDateTime());
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
