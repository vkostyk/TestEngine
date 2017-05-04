package vk.testeng;

public class User {
    public enum AccessType {USER, ADMIN};
    private static int ids = 0;
    private int id;
    private String userName;
    private String password;
    private AccessType accessType;
    public User()
    {
        this(null, null, AccessType.USER);
        ids++;
    }
    public User(String userName, String password, AccessType accessType)
    {
        this.userName = userName;
        this.password = password;
        this.id = ids;
        this.accessType = accessType;
    }
    public String getUserName()
    {
        return this.userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public AccessType getAccessType()
    {
        return this.accessType;
    }
    public int getId()
    {
        return this.id;
    }
    public int getUsersCount()
    {
        return ids;
    }
}


/////user id to be