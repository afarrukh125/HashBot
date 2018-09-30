package me.afarrukh.hashbot.commands.tagging;

public interface CategorisedCommand {

    default String getType() {
        return getClass().getInterfaces()[0].getSimpleName().replace("Command", "").toLowerCase();
    }

}
