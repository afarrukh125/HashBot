package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.music.results.YTLinkResultHandler;
import me.afarrukh.hashbot.music.results.YTSearchResultHandler;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PlayCommand extends Command implements MusicCommand {

    public PlayCommand() {
        super("play", new String[] {"p"});
        description = "Adds a song to the music queue";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if(params == null) {
            onIncorrectParams(evt.getTextChannel());
            return;
        }
        if(!MusicUtils.canInteract(evt) && !evt.getMember().getVoiceState().inVoiceChannel()) {
            evt.getChannel().sendMessage("You cannot call the bot if you are not in a voice channel.").queue();
            MusicUtils.cleanPlayMessage(evt);
            return;
        }

        GuildMusicManager gmm = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());
        if(params.split(" ").length == 1 && params.contains("http")) {
            Bot.musicManager.getPlayerManager().loadItemOrdered(gmm, params, new YTLinkResultHandler(gmm, evt, false));
            evt.getMessage().delete().queue();
        }
        else
            Bot.musicManager.getPlayerManager().loadItem("ytsearch: " + params, new YTSearchResultHandler(gmm, evt, false));
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: play <youtube search> OR play <youtube link>").queue();
    }
}
