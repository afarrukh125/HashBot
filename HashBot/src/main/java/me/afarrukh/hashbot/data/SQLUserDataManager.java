package me.afarrukh.hashbot.data;

import net.dv8tion.jda.core.entities.Member;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Abdullah on 01/05/2019 00:03
 */
public class SQLUserDataManager implements IDataManager {

    private static Connection conn;
    private static boolean hasData = false;

    private Member member;

    public SQLUserDataManager(Member member) {
        this.member = member;

        try {
            if (conn == null)
                getConnection();

            if (getUserData(member) == null)
                addUser(member);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("SQLUserDataManager@SQLUserDataManager: " + e.getLocalizedMessage());
            System.out.println("SQLUserDataManager@SQLUserDataManager: " + "failed to create user profile");
        }
    }

    public ResultSet getUserData(Member m) throws ClassNotFoundException, SQLException {
        if (conn == null)
            getConnection();

        Statement statement = conn.createStatement();

        return statement.executeQuery("SELECT id, exp, level, time, credit, guild FROM USER WHERE id = " + m.getUser().getId() + " AND guild = " + member.getGuild().getId());
    }

    private void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:HashBot.db");
        initialise();
    }

    private void initialise() throws SQLException {
        if (!hasData) {
            hasData = true;

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name='USER'");
            if (rs == null) {
                System.out.println("SQLUserDataManager@initialise: Building USER table");

                // Creating table
                Statement statement2 = conn.createStatement();
                statement2.execute("CREATE TABLE user(id VARCHAR(60)," +
                        "exp VARCHAR(60), level integer, time VARCHAR(60), credit VARCHAR(60), guild VARCHAR(60))");

                PreparedStatement ps = conn.prepareStatement("INSERT INTO user values(?, ?, ?, ?, ?, ?);");
                ps.setString(1, "281032702327652352");
                ps.setString(2, "6549");
                ps.setInt(3, 136);
                ps.setString(4, "1330");
                ps.setString(5, "18464");
                ps.setString(6, member.getGuild().getId());

                ps.execute();
            }
        }
    }

    public void addUser(Member member) throws ClassNotFoundException, SQLException {
        if (conn == null)
            getConnection();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO USER VALUES(?, ?, ?, ?, ?, ?);");
        ps.setString(1, member.getUser().getId());
        ps.setInt(2, 0);
        ps.setInt(3, 1);
        ps.setLong(4, 0);
        ps.setLong(5, 100);
        ps.setString(6, member.getGuild().getId());

        ps.execute();
    }

    @Override
    public void load() {
    }

    @Override
    public void writePresets() {

    }

    @Override
    public Object getValue(Object key) {
        if (conn == null)
            try {
                getConnection();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT " + key + " FROM USER WHERE id=" + member.getUser().getId() + " AND guild=" + member.getGuild().getId());
            if(!rs.next()) {
                rs.close();
                addUser(member);
                rs = statement.executeQuery("SELECT " + key + " FROM USER WHERE id=" + member.getUser().getId() + " AND guild=" + member.getGuild().getId());
            }
            return rs.getObject(key.toString());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateValue(Object key, Object value) {
        if (conn == null)
            try {
                getConnection();
            } catch (SQLException | ClassNotFoundException ignored) {
            }

        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE USER SET " + key + "=? WHERE id=" + member.getUser().getId());
            ps.setObject(1, value);
            ps.execute();
        } catch (SQLException e) {

        }
    }
}
