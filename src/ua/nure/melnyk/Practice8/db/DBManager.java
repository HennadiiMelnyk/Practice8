package ua.nure.melnyk.Practice8.db;

import com.mysql.jdbc.Connection;
import ua.nure.melnyk.Practice8.db.entity.Group;
import ua.nure.melnyk.Practice8.db.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


//import db.entity.User;

public class DBManager {

    private static final String CONNECTION_URL = "jdbc:mysql://127.0.0.1:3306/myDB";

    private static final String USER = "root";
    private static final String PASSWORD = "1111";


    ////////////////////////////////
    // queries

    private static final String SQL_FIND_ALL_USERS = "SELECT * FROM users";

    private static final String SQL_FIND_ALL_GROUPS = "SELECT * FROM groups";

    private static final String SQL_FIND_USER_BY_LOGIN = "SELECT * FROM users WHERE login=?";

    private static final String SQL_FIND_GROUP_BY_USER_ID = "SELECT * FROM groups WHERE groups.id in " +
            "(SELECT group_id FROM users_groups WHERE user_id=?)";

    private static final String SQL_FIND_GROUP_BY_NAME = "SELECT * FROM groups WHERE name=?";

    private static final String SQL_CREATE_USER = "INSERT INTO users VALUES(DEFAULT, ?)";

    private static final String SQL_CREATE_GROUP = "INSERT INTO groups VALUES(DEFAULT, ?)";

    private static final String SQL_INSERT_USER = "INSERT INTO users VALUES (DEFAULT, ?, ?)";

    private static final String SQL_SET_GROUP_FOR_USER = "INSERT INTO users_groups VALUES (?, ?)";

    private static final String SQL_DELETE_GROUP = "DELETE FROM groups WHERE name=?";

    private static final String SQL_UPDATE_GROUP = "UPDATE groups SET name=? WHERE id=?";

    ///////////////////////////////
    // singleton


    private static DBManager instance; // == null

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    private DBManager() {
    }

    ////////////////////////////////
    // logic

    public boolean insertUser(User user) throws SQLException {
        boolean result = true;
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(SQL_CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            int k = 1;
            pstmt.setString(k++, user.getLogin());
            // pstmt.execute();
            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                        user.setId(rs.getInt(1));
                }
            }
            connection.commit();
        } catch (SQLException e) {
            result = false;
            connection.rollback();
            e.printStackTrace();
        } finally {
            pstmt.close();
            connection.close();
        }
        return result;
    }

    public User getUser(String login) throws SQLException {
        Connection con = getConnection();
        PreparedStatement pstmt = con.prepareStatement(SQL_FIND_USER_BY_LOGIN);

        try {
            int k = 1;
            pstmt.setString(k++, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Group getGroup(String name) throws SQLException {
        Connection con = getConnection();
        PreparedStatement pstmt = con.prepareStatement(SQL_FIND_GROUP_BY_NAME);

        try {
            int k = 1;
            pstmt.setString(k++, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractGroup(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(SQL_FIND_ALL_USERS);
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            con.rollback();
            e.printStackTrace();
        } finally {
            rs.close();
            stmt.close();
            con.close();
        }
        return users;
    }

    public boolean insertGroup(Group group) throws SQLException {
        boolean result = true;
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(SQL_CREATE_GROUP, Statement.RETURN_GENERATED_KEYS);
            int k = 1;
            pstmt.setString(k++, group.getName());
            //pstmt.execute();
            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    group.setId(rs.getInt(1));
                }
            }
            connection.commit();
        } catch (SQLException e) {
            result = false;
            connection.rollback();
            e.printStackTrace();
        } finally {
            pstmt.close();
            connection.close();
        }
        return result;
    }

    public List<Group> findAllGroups() throws SQLException {
        List<Group> users = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(SQL_FIND_ALL_GROUPS);
            while (rs.next()) {
                users.add(extractGroup(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            rs.close();
            stmt.close();
            con.close();
        }
        return users;
    }

    public User findUserByLogin(String login) throws SQLException {
        Connection con = getConnection();

        PreparedStatement pstmt = con.prepareStatement(SQL_FIND_USER_BY_LOGIN);
        int k = 1;
        pstmt.setString(k++, login);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return extractUser(rs);
        }
        return null;
    }

    public boolean deleteGroup(Group group) throws SQLException {

        boolean result = true;
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(SQL_DELETE_GROUP);
            int k = 1;
            pstmt.setString(k++, group.getName());
            pstmt.execute();
            connection.commit();
        } catch (SQLException e) {
            result = false;
            connection.rollback();
            e.printStackTrace();
        } finally {
            pstmt.close();
            connection.close();
        }
        return result;
    }

    public void updateGroup(Group group) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {


            con = getConnection();
            pstmt = con.prepareStatement(SQL_UPDATE_GROUP);
            int k = 1;
            pstmt.setString(k++, group.getName());
            pstmt.setInt(k++, group.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // con.rollback();
            e.printStackTrace();
        } finally {
            pstmt.close();
            con.close();
        }
        // return res;
    }

    public List<Group> getUserGroups(User user) throws SQLException {
        List<Group> groups = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(SQL_FIND_GROUP_BY_USER_ID);
            int k = 1;
            pstmt.setInt(k++, user.getId());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groups.add(extractGroup(rs));
            }
        } catch (SQLException e) {
            con.rollback();
            e.printStackTrace();
        } finally {
            rs.close();
            pstmt.close();
            con.close();
        }
        return groups;


    }

    public boolean setGroupsForUser(User user, Group... group) throws SQLException {
        boolean result = true;
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(SQL_SET_GROUP_FOR_USER);
            for (int index = 0, k = 1; index < group.length; index++, k = 1) {

                pstmt.setInt(k++, user.getId());
                pstmt.setInt(k++, group[index].getId());
                pstmt.addBatch();

            }

            pstmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            result = false;
            connection.rollback();
            e.printStackTrace();
        } finally {
            pstmt.close();
            connection.close();
        }
        return result;
    }


    /////////////////////////
    // util methods

    public Connection getConnection() throws SQLException {
        Connection con = (Connection) DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
        con.setAutoCommit(false);
        // ...
        return con;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setLogin(rs.getString("login"));
        return user;
    }

    private Group extractGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt("id"));
        group.setName(rs.getString("name"));
        return group;
    }

    private void close(AutoCloseable ac) {
        if (ac != null) {
            try {
                ac.close();
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot close " + ac);
            }

        }
    }
}