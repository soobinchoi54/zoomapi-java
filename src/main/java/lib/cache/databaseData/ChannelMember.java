package lib.cache.databaseData;

import lib.utils.Unit;

import java.util.Map;

public class ChannelMember extends Unit implements CacheUnit {
    private String id;
    private String clientId;
    private String channelId;
    private String memberId;
    private String email;
    private String firstName;
    private String lastName;


    public ChannelMember(){}

    /*********************************************************************
     * After careful consideration, getId methods should not be provided,
     * since id indicates the sensitive data in the cache system
     *********************************************************************/
    private String getId(){
        return this.id;
    }

    public String getClientId(){return this.clientId;}

    public String getChannelId(){return this.channelId;}

    public String getMemberId(){return this.memberId;}

    public String getEmail(){return this.email;}

    public String getFirstName(){return this.firstName;}

    public String getLastName(){return this.lastName;}

    @Override
    public void setValues(Map<String, String> values) {
        this.id = values.getOrDefault("id", null);
        this.clientId = values.getOrDefault("clientId", null);
        this.channelId = values.getOrDefault("channelId", null);
        this.memberId = values.getOrDefault("memberId", null);
        this.email = values.getOrDefault("email", null);
        this.firstName = values.getOrDefault("firstName", null);
        this.lastName = values.getOrDefault("lastName", null);
    }

    @Override
    public String toString() {
        return " clientId: " + this.getClientId() + " channelId: " + this.getChannelId() + " memberId: " + this.getMemberId() + " email: " + this.getEmail() + " firstName: " + this.getFirstName() + " lastName: " + this.getLastName();
    }
}
