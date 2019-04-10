package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.gameroles.RoleAdder;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.gameroles.RoleDeleter;
import me.afarrukh.hashbot.gameroles.RoleRemover;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashSet;
import java.util.Set;

class ReactionManager {

    void sendToBuilder(GuildMessageReactionAddEvent evt) {
        RoleBuilder rb = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).builderForUser(evt.getUser());
        if(rb == null)
            return;
        rb.handleEvent(evt);
    }

    void sendToAdder(GuildMessageReactionAddEvent evt) {
        RoleAdder ra = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).adderForUser(evt.getUser());
        if(ra == null)
            return;
        ra.handleEvent(evt);
    }

    void sendToRemover(GuildMessageReactionAddEvent evt) {
        RoleRemover rr = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).removerForUser(evt.getUser());
        if(rr == null)
            return;
        rr.handleEvent(evt);
    }

    public void sendToDeleter(GuildMessageReactionAddEvent evt) {
        RoleDeleter rd = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).deleterForUser(evt.getUser());
        if(rd == null)
            return;
        rd.handleEvent(evt);
    }

    /**
     * We decide here if we want to post the message to the pinned channel depending on the reaction count
     * We are testing for distinct users, hence we use the Set class. Algorithm is unfortunately O(n^2)
     * @param evt The event associated with the reaction being added
     */
    void processForPinning(GuildMessageReactionAddEvent evt) {
        Message m = evt.getChannel().getMessageById(evt.getMessageId()).complete();
        if(m == null) // Message might not exist since it might have been deleted
            return;

        Set<User> userSet = new HashSet<>();
        for(MessageReaction reaction: m.getReactions()) {
            for(User u: reaction.getUsers()) {
                userSet.add(u);
            }
        }

        if(userSet.size() < Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPinThreshold())
            return;

        // TODO code here to pin

    }
}
