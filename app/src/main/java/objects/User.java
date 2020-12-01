package objects;

public class User {

    String userName;
    boolean isAdmin;

    public User(String userName) {
        this.isAdmin = false;
        this.userName = userName;
    }


    public User(String userName, boolean isAdmin) {
        this.userName = userName;
        this.isAdmin = isAdmin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
