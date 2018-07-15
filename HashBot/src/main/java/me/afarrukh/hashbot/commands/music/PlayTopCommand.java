package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.music.GuildMusicManager;
import me.afarrukh.hashbot.music.results.YTLinkResultHandler;
import me.afarrukh.hashbot.music.results.YTSearchResultHandler;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PlayTopCommand extends Command {

    public PlayTopCommand() {
        super("playtop", new String[] {"ptop"});
        description = "Adds a song to the top of the music queue";
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

        if(new Invoker(evt.getMember()).getCredit() < Constants.PLAY_TOP_COST) {
            evt.getTextChannel().sendMessage("You need at least " +Constants.PLAY_TOP_COST+ " credit to queue songs to the top of the list.").queue();
            return;
        }
        GuildMusicManager gmm = Bot.musicManager.getGuildAudioPlayer(evt.getGuild());
        if(params.split(" ").length == 1 && params.contains("http")) {
            Bot.musicManager.getPlayerManager().loadItemOrdered(gmm, params, new YTLinkResultHandler(gmm, evt, true));
            evt.getMessage().delete().queue();
        }
        else
            Bot.musicManager.getPlayerManager().loadItem("ytsearch: " + params, new YTSearchResultHandler(gmm, evt, true));
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {
        channel.sendMessage("Usage: play <youtube search> OR play <youtube link>").queue();
    }
}
