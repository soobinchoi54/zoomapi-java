package lib.cache.databaseData;

import java.util.Map;

public class Credential {
    private String id;
    private String clientId;
    private String userSecret;
    private String oauthToken;
    private String timeStamp;

    public Credential(){}

    public void setValues(Map<String, String> values){
        this.id = values.getOrDefault("id", null);
        this.clientId = values.getOrDefault("clientId", null);
        this.userSecret = values.getOrDefault("userSecret", null);
        this.oauthToken = values.getOrDefault("oauthToken", null);
        this.timeStamp = values.getOrDefault("timeStamp", null);
    }

    /*********************************************************************
     * After careful consideration, getId methods should not be provided,
     * since id indicates the sensitive data in the cache system
     *********************************************************************/
    private String getId(){
        return this.id;
    }

    public String getClientId() { return this.clientId; }

    public String getUserSecret() { return this.userSecret; }

    public String getOauthToken() {return this.oauthToken; }

    public String getTimeStamp() {return this.timeStamp;}
    @Override
    public String toString() {
        return " clientId: " + this.getClientId() + " userSecret: " + "***************" + " oauthToken: " + this.getOauthToken() + " timeStamp: " + this.getTimeStamp();
    }
}
