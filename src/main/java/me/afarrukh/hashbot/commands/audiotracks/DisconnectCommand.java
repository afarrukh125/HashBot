package me.afarrukh.hashbot.commands.audiotracks;

import com.google.inject.Guice;
import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.AudioTrackCommand;
import me.afarrukh.hashbot.core.Bot;
import me.afarrukh.hashbot.core.module.CoreBotModule;
import me.afarrukh.hashbot.utils.AudioTrackUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DisconnectCommand extends Command implements AudioTrackCommand {
    public DisconnectCommand() {
        super("disconnect");
        addAlias("dc");
        addAlias("d");
        addAlias("leave");
        description = "Disconnects the bot if it is already in a voice channel";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        if (!AudioTrackUtils.canInteract(evt)) {
            return;
        }

        var injector = Guice.createInjector(new CoreBotModule());

        if (evt.getGuild()
                .getMemberById(injector.getInstance(Bot.class)
                        .getBotUser()
                        .getSelfUser()
                        .getId())
                .getVoiceState()
                .getChannel()
                .equals(evt.getMember().getVoiceState().getChannel())) {
            AudioTrackUtils.disconnect(evt.getGuild());
        }
    }
}
