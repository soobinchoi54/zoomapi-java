package zoomapi.cache.databaseData;

import zoomapi.utils.Unit;

import java.util.Map;

public class Credential extends Unit {
    private String id;
    private String userId;
    private String userSecret;
    private String oauthToken;

    public Credential(){}

    public void setValues(Map<String, String> values){
        this.id = values.get("id");
        this.userId = values.get("userId");
        this.userSecret = values.get("userSecret");
        this.oauthToken = values.get("oauthToken");
    }

    public String getId(){
        return this.id;
    }

    public String getUserId() { return this.userId; }

    public String getUserSecret() { return this.userSecret; }

    public String getOauthToken() {return this.oauthToken; }
    @Override
    public String toString() {
        return "id: " + this.getId() + " userId: " + this.getUserId() + " userSecret: " + this.getUserSecret() + " oauthToken: " + this.getOauthToken();
    }
}
