package me.afarrukh.hashbot.gameroles;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

public interface RoleGUI {

    public User getUser();

    public Guild getGuild();

    public Message getMessage();

    public void handleEvent(GuildMessageReactionAddEvent evt);
}
