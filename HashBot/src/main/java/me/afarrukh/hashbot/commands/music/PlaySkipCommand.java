package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;

public class PlaySkipCommand extends Command {

    public PlaySkipCommand() {
        super("playskip", new String[]{"pskip", "ps"});
        description = "Adds a song to the top of the queue and immediately skips to it. Basically a lazy version of ptop and skip";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(!MusicUtils.canInteract(evt) && !evt.getMember().getVoiceState().inVoiceChannel()) {
            evt.getChannel().sendMessage("You cannot call the bot if you are not in a voice channel.").queue();
            MusicUtils.cleanPlayMessage(evt);
            return;
        }

        if(params != null) {
            new PlayTopCommand().onInvocation(evt, params);
            Timer timer  = new Timer();
            timer.schedule(new WaitForQueueTimer(evt), 1000);
        }
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }

    private class WaitForQueueTimer extends TimerTask {
        final MessageReceivedEvent evt;

        private WaitForQueueTimer(MessageReceivedEvent evt) {
            this.evt = evt;
        }

        @Override
        public void run() {
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().nextTrack();
        }
    }
}
