package me.afarrukh.hashbot;

import me.afarrukh.hashbot.core.Bot;

public class Main {
    static Bot hashBot;

    public static void main(String[] args) {
        hashBot = new Bot(args[0]);
    }
}
