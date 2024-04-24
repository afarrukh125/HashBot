package me.afarrukh.hashbot;

import com.google.inject.Guice;
import me.afarrukh.hashbot.cli.CommandLineInputManager;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

class Main {

    public static void main(String... args) {
        Constants.init();
        var injector = Guice.createInjector(new CommandLineInputManagerModule(),
                new ConfigModule(),
                new DatabaseModule(),
                new JDAModule(),
                new CommandManagerModule());

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
