package me.afarrukh.hashbot;

import com.google.inject.Guice;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;

class Main {

    public static void main(String... args) {
        var injector = Guice.createInjector(new CoreBotModule());
        Bot bot = injector.getInstance(Bot.class);
        bot.init();
    }
}
