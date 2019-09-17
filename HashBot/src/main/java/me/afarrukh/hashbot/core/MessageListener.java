package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.gameroles.RoleGUI;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.DisconnectTimer;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Timer;

class MessageListener extends ListenerAdapter {

    /**
     * Processes any message being received on every single message received event
     *
     * @param evt The message received event being passed in to process
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent evt) {
        if (evt.getAuthor().isBot())
            return;
        if (evt.getMessage().getContentRaw().startsWith(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix())) {
            Bot.commandManager.processEvent(evt);
            return;
        }
        if (BotUtils.isPinnedChannel(evt) && !evt.getMember().getUser().getId().equals(Bot.botUser.getSelfUser().getId())) {
            evt.getMessage().delete().queue();
            return;
        }
        Invoker invoker = new Invoker(evt.getMember());
        if (invoker.hasTimePassed()) {
            invoker.addRandomCredit();
            if (!evt.getMessage().getAttachments().isEmpty())
                invoker.addRandomExperience();
            else
                invoker.updateExperience(evt.getMessage().getContentRaw());
        }
        RoleGUI rb = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).modifierForUser(evt.getAuthor());
        if (rb == null)
            return;
        if (rb instanceof RoleBuilder)
            ((RoleBuilder) rb).handleEvent(evt);
    }

    /**
     * @param evt The event associated with the deletion of a role
     */
    public void onRoleDelete(RoleDeleteEvent evt) {
        if (BotUtils.isGameRole(evt.getRole(), evt.getGuild())) {
            GuildDataMapper.getInstance().getDataManager(evt.getGuild()).removeRole(evt.getRole().getName()); // Remove from file
            Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).removeRole(evt.getRole().getName()); // Remove from live game role list
        }
    }

    /**
     * @param evt The event associated with a member leaving guild voice
     */
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent evt) {

        VoiceChannel vc = evt.getChannelLeft();

        if (!vc.getMembers().contains(evt.getGuild().getMemberById(evt.getJDA().getSelfUser().getId()))) // If the channel that the event is associated with does not contain the bot
            return;

        GuildMusicManager manager = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());

        if (Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack() == null) {
            MusicUtils.disconnect(evt.getGuild());
            return;
        }

        //Pause if no users in channel
        if (vc.getMembers().size() == 1 && !manager.getPlayer().isPaused() && evt.getGuild().getAudioManager().isConnected()) {
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(true);

            Timer disconnectTimer = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getDisconnectTimer();
            disconnectTimer.schedule(new DisconnectTimer(evt.getGuild()), Constants.DISCONNECT_DELAY * 1000);
        }
    }

    /**
     * @param evt The event associated with a member joining guild voice
     */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent evt) {
        VoiceChannel vc = evt.getChannelJoined();

        if (!vc.getMembers().contains(evt.getGuild().getMemberById(evt.getJDA().getSelfUser().getId())))
            return;

        GuildMusicManager manager = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());

        //Check if the user to join is the first to join and resume if it is already paused
        if ((vc.getMembers().size() == 2) && manager.getPlayer().isPaused() && evt.getGuild().getAudioManager().isConnected()) {
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(false);

            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).resetDisconnectTimer();
        }
    }

    /**
     * @param evt The event associated with a reaction being added to a message
     */
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent evt) {
        if (evt.getUser().isBot()) return;
        Bot.reactionManager.processForPinning(evt);
        Bot.reactionManager.sendToModifier(evt);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent evt) {
        Bot.botUser.getPresence().setGame(Game.playing(" in " + Bot.botUser.getGuilds().size() + " guilds"));
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent evt) {
        Bot.botUser.getPresence().setGame(Game.playing(" in " + Bot.botUser.getGuilds().size() + " guilds"));
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent evt) {
        GuildDataManager gdm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());

        if (gdm.getPinnedChannelId().equals(""))
            return;

        if (evt.getGuild().getTextChannelById(gdm.getPinnedChannelId()) == null)
            return;

        // If the message deleted is a pinned message remove its entry by pinned message id
        if (evt.getChannel().getId().equals(gdm.getPinnedChannelId())) {
            gdm.deletePinnedEntryByNew(evt.getMessageId());
        } else {
            // Otherwise remove it by normal message id
            if (gdm.isPinned(evt.getMessageId())) {

                // Get the associated pinned message and delete it
//                String pinnedMessageId = gdm.getPinnedMessageIdFromOriginalMessage(evt.getMessageId());
//                evt.getGuild().getTextChannelById(gdm.getPinnedChannelId()).deleteMessageById((pinnedMessageId)).queue();

                // Remove from data manager
                gdm.deletePinnedEntryByOriginal(evt.getMessageId());
            }
        }

    }
}
