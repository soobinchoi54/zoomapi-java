package zoomapi.cache;

import zoomapi.cache.databaseData.Channel;
import zoomapi.cache.databaseData.ChannelMember;
import zoomapi.cache.databaseData.Credential;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChannelMemberTable extends DatabaseTableHelper<ChannelMember> {
    private static ChannelMemberTable instance = null;
    private ChannelMemberTable(Class<ChannelMember> cls) {
        super(cls);
    }

    public static ChannelMemberTable getInstance(){
        if (instance == null) instance = new ChannelMemberTable(ChannelMember.class);
        return instance;
    }

    /****************************************************************
     * Public APIs
     ****************************************************************/

    public void add(ChannelMember toAdd){
        String sql = "INSERT INTO " + typeName + " (userId, channelId, email, firstName, lastName) " +  " VALUES(?,?,?,?,?)";
        try{
            conn = DriverManager.getConnection(databasePath);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, toAdd.getUserId());
            pstmt.setString(2, toAdd.getChannelId());
            pstmt.setString(3, toAdd.getEmail());
            pstmt.setString(4, toAdd.getFirstName());
            pstmt.setString(5, toAdd.getLastName());
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
