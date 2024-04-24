package me.afarrukh.hashbot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.afarrukh.hashbot.commands.audiotracks.ClearQueueCommand;
import me.afarrukh.hashbot.commands.audiotracks.DisconnectCommand;
import me.afarrukh.hashbot.commands.audiotracks.FairPlayCommand;
import me.afarrukh.hashbot.commands.audiotracks.FairShuffleCommand;
import me.afarrukh.hashbot.commands.audiotracks.InterleaveCommand;
import me.afarrukh.hashbot.commands.audiotracks.LoopCommand;
import me.afarrukh.hashbot.commands.audiotracks.LoopQueueCommand;
import me.afarrukh.hashbot.commands.audiotracks.MoveCommand;
import me.afarrukh.hashbot.commands.audiotracks.NowPlayingCommand;
import me.afarrukh.hashbot.commands.audiotracks.PauseCommand;
import me.afarrukh.hashbot.commands.audiotracks.PlayCommand;
import me.afarrukh.hashbot.commands.audiotracks.PlayTopCommand;
import me.afarrukh.hashbot.commands.audiotracks.PruneQueueCommand;
import me.afarrukh.hashbot.commands.audiotracks.QueueCommand;
import me.afarrukh.hashbot.commands.audiotracks.RemoveCommand;
import me.afarrukh.hashbot.commands.audiotracks.RemoveRangeCommand;
import me.afarrukh.hashbot.commands.audiotracks.ResetPlayerCommand;
import me.afarrukh.hashbot.commands.audiotracks.ResumeCommand;
import me.afarrukh.hashbot.commands.audiotracks.ReverseQueueCommand;
import me.afarrukh.hashbot.commands.audiotracks.SeekCommand;
import me.afarrukh.hashbot.commands.audiotracks.SetVolumeCommand;
import me.afarrukh.hashbot.commands.audiotracks.ShuffleCommand;
import me.afarrukh.hashbot.commands.audiotracks.SkipCommand;
import me.afarrukh.hashbot.commands.audiotracks.SortByLengthCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.DeleteListCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.LoadListCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.SavePlaylistCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.ViewListCommand;
import me.afarrukh.hashbot.commands.management.bot.CheckMemoryCommand;
import me.afarrukh.hashbot.commands.management.bot.CommandListCommand;
import me.afarrukh.hashbot.commands.management.bot.HelpCommand;
import me.afarrukh.hashbot.commands.management.bot.PingCommand;
import me.afarrukh.hashbot.commands.management.bot.UptimeCommand;
import me.afarrukh.hashbot.commands.management.bot.owner.SetNameCommand;
import me.afarrukh.hashbot.commands.management.guild.RoleRGBCommand;
import me.afarrukh.hashbot.commands.management.guild.SetPinThresholdCommand;
import me.afarrukh.hashbot.commands.management.guild.SetPinnedChannel;
import me.afarrukh.hashbot.commands.management.guild.SetPrefixCommand;
import me.afarrukh.hashbot.commands.management.guild.SetUnpinnedCommand;
import me.afarrukh.hashbot.commands.management.user.ClearCommand;
import me.afarrukh.hashbot.commands.management.user.PruneCommand;
import me.afarrukh.hashbot.core.CommandManager;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.JDA;

public class CommandManagerModule extends AbstractModule {

    @Provides
    @Singleton
    public CommandManager commandManager(Database database, JDA jda) {
        CommandManager commandManager = new CommandManager()
                .withCommand(new CheckMemoryCommand())
                .withCommand(new ClearCommand(database))
                .withCommand(new ClearQueueCommand())
                .withCommand(new DeleteListCommand(database))
                .withCommand(new FairPlayCommand())
                .withCommand(new FairShuffleCommand())
                .withCommand(new InterleaveCommand())
                .withCommand(new LoadListCommand(database))
                .withCommand(new LoopCommand())
                .withCommand(new LoopQueueCommand())
                .withCommand(new MoveCommand(database))
                .withCommand(new NowPlayingCommand())
                .withCommand(new PauseCommand(database))
                .withCommand(new PingCommand())
                .withCommand(new PlayCommand(database, jda))
                .withCommand(new PlayTopCommand(database, jda))
                .withCommand(new PruneCommand(database))
                .withCommand(new PruneQueueCommand())
                .withCommand(new QueueCommand(database))
                .withCommand(new RemoveCommand(database))
                .withCommand(new RemoveRangeCommand(database))
                .withCommand(new DisconnectCommand(jda))
                .withCommand(new ResetPlayerCommand())
                .withCommand(new ResumeCommand())
                .withCommand(new ReverseQueueCommand())
                .withCommand(new RoleRGBCommand(database))
                .withCommand(new SavePlaylistCommand(database))
                .withCommand(new SeekCommand())
                .withCommand(new SetNameCommand())
                .withCommand(new SetPinThresholdCommand(database))
                .withCommand(new SetPinnedChannel(database))
                .withCommand(new SetPrefixCommand(database))
                .withCommand(new SetUnpinnedCommand(database))
                .withCommand(new SetVolumeCommand())
                .withCommand(new ShuffleCommand())
                .withCommand(new SkipCommand())
                .withCommand(new SortByLengthCommand(jda))
                .withCommand(new ViewListCommand(database));

        commandManager = commandManager.withCommand(new UptimeCommand(commandManager));
        commandManager = commandManager.withCommand(new HelpCommand(database, commandManager));
        commandManager = commandManager.withCommand(new CommandListCommand(commandManager, database));
        return commandManager;
    }
}
