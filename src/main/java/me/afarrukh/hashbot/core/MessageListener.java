package me.afarrukh.hashbot.core;

import com.google.inject.Inject;
import me.afarrukh.hashbot.config.Config;
import me.afarrukh.hashbot.config.Constants;
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

import static java.util.Objects.nonNull;

public class MessageListener extends ListenerAdapter {

    private final Config config;
    private final Database database;
    private final JDA jda;
    private final ReactionManager reactionManager;
    private final CommandManager commandManager;
    private final AudioTrackManager audioTrackManager;

    @Inject
    public MessageListener(Config config, Database database, JDA jda, ReactionManager reactionManager, CommandManager commandManager,
                           AudioTrackManager audioTrackManager) {
        this.config = config;
        this.database = database;
        this.jda = jda;
        this.reactionManager = reactionManager;
        this.commandManager = commandManager;
        this.audioTrackManager = audioTrackManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        if (evt.getAuthor().isBot()) {
            return;
        }
        String prefix = database.getPrefixForGuild(evt.getGuild().getId());
        if (evt.getMessage().getContentRaw().startsWith(prefix)) {
            commandManager.processEvent(evt, config);
            return;
        }

        String userId = evt.getMember().getId();
        if (BotUtils.isPinnedChannel(database, evt)
                && !userId.equals(jda.getSelfUser().getId())) {
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

            GuildAudioTrackManager manager = audioTrackManager.getGuildAudioPlayer(evt.getGuild());

            // Check if the user to join is the first to join and resume if it is already paused
            if ((joinedChannel.getMembers().size() == 2)
                    && manager.getPlayer().isPaused()
                    && evt.getGuild().getAudioManager().isConnected()) {
                audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(false);

                audioTrackManager.getGuildAudioPlayer(evt.getGuild()).resetDisconnectTimer();
            }
        }

        AudioChannel leftChannel = evt.getChannelLeft();
        if (nonNull(leftChannel)) {
            Member botMember =
                    evt.getGuild().getMemberById(evt.getJDA().getSelfUser().getId());
            if (!leftChannel.getMembers().contains(botMember)) {
                return;
            }

            GuildAudioTrackManager manager = audioTrackManager.getGuildAudioPlayer(evt.getGuild());

            if (audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack() == null) {
                AudioTrackUtils.disconnect(evt.getGuild(), audioTrackManager);
                return;
            }

            // Pause if no users in channel
            if (leftChannel.getMembers().size() == 1
                    && !manager.getPlayer().isPaused()
                    && evt.getGuild().getAudioManager().isConnected()) {
                audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(true);

                var disconnectTimer =
                        audioTrackManager.getGuildAudioPlayer(evt.getGuild()).getDisconnectTimer();
                disconnectTimer.schedule(new DisconnectTimer(evt.getGuild(), audioTrackManager), Constants.DISCONNECT_DELAY * 1000);
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent evt) {
        if (evt.getUser().isBot()) {
            return;
        }
        reactionManager.processForPinning(evt);
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent evt) {
        jda
                .getPresence()
                .setActivity(Activity.playing(" in " + jda.getGuilds().size() + " guilds"));
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent evt) {
        jda
                .getPresence()
                .setActivity(Activity.playing(" in " + jda.getGuilds().size() + " guilds"));
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent evt) {
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
