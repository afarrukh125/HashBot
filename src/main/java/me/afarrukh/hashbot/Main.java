package me.afarrukh.hashbot;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Main {

    public static void main(String[] args) throws InterruptedException {
        String tok;
        Constants.init();
        if (args.length == 0) {
            tok = Constants.token;
            new Bot(tok);
        } else {
            new Bot(args[0]);
        }
    }
}
