package me.afarrukh.hashbot.extras;

import me.afarrukh.hashbot.extras.fortnite.FortniteExtra;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;

public class GuildExtrasManager {

    private Guild guild;
    private FortniteExtra fortniteExtra;

    public GuildExtrasManager(Guild guild) {
        this.guild = guild;
        fortniteExtra = new FortniteExtra(guild);
    }

    public FortniteExtra getFortniteExtra() {
        return fortniteExtra;
    }

    public void processEvent(MessageDeleteEvent evt) {
        fortniteExtra.processEvent(evt);
    }

    public Guild getGuild() {
        return guild;
    }
}
