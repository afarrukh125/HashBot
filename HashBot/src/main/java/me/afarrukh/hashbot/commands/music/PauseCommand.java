package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class PauseCommand extends Command implements MusicCommand {

    public PauseCommand() {
        super("pause");
        description = "Pauses the music player";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        if (MusicUtils.canInteract(evt)) {
            ResumeCommand resumeCommand = new ResumeCommand();
            if (!Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getPlayer().isPaused()) {
                MusicUtils.pause(evt);
                evt.getChannel().sendMessage("Now paused. Type " + Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix()
                        + resumeCommand.getName() + " to resume.").queue();
            } else
                evt.getChannel().sendMessage("The bot is already paused. Type " + Bot.gameRoleManager.getGuildRoleManager(evt.getGuild()).getPrefix()
                        + resumeCommand.getName() + " to resume.").queue();
        }
    }
}
