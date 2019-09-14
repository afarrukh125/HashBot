package me.afarrukh.hashbot.data;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.exceptions.PlaylistException;
import me.afarrukh.hashbot.music.Playlist;
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

    /**
     * The <code>Member</code> object associated with this data manager.
     * From this object, we obtain the user ID, and the guild that we will use in executing SQL queries.
     */
    private final Member member;

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
        if (conn == null)
            getConnection();

        Statement statement = conn.createStatement();
        return statement.executeQuery("SELECT * FROM username WHERE id=" + u.getId());
    }

    public static void updateUsernames(Guild g) throws ClassNotFoundException, SQLException {
        if (conn == null)
            getConnection();

        for (Member m : g.getMembers()) {
            if (SQLUserDataManager.getUserNameData(m.getUser()).next())
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

        if (getUserNameData(member.getUser()).next())
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
            if (!rs.next()) {
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
                if (member == null)
                    continue;
                if (member.getUser().isBot())
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
                    if (o1.getLevel() > o2.getLevel())
                        return -1;
                    if (o1.getLevel() == o2.getLevel()) {
                        if (o2.getExp() > o1.getExp())
                            return 1;
                        return -1;
                    }
                    return 1;
                }
            };

            memberData.sort(memberDataSorter);
            List<Member> memberList = new ArrayList<>();
            for (MemberData md : memberData) {
                memberList.add(md.getMember());
            }
            return memberList;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void checkConn() {
        if (conn == null)
            try {
                getConnection();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
    }

    /**
     * Adds a list of tracks to the database
     *
     * @param lname     The name of the playlist
     * @param trackList The list of tracks to add
     * @throws PlaylistException
     */
    public void addPlaylist(String lname, List<AudioTrack> trackList) throws PlaylistException {
        checkConn(); // Mandatory before every call to the database

        try {

            // Create the playlist
            PreparedStatement pslist = conn.prepareStatement("INSERT INTO playlist VALUES(?, ?, ?);");
            // Field at parameterIndex = 1 increments automatically
            pslist.setString(2, lname);
            pslist.setString(3, this.member.getUser().getId());
            pslist.execute();

            // We need to check if a playlist with the same name, by the same user already exists
            // If this is the case, we throw a checked exception, to be handled by the class calling this method.
            Statement statement = conn.createStatement();

            final String getPlaylistsWithNameQuery = "SELECT DISTINCT(url), name FROM track, listuser, playlist, user, listtrack " +
                    "WHERE track.url=listtrack.trackurl AND playlist.name='" +lname + "' AND listuser.userid=user.id " +
                    "AND listtrack.listid=playlist.listid ORDER BY listtrack.position ASC";

            ResultSet rs = statement.executeQuery(getPlaylistsWithNameQuery);

            int listId;
            if (!rs.next())
                listId = conn.createStatement().getGeneratedKeys().getInt(1);
            else {
                /* We need to undo the creation of the playlist from earlier.
                 For some reason, the JDBC Statement class has a method that allows you to obtain generated keys, as
                 exhibited above, that obtains the latest auto incrementing key based on 'context'.
                 I am currently aware of how to circumvent such behaviour, other than to delete the entry we created
                 just to obtain the context */
                conn.prepareStatement("DELETE FROM playlist WHERE userid="+this.member.getUser().getId()+" " +
                        "AND listid=(SELECT MAX(listid) FROM playlist WHERE userid = " + this.member.getUser().getId() + ");").execute();
                throw new PlaylistException("A playlist with that name already exists!");
            }

            System.out.println(listId);

            // Create an entry in the table that maps users to their playlists.
            PreparedStatement pslistuser = conn.prepareStatement("INSERT INTO listuser VALUES(?, ?)");
            pslistuser.setInt(1, listId);
            pslistuser.setString(2, this.member.getUser().getId());
            int pos = 1; // Assigning a position to each of the tracks as well
            // Adding the tracks
            for (AudioTrack track : trackList) {

                String trackURI = track.getInfo().uri.replace(":", ";");

                // Put the track into the database, with the name
                PreparedStatement pstrack = conn.prepareStatement("INSERT INTO track VALUES(?, ?);");
                pstrack.setString(1, trackURI);
                pstrack.setString(2, track.getInfo().title);
                try {
                    pstrack.execute();
                } catch(SQLException e) {
                    // Duplicate track detected, ignore
                }


                // Add to the table that maps the playlist ID to the track URL. Assign the position too.
                PreparedStatement pslisttrack = conn.prepareStatement("INSERT INTO listtrack VALUES(?, ?, ?)");
                pslisttrack.setInt(1, listId);
                pslisttrack.setString(2, trackURI);
                pslisttrack.setInt(3, pos);

                pos++;

                pslisttrack.execute();
            }

            pslistuser.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void loadPlaylistByName(String name) {
        checkConn();

        // TODO Fix the asynchronous behaviour of the playlist loading

        try {
            final String query = "SELECT DISTINCT(url) FROM track, listuser, playlist, user, listtrack " +
                    "WHERE track.url=listtrack.trackurl AND playlist.name='" +name + "' AND listuser.userid=user.id " +
                    "AND listtrack.listid=playlist.listid ORDER BY listtrack.position ASC";

            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                String uri = rs.getString(1).replace(";", ":");
                Bot.musicManager.getPlayerManager().loadItemOrdered(Bot.musicManager.getGuildAudioPlayer(member.getGuild()), uri, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack audioTrack) {
                        audioTrack.setUserData(member.getUser().getName());
                        Bot.musicManager.getGuildAudioPlayer(member.getGuild()).getScheduler().queue(audioTrack);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    }

                    @Override
                    public void noMatches() {
                    }

                    @Override
                    public void loadFailed(FriendlyException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Playlist> viewAllPlaylists() {
        checkConn();

        final String query = "SELECT COUNT(DISTINCT (url)) AS trackCount, name AS listName " +
                "FROM playlist, listuser, track, user, listtrack " +
                "WHERE user.id=listuser.userid " +
                    "AND listtrack.trackurl=track.url " +
                    "AND listtrack.listid=playlist.listid " +
                    "AND listuser.userid="+ member.getUser().getId() + " " +
                    "AND playlist.listid=listuser.listid" +
                " GROUP BY playlist.listid ORDER BY playlist.listid";

        System.out.println(query);

        try {
            List<Playlist> plist = new ArrayList<>();
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                String name = rs.getString(2);
                int size = rs.getInt(1);
                plist.add(new Playlist(name, size));
                System.out.println(name + ", " + size);
            }
            return plist;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the size of a given playlist from this member's playlists
     * @param listName The name of the list to be searched for
     * @return An integer corresponding to the size of the playlist that has been searched for
     */
    public int getPlaylistSize(String listName) throws PlaylistException {
        checkConn();

        final String getPlaylistsWithNameQuery = "SELECT DISTINCT(url), MAX(position) AS maxPosition " +
                "FROM track, listuser, playlist, user, listtrack " +
                "WHERE track.url=listtrack.trackurl " +
                "AND playlist.name='" + listName + "' " +
                "AND listuser.userid=user.id " +
                "AND listtrack.listid=playlist.listid " +
                "ORDER BY listtrack.position ASC";

        try {

            return conn.createStatement().executeQuery(getPlaylistsWithNameQuery).getInt("maxPosition");

        } catch (SQLException e) {
            throw new PlaylistException("No playlist was found with that name");
        }
    }
}
