package me.afarrukh.hashbot.gameroles;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

public interface RoleGUI {

    void handleEvent(GuildMessageReactionAddEvent evt);

    User getUser();

    Guild getGuild();

    Message getMessage();
}
