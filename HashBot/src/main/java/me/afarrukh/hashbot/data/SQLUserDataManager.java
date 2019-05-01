package me.afarrukh.hashbot.data;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
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
                addMember(member);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("SQLUserDataManager@SQLUserDataManager: " + e.getLocalizedMessage());
            System.out.println("SQLUserDataManager@SQLUserDataManager: " + "failed to create user profile");
        }
    }

    public static ResultSet getUserData(Member m) throws ClassNotFoundException, SQLException {
        if (conn == null)
            getConnection();

        Statement statement = conn.createStatement();

        return statement.executeQuery("SELECT id, exp, level, time, credit, guild FROM USER WHERE id = " + m.getUser().getId() + " AND guild = " + m.getGuild().getId());
    }

    private static void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:HashBot.db");
        initialise();
    }

    private static void initialise() throws SQLException {
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
            }
        }
    }

    public static ResultSet getUserNameData(User u) throws SQLException, ClassNotFoundException {
        if(conn == null)
            getConnection();

        Statement statement = conn.createStatement();
        return statement.executeQuery("SELECT * FROM username WHERE id="+u.getId());
    }

    public static void updateUsernames(Guild g) throws ClassNotFoundException, SQLException {
        if(conn == null)
            getConnection();

        for(Member m: g.getMembers()) {
            if(SQLUserDataManager.getUserNameData(m.getUser()).next())
                continue;
            PreparedStatement ps = conn.prepareStatement("INSERT INTO username VALUES (?, ?);");
            ps.setString(1, m.getUser().getName());
            ps.setString(2, m.getUser().getId());
            ps.execute();
        }

    }

    public static void addMember(Member member) throws ClassNotFoundException, SQLException {
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

        if(getUserNameData(member.getUser()).next())
            return;

        PreparedStatement ps2 = conn.prepareStatement("INSERT INTO username VALUES (?, ?);");
        ps2.setString(1, member.getUser().getName());
        ps2.setString(2, member.getUser().getId());

        ps2.execute();
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
                addMember(member);
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
            PreparedStatement ps = conn.prepareStatement("UPDATE USER SET " + key + "=? WHERE id=" + member.getUser().getId() + " AND guild=" + member.getGuild().getId());
            ps.setObject(1, value);
            ps.execute();
        } catch (SQLException e) {

        }
    }

    public static List<Member> getMemberData(Guild guild) {
        if (conn == null)
            try {
                getConnection();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT id, level, exp FROM USER WHERE guild=" + guild.getId());

            List<MemberData> memberData = new ArrayList<>();

            while (rs.next()) {
                Member member = guild.getMemberById(rs.getString("id"));
                if(member == null)
                    continue;
                if(member.getUser().isBot())
                    continue;
                int lvl = rs.getInt("level");
                long exp;
                try {
                    exp = Long.parseLong(rs.getString("exp"));
                } catch (NumberFormatException e) {
                    exp = Long.parseLong(rs.getString("exp").split("\\.")[0]);
                }
                memberData.add(new MemberData(member, lvl, exp));
            }

            Comparator<MemberData> memberDataSorter = new Comparator<MemberData>() {
                @Override
                public int compare(MemberData o1, MemberData o2) {
                    if(o1.getLevel() > o2.getLevel())
                        return -1;
                    if(o1.getLevel() == o2.getLevel()) {
                        if (o2.getExp() > o1.getExp())
                            return 1;
                        return -1;
                    }
                    return 1;
                }
            };

            memberData.sort(memberDataSorter);
            List<Member> memberList = new ArrayList<>();
            for(MemberData md: memberData) {
                memberList.add(md.getMember());
            }
            return memberList;


        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
