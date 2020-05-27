package zoomapi.cache.databaseData;

import zoomapi.utils.Member;
import zoomapi.utils.Unit;

import java.util.Map;

public class ChannelMember extends Unit implements CacheUnit {
    private String id;
    private String userId;
    private String channelId;
    private String email;
    private String firstName;
    private String lastName;


    public ChannelMember(){}

    public String getId(){
        return this.id;
    }

    public String getUserId(){return this.userId;}

    public String getChannelId(){return this.channelId;}

    public String getEmail(){return this.email;}

    public String getFirstName(){return this.firstName;}

    public String getLastName(){return this.lastName;}

    @Override
    public void setValues(Map<String, String> values) {
        this.id = values.get("id");
        this.userId = values.get("userId");
        this.channelId = values.get("channelId");
        this.email = values.get("email");
        this.firstName = values.get("firstName");
        this.lastName = values.get("lastName");
    }

    @Override
    public String toString() {
        return "id: " + this.getId() + " userId: " + this.getUserId() + " channelId: " + this.getChannelId() + " email: " + this.getEmail() + " firstName: " + this.getFirstName() + " lastName: " + this.getLastName();
    }
}
