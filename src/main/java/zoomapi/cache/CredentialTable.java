package zoomapi.cache;

import zoomapi.cache.databaseData.Credential;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CredentialTable extends DatabaseTableHelper<Credential> {
    private static CredentialTable instance = null;
    private CredentialTable(Class<Credential> cls) {
        super(cls);
    }

    public static CredentialTable getInstance(){
        if (instance == null) instance = new CredentialTable(Credential.class);
        return instance;
    }

    /****************************************************************
     * Public APIs
     ****************************************************************/

    public void add(Credential toAdd){
        String sql = "INSERT INTO " + typeName + " (userId, userSecret, oauthToken) " +  " VALUES(?,?,?)";
        try{
            conn = DriverManager.getConnection(databasePath);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, toAdd.getUserId());
            pstmt.setString(2, toAdd.getUserSecret());
            pstmt.setString(3, toAdd.getOauthToken());
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
