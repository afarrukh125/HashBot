package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.gameroles.RoleAdder;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.gameroles.RoleRemover;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

class ReactionManager {

    public void sendToBuilder(GuildMessageReactionAddEvent evt) {
        RoleBuilder rb = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).builderForUser(evt.getUser());
        if(rb == null)
            return;
        rb.handleEvent(evt);
    }

    public void sendToAdder(GuildMessageReactionAddEvent evt) {
        RoleAdder ra = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).adderForUser(evt.getUser());
        if(ra == null)
            return;
        ra.handleEvent(evt);
    }

    public void sendToRemover(GuildMessageReactionAddEvent evt) {
        RoleRemover rr = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).removerForUser(evt.getUser());
        if(rr == null)
            return;
        rr.handleEvent(evt);
    }
}
