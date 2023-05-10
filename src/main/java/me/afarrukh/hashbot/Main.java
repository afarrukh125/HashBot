package me.afarrukh.hashbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static java.util.Collections.singletonList;

class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws InterruptedException, IOException {
        Config config = getConfigFromFile();
        Constants.init();
        new Bot(config);
    }

    private static Config getConfigFromFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File targetFile = new File("res/config/settings.json");
        if (targetFile.exists()) {
            return mapper.readValue(targetFile, Config.class);
        } else {
            Config tempConfig = new Config("!",
                    "TOKEN_HERE",
                    singletonList("111608457290895360"),
                    "DB_URI_HERE",
                    "DB_USERNAME_HERE",
                    "DB_PASSWORD_HERE");
            mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, tempConfig);
            LOG.info("Config did not exist, it has been created in {}, please fill it out and rerun.", targetFile.getAbsolutePath());
            System.exit(0);
            return null;
        }
    }
}
