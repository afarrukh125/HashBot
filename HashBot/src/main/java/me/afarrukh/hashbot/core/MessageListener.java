package me.afarrukh.hashbot.core;

import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import me.afarrukh.hashbot.config.Constants;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        if(evt.isFromType(ChannelType.PRIVATE))
            return;
        if(evt.getAuthor().isBot())
            return;
        if(!evt.getMessage().getAttachments().isEmpty())
            return;
        if(evt.getMessage().getContentRaw().startsWith(Constants.invokerChar)) {
            Bot.commandManager.processEvent(evt);
            return;
        }
        Invoker invoker = new Invoker(evt.getMember());
        if(invoker.hasTimePassed()) {
            invoker.addRandomCredit();
            invoker.updateExperience(evt.getMessage().getContentRaw());
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent evt) {
        VoiceChannel vc = evt.getChannelLeft();

        GuildMusicManager manager = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());

        //Pause if no users in channel
        if(vc.getMembers().size() == 1 && !manager.getPlayer().isPaused() && evt.getGuild().getAudioManager().isConnected())
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(true);

        if(Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().getPlayingTrack() == null) {
            MusicUtils.disconnect(evt.getGuild());
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent evt) {
        VoiceChannel vc = evt.getChannelJoined();
        GuildMusicManager manager = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());

        //Check if the user to join is the first to join and resume if it is already paused
        if((vc.getMembers().size() == 2) && manager.getPlayer().isPaused() && evt.getGuild().getAudioManager().isConnected())
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().setPaused(false);
    }
}
