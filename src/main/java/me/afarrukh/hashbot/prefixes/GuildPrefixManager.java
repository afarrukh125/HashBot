package me.afarrukh.hashbot.prefixes;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.DataManager;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import net.dv8tion.jda.api.entities.Guild;

public class GuildPrefixManager {
    private final Guild guild;
    private String prefix = Constants.invokerChar;
    private int pinThreshold = Constants.PIN_THRESHOLD;

    public GuildPrefixManager(Guild guild) {
        this.guild = guild;
        init();
    }

    private void init() {
        DataManager guildDataManager = GuildDataMapper.getInstance().getDataManager(guild);
        setupCommandPrefix(guildDataManager);
        setupPinnedMessageThreshold(guildDataManager);
    }

    private void setupCommandPrefix(DataManager guildDataManager) {
        String prefix = (String) guildDataManager.getValue(Key.PREFIX.string());

        if (prefix != null) {
            this.prefix = prefix;
        } else {
            guildDataManager.updateValue(Key.PREFIX.string(), Constants.invokerChar);
        }
    }

    private void setupPinnedMessageThreshold(DataManager guildDataManager) {
        String threshold = (String) guildDataManager.getValue(Key.PINNED_THRESHOLD.string());
        if (threshold != null) {
            this.pinThreshold = Integer.parseInt(threshold);
        } else {
            guildDataManager.updateValue(Key.PINNED_THRESHOLD.string(), Long.toString(this.pinThreshold));
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(guild);
        jgm.updateValue(Key.PREFIX.string(), prefix);
        this.prefix = prefix;
    }

    public int getPinThreshold() {
        return pinThreshold;
    }

    public void setPinThreshold(int amount) {
        GuildDataManager jgm = GuildDataMapper.getInstance().getDataManager(guild);
        this.pinThreshold = amount;
        jgm.updateValue(Key.PINNED_THRESHOLD.string(), Integer.toString(amount));
    }

    private enum Key {
        PREFIX("prefix"),
        NAME("name"),
        PINNED_THRESHOLD("pThreshold"),
        CREATOR("creatorId");

        private final String key;

        Key(String key) {
            this.key = key;
        }

        String string() {
            return key;
        }
    }
}
