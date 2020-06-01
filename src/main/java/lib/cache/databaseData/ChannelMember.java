package lib.cache.databaseData;

import java.util.Map;

public class ChannelMember implements CacheUnit {
    // unique keys
    private String id;
    private String clientId;
    private String channelId;
    private String channelName;

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

    public String getChannelName() {return this.channelName;}

    public String getMemberId(){return this.memberId;}

    public String getEmail(){return this.email;}

    public String getFirstName(){return this.firstName;}

    public String getLastName(){return this.lastName;}

    @Override
    public void setValues(Map<String, String> values) {
        this.id = values.getOrDefault("id", null);
        this.clientId = values.getOrDefault("clientId", null);
        this.channelId = values.getOrDefault("channelId", null);
        this.channelName = values.getOrDefault("channelName", null);
        this.memberId = values.getOrDefault("memberId", null);
        this.email = values.getOrDefault("email", null);
        this.firstName = values.getOrDefault("firstName", null);
        this.lastName = values.getOrDefault("lastName", null);
    }

    @Override
    public String toString() {
        return " clientId: " + this.getClientId() + " channelId: " + this.getChannelId() + " channelName: " + this.getChannelName()+ " memberId: " + this.getMemberId() + " email: " + this.getEmail() + " firstName: " + this.getFirstName() + " lastName: " + this.getLastName();
    }
}
