package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.data.GuildDataManager;
import me.afarrukh.hashbot.data.GuildDataMapper;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.gameroles.RoleGUI;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.DisconnectTimer;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;

class MessageListener extends ListenerAdapter {

    /**
     * Processes any message being received on every single message received event
     *
     * @param evt The message received event being passed in to process
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
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
        Invoker invoker = Invoker.of(evt.getMember());
        if (invoker.hasTimePassed()) {
            invoker.addRandomCredit();
            invoker.addRandomExperience();
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
        AudioChannel vc = evt.getChannelLeft();

        if (!vc.getMembers().contains(evt.getGuild().getMemberById(evt.getJDA().getSelfUser().getId()))) // If the channel that the event is associated with does not contain the bot
            return;

        GuildAudioTrackManager manager = Bot.trackManager.getGuildAudioPlayer(evt.getGuild());

        if (Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack() == null) {
            AudioTrackUtils.disconnect(evt.getGuild());
            return;
        }

        //Pause if no users in channel
        if (vc.getMembers().size() == 1 && !manager.getPlayer().isPaused() && evt.getGuild().getAudioManager().isConnected()) {
            Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(true);

            Timer disconnectTimer = Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getDisconnectTimer();
            disconnectTimer.schedule(new DisconnectTimer(evt.getGuild()), Constants.DISCONNECT_DELAY * 1000);
        }
    }

    /**
     * @param evt The event associated with a member joining guild voice
     */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent evt) {
        AudioChannel vc = evt.getChannelJoined();

        if (!vc.getMembers().contains(evt.getGuild().getMemberById(evt.getJDA().getSelfUser().getId())))
            return;

        GuildAudioTrackManager manager = Bot.trackManager.getGuildAudioPlayer(evt.getGuild());

        //Check if the user to join is the first to join and resume if it is already paused
        if ((vc.getMembers().size() == 2) && manager.getPlayer().isPaused() && evt.getGuild().getAudioManager().isConnected()) {
            Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(false);

            Bot.trackManager.getGuildAudioPlayer(evt.getGuild()).resetDisconnectTimer();
        }
    }

    /**
     * @param evt The event associated with a reaction being added to a message
     */
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent evt) {
        if (evt.getUser().isBot()) return;
        Bot.reactionManager.processForPinning(evt);
        Bot.reactionManager.sendToModifier(evt);
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent evt) {
        Bot.botUser.getPresence().setActivity(Activity.playing(" in " + Bot.botUser.getGuilds().size() + " guilds"));
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent evt) {
        Bot.botUser.getPresence().setActivity(Activity.playing(" in " + Bot.botUser.getGuilds().size() + " guilds"));
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent evt) {
        GuildDataManager gdm = GuildDataMapper.getInstance().getDataManager(evt.getGuild());

        if (gdm.getPinnedChannelId() == null || gdm.getPinnedChannelId().equals(""))
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
