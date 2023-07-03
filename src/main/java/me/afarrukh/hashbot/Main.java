package me.afarrukh.hashbot;

import com.google.inject.Guice;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {
        var injector = Guice.createInjector(new CoreBotModule());
        injector.getInstance(Bot.class).init();
    }
}
