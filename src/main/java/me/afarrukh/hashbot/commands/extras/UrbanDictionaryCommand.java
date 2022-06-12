package me.afarrukh.hashbot.commands.extras;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.commands.tagging.ExtrasCommand;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.extras.urbandict.UrbanDictionary;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UrbanDictionaryCommand extends Command implements ExtrasCommand {

    public UrbanDictionaryCommand() {
        super("urbandictionary");
        addAlias("ud");
        description = "Performs an urban dictionary search";
        addParameter("search query", "The word to search for on urban dictionary");
        addExampleUsage("ud java");
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        String result = UrbanDictionary.getDefinition(params);

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Constants.EMB_COL);

        String[] definitionAndExample = result.split("<>");
        String definition = definitionAndExample[0];
        String example;
        try {
            eb.addField(new MessageEmbed.Field(params, definition, false));
        } catch (IllegalArgumentException e) {
            eb.appendDescription("**" + params + "**\n");
            eb.appendDescription(definition);
        }

        if (definitionAndExample.length == 2) {
            example = definitionAndExample[1];
            eb.addField(new MessageEmbed.Field("Example", example, false));
        }

        evt.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
