package lib.cache.tables;

import lib.cache.databaseData.Credential;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/****
 * Singleton Table Class
 * ***/
public class CredentialTable extends TableHelper<Credential> {
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
        String sql = "INSERT INTO " + typeName + " (clientId, userSecret, oauthToken, timeStamp) " +  " VALUES(?,?,?,?)";
        try{
            conn = DriverManager.getConnection(databasePath);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, toAdd.getClientId());
            pstmt.setString(2, toAdd.getUserSecret());
            pstmt.setString(3, toAdd.getOauthToken());
            pstmt.setString(4, toAdd.getTimeStamp());
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
