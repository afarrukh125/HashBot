package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AdminCommand;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ResetPlayerCommand extends Command implements MusicCommand, AdminCommand {

    public ResetPlayerCommand() {
        super("resetplayer");
        addAlias("rp");
        description = "Resets the music player for this guild. Use if any issues are occurring with the music player. Admin only command.";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!evt.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;

        MusicUtils.disconnect(evt.getGuild());
        Bot.musicManager.resetGuildAudioPlayer(evt.getGuild());
        evt.getTextChannel().sendMessage("Reset the audio player for this guild successfully.").queue();
    }
}
