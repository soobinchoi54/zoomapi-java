package lib.cache.tables;

import lib.cache.databaseData.ChannelMember;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/****
 * Singleton Table Class
 * ***/
public class ChannelMemberTable extends TableHelper<ChannelMember> {
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
        String sql = "INSERT INTO " + typeName + " (clientId, channelId, channelName, email, firstName, lastName) " +  " VALUES(?,?,?,?,?,?)";
        try{
            conn = DriverManager.getConnection(databasePath);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, toAdd.getClientId());
            pstmt.setString(2, toAdd.getChannelId());
            pstmt.setString(3, toAdd.getChannelName());
            pstmt.setString(4, toAdd.getEmail());
            pstmt.setString(5, toAdd.getFirstName());
            pstmt.setString(6, toAdd.getLastName());
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
