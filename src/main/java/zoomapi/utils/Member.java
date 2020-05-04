package zoomapi.utils;

public class Member extends Event{
    private String id;
    private String email;
    private String first_name;
    private String last_name;
    private String role;

    public Member(String id, String email, String first_name, String last_name, String role) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return id;
    }

    public String getLast_name() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String toString(){
        return ("[ID] " + this.id + " [EMAIL] " + this.email + " [FIRST_NAME] " + this.first_name + " [LAST_NAME] " + this.last_name);
    }
}
