package vk.testeng.model;

public class User {
    public enum AccessType {USER, ADMIN}
    private int id;
    private String username;
    private String password;
    private AccessType accessType;
    public User(String username, String password, AccessType accessType)
    {
        this.username = username;
        this.password = password;
        this.accessType = accessType;
    }

    public User(String username, String password)
    {

        this.username = username;
        this.password = password;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public void setAccessType(AccessType accessType) {this.accessType = accessType;}
    public void setId (int id) {this.id = id;}

    public String getUsername()
    {
        return this.username;
    }
    public String getPassword()
    {
        return this.password;
    }
    public AccessType getAccessType()
    {
        return this.accessType;
    }
    public int getId()
    {
        return this.id;
    }

}
