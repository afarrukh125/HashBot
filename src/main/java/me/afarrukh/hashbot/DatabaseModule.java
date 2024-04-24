package me.afarrukh.hashbot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.data.Neo4jDatabase;
import me.afarrukh.hashbot.data.SQLiteDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseModule.class);

    @Provides
    @Singleton
    public Database database(Config config) {
        try {
            return new Neo4jDatabase(config);
        } catch (Exception e) {
            LOG.warn("Failed to connect to neo4j database, using fallback SQLite implementation: {}", e.getMessage());
            return new SQLiteDatabase(config);
        }
    }
}
