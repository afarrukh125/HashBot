package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class ShuffleCommand extends Command implements MusicCommand {

    public ShuffleCommand() {
        super("shuffle");
        description = "Shuffles the music queue";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        if (MusicUtils.canInteract(evt)) {
            Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().shuffle();
            evt.getChannel().sendMessage("Shuffled the playlist :ok_hand:").queue();
        }
    }
}
