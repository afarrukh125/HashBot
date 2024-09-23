package me.afarrukh.hashbot;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import com.google.inject.Guice;
import java.util.Scanner;
import me.afarrukh.hashbot.cli.CommandLineInputManager;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;

class Main {

    public static void main(String... args) {
        Constants.init();
        var injector = Guice.createInjector(
                new CommandLineInputManagerModule(),
                new ConfigModule(),
                new DatabaseModule(),
                new JDAModule(),
                new CommandManagerModule(),
                new AudioTrackModule());

        injector.getInstance(Bot.class).init();

        try (var cliExecutor = newSingleThreadExecutor()) {
            var commandLineInputManager = injector.getInstance(CommandLineInputManager.class);
            cliExecutor.execute(() -> {
                while (true) {
                    var scanner = new Scanner(System.in);
                    var input = scanner.nextLine();
                    commandLineInputManager.processInput(input);
                }
            });
        }
    }
}
