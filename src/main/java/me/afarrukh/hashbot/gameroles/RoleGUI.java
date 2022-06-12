package me.afarrukh.hashbot.gameroles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public interface RoleGUI {

    void handleEvent(MessageReactionAddEvent evt);

    User getUser();

    Guild getGuild();

    Message getMessage();

    void endSession();
}
