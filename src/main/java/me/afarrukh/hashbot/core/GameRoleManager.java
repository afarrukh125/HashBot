package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.gameroles.GuildGameRoleManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * A game role is a role added to discord servers that usually defines a game being played by the users.
 * It is something which is useful for smaller guilds, say if you want to add a role with the name of a game,
 * users can add this role to their list of roles and when this role is mentioned, users with that role
 * will be given a notification. Say for example you wish to add Overwatch as a GameRole, you would run the
 * CreateRoleCommand, and set up the colour and role name. After that, other members can add this role using
 * AddRoleCommand and now the role is set up. If the users wish to notify all users with that role they would
 * simply mention the Overwatch GameRole. This could be useful in the context of finding other users to play with.
 * <p>
 * In the event that this notification gets annoying, users can remove this role using RemoveRoleCommand.
 * The reason why I have chosen to represent a GameRole as a separate object from standard roles is because
 * they are all not hoisted roles (shown separately in discord), and we don't want to have any of the
 * GameRole adders/removers/deleters/creators interacting with administrator roles.
 */
public class GameRoleManager {

    /**
     * A map which has the guild ID as a key and the GuildGameRoleManager as the value associated with it.
     */
    private final Map<Long, GuildGameRoleManager> gameRoleManagers;

    public GameRoleManager() {
        this.gameRoleManagers = new HashMap<>();

        for (Guild guild : Bot.botUser().getGuilds()) {
            getGuildRoleManager(guild);
        }
    }

    /**
     * Returns the guild game role manager for the guild. Creates one and adds to the map if none exists.
     *
     * @param guild The guild for which the game role manager is to be retrieved
     * @return The guild game role manager for the guild
     */
    public synchronized GuildGameRoleManager getGuildRoleManager(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildGameRoleManager roleManager = gameRoleManagers.get(guildId); //Gets the current role manager for this guild

        if (roleManager == null) { // If the guild doesn't already have a role manager then create one
            roleManager = new GuildGameRoleManager(guild);
            gameRoleManagers.put(guildId, roleManager);
        }

        return roleManager;
    }
}
