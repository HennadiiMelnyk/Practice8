package ua.nure.melnyk.Practice8.db.entity;

public class User {
    private int id;

    private String login;

    public User() {
    }

    public User(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public int getId() {
         return id;
    }

    public void setId(int id) {
         this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    @Override
    public String toString() {
        return "User [id=" + id + ", login=" + login +  "]";
    }


    public static User createUser(String login) {
        User user = new User();
        user.setLogin(login);
        return user;
    }
}
