package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ClearQueueCommand extends Command implements MusicCommand {

    public ClearQueueCommand() {
        super("clearqueue");
        addAlias("cq");
        description = "Clears the current queue for the music player";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        if (!MusicUtils.canInteract(evt))
            return;

        if (Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getQueue().isEmpty()) {
            evt.getChannel().sendMessage("Nothing is in the queue to be cleared.").queue();
            return;
        }
        Bot.musicManager.getGuildAudioPlayer(evt.getGuild()).getScheduler().getQueue().clear();
        evt.getChannel().sendMessage("Queue cleared :ok_hand:").queue();
    }
}
