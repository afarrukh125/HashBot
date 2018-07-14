package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.gameroles.RoleBuilder;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.utils.BotUtils;
import me.afarrukh.hashbot.utils.DisconnectTimer;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Timer;

class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        if(evt.isFromType(ChannelType.PRIVATE))
            return;
        if(evt.getAuthor().isBot())
            return;
        if(!evt.getMessage().getAttachments().isEmpty())
            return;
        if(evt.getMessage().getContentRaw().startsWith(Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix())) {
            Bot.commandManager.processEvent(evt);
            return;
        }
        if(evt.getMessage().getAttachments().isEmpty() && BotUtils.isPinnedChannel(evt) && !evt.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            evt.getMessage().delete().queue();
            return;
        }
        Invoker invoker = new Invoker(evt.getMember());
        if(invoker.hasTimePassed()) {
            invoker.addRandomCredit();
            invoker.updateExperience(evt.getMessage().getContentRaw());
        }
        RoleBuilder rb = Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).builderForUser(evt.getAuthor());
        if(rb == null) {
        }
        else
            rb.handleEvent(evt);
    }

    public void onRoleDelete(RoleDeleteEvent evt) {
        if(BotUtils.isGameRole(evt.getRole(), evt.getGuild()))
            new JSONGuildManager(evt.getGuild()).removeRole(evt.getRole().getName());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent evt) {
        VoiceChannel vc = evt.getChannelLeft();

        GuildMusicManager manager = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());

        //Pause if no users in channel
        if(vc.getMembers().size() == 1 && !manager.getPlayer().isPaused() && evt.getGuild().getAudioManager().isConnected()) {
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(true);
            Timer disconnectTimer = Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getDisconnectTimer();
            disconnectTimer.schedule(new DisconnectTimer(evt.getGuild()), 5*1000);
        }

        if(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack() == null)
            MusicUtils.disconnect(evt.getGuild());
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent evt) {
        VoiceChannel vc = evt.getChannelJoined();
        GuildMusicManager manager = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());

        //Check if the user to join is the first to join and resume if it is already paused
        if((vc.getMembers().size() == 2) && manager.getPlayer().isPaused() && evt.getGuild().getAudioManager().isConnected()) {
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(false);

            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getDisconnectTimer().cancel();
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).setDisconnectTimer(new Timer());
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent evt) {
        if (evt.getUser().isBot()) return;
        Bot.reactionManager.sendToBuilder(evt);
        Bot.reactionManager.sendToAdder(evt);
        Bot.reactionManager.sendToRemover(evt);
    }


}
