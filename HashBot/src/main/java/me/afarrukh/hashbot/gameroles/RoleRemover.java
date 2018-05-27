package me.afarrukh.hashbot.gameroles;

import me.afarrukh.hashbot.core.Bot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

public class RoleRemover implements RoleGUI {

    private User user;
    private Guild guild;
    private Message message;

    private int page = 1;
    private int stage = 1;

    public RoleRemover(MessageReceivedEvent evt) {
        this.user = evt.getAuthor();
        this.guild = evt.getGuild();

        Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getRoleRemovers().add(this);
    }

    public void handleEvent(GuildMessageReactionAddEvent evt) {

    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Guild getGuild() {
        return guild;
    }

    @Override
    public Message getMessage() {
        return message;
    }
}
