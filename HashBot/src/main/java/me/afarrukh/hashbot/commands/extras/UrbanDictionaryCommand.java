package me.afarrukh.hashbot.commands.extras;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.extras.urbandict.UrbanDictionary;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UrbanDictionaryCommand extends Command{

    public UrbanDictionaryCommand() {
        super("urbandictionary", new String[]{"ud"});
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        String result = UrbanDictionary.getDefinition(params);

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Constants.EMB_COL);

        String[] definitionAndExample = result.split("<>");
        String definition = definitionAndExample[0];
        String example;
        eb.addField(new MessageEmbed.Field(params, definition, false));

        if(definitionAndExample.length == 2) {
            example = definitionAndExample[1];
            eb.addField(new MessageEmbed.Field("Example", example, false));
        }

        evt.getTextChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }
}
