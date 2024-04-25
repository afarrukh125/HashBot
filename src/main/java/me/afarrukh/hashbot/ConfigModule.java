package me.afarrukh.hashbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.afarrukh.hashbot.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static java.util.Collections.singletonList;

public class ConfigModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigModule.class);

    @Provides
    @Singleton
    public Config config() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File targetFile = new File("settings.json");
        if (targetFile.exists()) {
            return mapper.readValue(targetFile, Config.class);
        } else {
            Config tempConfig = new Config(
                    "!",
                    "TOKEN_HERE",
                    singletonList("111608457290895360"),
                    "OPTIONAL_NEO4J_DB_URI_HERE(neo4j+s://...)",
                    "OPTIONAL_NEO4J_DB_USERNAME_HERE",
                    "OPTIONAL_NEO4J_DB_PASSWORD_HERE");
            mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, tempConfig);
            LOG.info(
                    "Config did not exist, it has been created in {}, please fill it out and rerun.",
                    targetFile.getAbsolutePath());
            System.exit(0);
            return null;
        }

    }
}
