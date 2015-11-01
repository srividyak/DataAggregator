package com.dataaggregator.core.actors;

import akka.actor.UntypedActor;
import com.dataaggregator.core.PlatformCommand;
import org.apache.log4j.Logger;

import java.nio.file.*;
import java.util.List;

/**
 * Created by srividyak on 15/01/15.
 */
public class PropertiesWatcher extends UntypedActor {

    private boolean watch;
    private String directoryToWatch;
    private static final Logger LOG = Logger.getLogger(PropertiesWatcher.class);

    private void setWatch(boolean watch) {
        this.watch = watch;
    }

    private void startWatching() {
        try {
            Path directory = Paths.get(directoryToWatch);
            while (watch) {
                WatchService watchService = directory.getFileSystem().newWatchService();
                directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                WatchKey watchKey = watchService.take();
                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent event : events) {
                    String fileName = event.context().toString();
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY && fileName.endsWith(".properties")) {
                        getSender().tell(fileName, getSelf());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    public PropertiesWatcher(String directory) {
        directoryToWatch = directory;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof PlatformCommand) {
            if (o == PlatformCommand.START) {
                setWatch(true);
                startWatching();
            } else if (o == PlatformCommand.SHUTDOWN){
                setWatch(false);
            } else if (o == PlatformCommand.RESTART) {
                setWatch(true);
                startWatching();
            }
        }
    }
}
