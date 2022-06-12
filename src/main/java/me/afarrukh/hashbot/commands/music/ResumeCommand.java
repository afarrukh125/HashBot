package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResumeCommand extends Command implements MusicCommand {

    public ResumeCommand() {
        super("resume");
        addAlias("res");
        description = "Resumes the music player";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (MusicUtils.canInteract(evt)) {
            if (Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().isPaused()) {
                MusicUtils.resume(evt);
                evt.getChannel().sendMessage("Resumed.").queue();
            } else
                evt.getChannel().sendMessage("The bot is already playing.").queue();
        }
    }
}
