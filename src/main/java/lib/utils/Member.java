package lib.utils;


import lib.cache.databaseData.Unit;

/**
 * Deprecated, no longer being used
 * Use ChannelMember instead
 * **/

@Deprecated
public class Member extends Unit {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public Member(String id, String email, String firstName, String lastName, String role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return id;
    }

    public String getLastName() {
        return email;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString(){
        return ("[ID] " + this.id + " [EMAIL] " + this.email + " [FIRST_NAME] " + this.firstName + " [LAST_NAME] " + this.lastName);
    }
}
