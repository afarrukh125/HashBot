package me.afarrukh.hashbot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.afarrukh.hashbot.commands.audiotracks.*;
import me.afarrukh.hashbot.commands.audiotracks.playlist.DeleteListCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.LoadListCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.SavePlaylistCommand;
import me.afarrukh.hashbot.commands.audiotracks.playlist.ViewListCommand;
import me.afarrukh.hashbot.commands.management.bot.*;
import me.afarrukh.hashbot.commands.management.guild.*;
import me.afarrukh.hashbot.commands.management.user.ClearCommand;
import me.afarrukh.hashbot.commands.management.user.PruneCommand;
import me.afarrukh.hashbot.core.AudioTrackManager;
import me.afarrukh.hashbot.core.CommandManager;
import me.afarrukh.hashbot.data.Database;
import net.dv8tion.jda.api.JDA;

public class CommandManagerModule extends AbstractModule {

    @Provides
    @Singleton
    public CommandManager commandManager(Database database, JDA jda, AudioTrackManager audioTrackManager) {
        CommandManager commandManager = new CommandManager()
                .withCommand(new CheckMemoryCommand())
                .withCommand(new ClearCommand(database))
                .withCommand(new ClearQueueCommand(audioTrackManager))
                .withCommand(new DeleteListCommand(database))
                .withCommand(new FairPlayCommand(audioTrackManager))
                .withCommand(new FairShuffleCommand(audioTrackManager))
                .withCommand(new InterleaveCommand(audioTrackManager))
                .withCommand(new LoadListCommand(database, audioTrackManager))
                .withCommand(new LoopCommand(audioTrackManager))
                .withCommand(new LoopQueueCommand(audioTrackManager))
                .withCommand(new MoveCommand(database, audioTrackManager))
                .withCommand(new NowPlayingCommand(audioTrackManager))
                .withCommand(new PauseCommand(database, audioTrackManager))
                .withCommand(new PingCommand())
                .withCommand(new PlayCommand(database, jda, audioTrackManager))
                .withCommand(new PlayTopCommand(database, jda, audioTrackManager))
                .withCommand(new PruneCommand(database))
                .withCommand(new PruneQueueCommand(audioTrackManager))
                .withCommand(new QueueCommand(database, audioTrackManager))
                .withCommand(new RemoveCommand(database, audioTrackManager))
                .withCommand(new RemoveRangeCommand(database, audioTrackManager))
                .withCommand(new DisconnectCommand(jda, audioTrackManager))
                .withCommand(new ResetPlayerCommand(audioTrackManager))
                .withCommand(new ResumeCommand(audioTrackManager))
                .withCommand(new ReverseQueueCommand(audioTrackManager))
                .withCommand(new RoleRGBCommand(database))
                .withCommand(new SavePlaylistCommand(database, audioTrackManager))
                .withCommand(new SeekCommand(audioTrackManager))
                .withCommand(new SetPinThresholdCommand(database))
                .withCommand(new SetPinnedChannel(database))
                .withCommand(new SetPrefixCommand(database))
                .withCommand(new SetUnpinnedCommand(database))
                .withCommand(new SetVolumeCommand(audioTrackManager))
                .withCommand(new ShuffleCommand(audioTrackManager))
                .withCommand(new SkipCommand(audioTrackManager))
                .withCommand(new SortByLengthCommand(jda, audioTrackManager))
                .withCommand(new ViewListCommand(database));

        commandManager = commandManager.withCommand(new UptimeCommand(commandManager));
        commandManager = commandManager.withCommand(new HelpCommand(database, commandManager));
        commandManager = commandManager.withCommand(new CommandListCommand(commandManager, database));
        return commandManager;
    }
}
