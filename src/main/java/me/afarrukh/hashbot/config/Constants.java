package me.afarrukh.hashbot.config;

import me.afarrukh.hashbot.graphics.FontLoader;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Constants {
    private static final Logger LOG = LoggerFactory.getLogger(Constants.class);
    public static final int WIDTH = 600;
    public static final int HEIGHT = 200;
    public static final String BG_PATH = "res/images/bg.jpg";
    public static final long minToMillis = 60000;
    public static final int colChangeCred = 500;
    public static final int MAX_CREDIT = 75;
    public static final int LEADERBOARD_MAX = 10;
    public static final int PLAY_TOP_COST = 15;
    public static final int MAX_PLAYLIST_SIZE = 400;
    public static final int DISCONNECT_DELAY =
            60; // The amount of seconds to wait before disconnecting after a user leaves
    public static final int AudioTrackBAR_SCALE = 35;
    public static final int MAX_VOL = 100; // The maximum volume the bot can play at.
    public static final String SELECTEDPOS = "full_moon";
    public static final String UNSELECTEDPOS = "=";
    public static final Color EMB_COL = new Color(100, 243, 213); // The default color for embeds
    public static final Color STATSIMG_COL = Color.WHITE;
    public static final int RANDOM_EXPERIENCE_BOUND = 50;
    public static final int VOICE_EXPERIENCE_TIMER = 240; // Seconds to tick for experience
    public static final int PIN_THRESHOLD = 5;
    public static String invokerChar = "!";
    public static long timeStarted = 0;
    public final Font font28;
    public final Font bigNumFont;
    public static final Long INITIAL_MEMORY =
            Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    public static final int CUSTOM_PLAYLIST_SIZE_LIMIT =
            100; // The limit of a playlist that can be created within the bot

    public static final int PLAYLIST_UPDATE_INTERVAL =
            10; // How often to update the message when loading or creating a new playlist through the bot

    // Bot configuration constants
    public static Set<String> ownerIds;
    public static String token;

    private static Constants instance;

    private Constants() {
        font28 = FontLoader.loadFont("res/fonts/VCR_OSD_MONO.ttf", 28);
        bigNumFont = FontLoader.loadFont("res/fonts/VCR_OSD_MONO.ttf", 36);
    }

    public static Constants getInstance() {
        if (instance == null) {
            instance = new Constants();
        }
        return instance;
    }

    public static void init() {
        timeStarted = System.currentTimeMillis();

        String path = "res/config/settings.json";
        File file = new File(path);
        String prefix;

        ownerIds = new HashSet<>();

        if (new File("res/config").mkdirs()) {}

        try {
            JSONArray arr = (JSONArray) new JSONParser().parse(new FileReader(file));
            Iterator<Object> iter = arr.iterator();
            while (iter.hasNext()) {
                JSONObject obj = (JSONObject) iter.next();
                prefix = (String) obj.get("prefix");
                token = (String) obj.get("token");

                JSONArray userList = (JSONArray) obj.get("ownerids");
                for (Object o : userList) {
                    ownerIds.add((String) o);
                }
                if (prefix != null) invokerChar = prefix;
            }

        } catch (FileNotFoundException e) {
            LOG.info(
                    "Cannot retrieve settings file. Please ensure it is there and in the correct format and then restart the bot.");
            createJsonFile();
            System.exit(0);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public Font font28() {
        return font28;
    }

    public Font bigNumFont() {
        return bigNumFont;
    }

    private static void createJsonFile() {
        if (new File("res/config").mkdirs()) {}

        File src = new File("settings_template.json");
        File dest = new File("res/config/settings.json");
        try {
            if (dest.createNewFile()) {
                LOG.info("Configuration file created at {}", dest.getAbsolutePath());
            } else {
                LOG.info("Configuration file already exists");
            }
            FileUtils.copyFile(src, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
