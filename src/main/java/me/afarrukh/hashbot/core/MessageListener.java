package me.afarrukh.hashbot.core;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.data.Database;
import me.afarrukh.hashbot.track.GuildAudioTrackManager;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.DisconnectTimer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;

import static java.util.Objects.nonNull;

@Singleton
public class MessageListener extends ListenerAdapter {

    private final CommandManager commandManager;
    private final AudioTrackManager trackManager;
    private final ReactionManager reactionManager;
    private final JDA botUser;

    @Inject
    public MessageListener(
            CommandManager commandManager,
            AudioTrackManager trackManager,
            ReactionManager reactionManager,
            JDA botUser) {

        this.commandManager = commandManager;
        this.trackManager = trackManager;
        this.reactionManager = reactionManager;
        this.botUser = botUser;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        if (evt.getAuthor().isBot()) {
            return;
        }

        var injector = Guice.createInjector(new CoreBotModule());
        Database database = Database.getInstance();
        String prefix = database.getPrefixForGuild(evt.getGuild().getId());
        if (evt.getMessage().getContentRaw().startsWith(prefix)) {
            commandManager.processEvent(evt);
            return;
        }

        String userId = evt.getMember().getId();
        if (BotUtils.isPinnedChannel(evt)
                && !userId.equals(botUser.getSelfUser().getId())) {
            evt.getMessage().delete().queue();
        }
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent evt) {
        AudioChannel joinedChannel = evt.getChannelJoined();
        if (nonNull(joinedChannel)) {
            if (!joinedChannel
                    .getMembers()
                    .contains(evt.getGuild()
                            .getMemberById(evt.getJDA().getSelfUser().getId()))) return;

            GuildAudioTrackManager manager = trackManager.getGuildAudioPlayer(evt.getGuild());

            // Check if the user to join is the first to join and resume if it is already paused
            if ((joinedChannel.getMembers().size() == 2)
                    && manager.getPlayer().isPaused()
                    && evt.getGuild().getAudioManager().isConnected()) {
                trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(false);

                trackManager.getGuildAudioPlayer(evt.getGuild()).resetDisconnectTimer();
            }
        }

        AudioChannel leftChannel = evt.getChannelLeft();
        if (nonNull(leftChannel)) {
            Member botMember =
                    evt.getGuild().getMemberById(evt.getJDA().getSelfUser().getId());
            if (!leftChannel.getMembers().contains(botMember)) {
                return;
            }

            GuildAudioTrackManager manager = trackManager.getGuildAudioPlayer(evt.getGuild());

            if (trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack() == null) {
                AudioTrackUtils.disconnect(evt.getGuild());
                return;
            }

            // Pause if no users in channel
            if (leftChannel.getMembers().size() == 1
                    && !manager.getPlayer().isPaused()
                    && evt.getGuild().getAudioManager().isConnected()) {
                trackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(true);

                Timer disconnectTimer =
                        trackManager.getGuildAudioPlayer(evt.getGuild()).getDisconnectTimer();
                disconnectTimer.schedule(new DisconnectTimer(evt.getGuild()), Constants.DISCONNECT_DELAY * 1000);
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent evt) {
        if (evt.getUser().isBot()) return;
        reactionManager.processForPinning(evt);
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent evt) {
        botUser.getPresence()
                .setActivity(Activity.playing(" in " + botUser.getGuilds().size() + " guilds"));
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent evt) {
        botUser.getPresence()
                .setActivity(Activity.playing(" in " + botUser.getGuilds().size() + " guilds"));
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent evt) {
        var database = Database.getInstance();
        String guildId = evt.getGuild().getId();
        database.getPinnedChannelIdForGuild(guildId).ifPresent(pinnedChannelId -> {
            if (evt.getGuild().getTextChannelById(pinnedChannelId) != null) {
                // If the message deleted is a pinned message remove its entry by pinned message id
                database.getPinnedChannelIdForGuild(guildId)
                        .ifPresentOrElse(
                                channelId -> {
                                    if (evt.getChannel().getId().equals(channelId)) {
                                        database.deletePinnedMessageEntryByBotPinnedMessageId(
                                                guildId, evt.getMessageId());
                                    }
                                },
                                () -> {
                                    if (database.isBotPinMessageInGuild(guildId, evt.getMessageId())) {
                                        database.deletePinnedMessageEntryByOriginalMessageId(
                                                guildId, evt.getMessageId());
                                    }
                                });
            }
        });
    }
}
