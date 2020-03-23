package me.afarrukh.hashbot.commands.music;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.MusicCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.utils.MusicUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DisconnectCommand extends Command implements MusicCommand {
    public DisconnectCommand() {
        super("disconnect");
        addAlias("dc");
        addAlias("d");
        addAlias("leave");
        description = "Disconnects the bot if it is already in a voice channel";
    }

    @Override
    public void onInvocation(GuildMessageReceivedEvent evt, String params) {
        if (!MusicUtils.canInteract(evt))
            return;

        if (evt.getGuild().getMemberById(Bot.botUser.getSelfUser().getId()).getVoiceState().getChannel().equals(evt.getMember().getVoiceState().getChannel()))
            MusicUtils.disconnect(evt.getGuild());
    }
}
