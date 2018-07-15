package me.afarrukh.hashbot.gameroles;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

interface RoleGUI {

    User getUser();

    Guild getGuild();

    Message getMessage();

    void handleEvent(GuildMessageReactionAddEvent evt);
}
