package me.afarrukh.hashbot;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;

class Main {

    public static void main(String[] args) {
        String tok;
        Constants.init();
        Bot hashBot;
        if(args.length == 0) {
            tok = Constants.token;
            hashBot = new Bot(tok);
        }
        else
            hashBot = new Bot(args[0]);
    }
}
