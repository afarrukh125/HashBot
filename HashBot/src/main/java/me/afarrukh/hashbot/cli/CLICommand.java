package me.afarrukh.hashbot.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abdullah
 * Created on 06/09/2019 at 18:39
 */
public abstract class CLICommand {

    private final String name;
    private final List<String> aliases;

    protected CLICommand(String name) {
        this.name = name;
        aliases = new ArrayList<>();
    }

    public List<String> getAliases() {
        return aliases;
    }

    public abstract void onInvocation(String params);

    public String getName() {
        return name;
    }

    protected void addAlias(String alias) {
        aliases.add(alias);
    }
}
