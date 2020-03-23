package me.afarrukh.hashbot.gameroles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public interface RoleGUI {

    void handleEvent(GuildMessageReactionAddEvent evt);

    User getUser();

    Guild getGuild();

    Message getMessage();

    void endSession();
}
