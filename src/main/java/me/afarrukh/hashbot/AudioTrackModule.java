package me.afarrukh.hashbot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.afarrukh.hashbot.core.AudioTrackManager;

public class AudioTrackModule extends AbstractModule {

    @Provides
    @Singleton
    public AudioTrackManager audioTrackManager() {
        return new AudioTrackManager();
    }
}
