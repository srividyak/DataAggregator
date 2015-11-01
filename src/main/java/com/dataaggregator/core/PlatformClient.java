package com.dataaggregator.core;

import akka.actor.UntypedActor;
import com.dataaggregator.core.exception.PlatformException;

/**
 * Created by srividyak on 04/01/15.
 */
public abstract class PlatformClient extends UntypedActor implements IPlatformClient {

    public void reinitialize() {
        if (isClientEnabled()) {
            shutdown();
            initialize();
        }
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof PlatformCommand) {
            if (o == PlatformCommand.START) {
                initialize();
            } else if (o == PlatformCommand.SHUTDOWN) {
                shutdown();
            } else if (o == PlatformCommand.RESTART) {
                reinitialize();
            } else {
                throw new PlatformException("Unknown PlatformCommand : " + o);
            }
        } else {
            throw new PlatformException("Unknown type of command : " + o);
        }
    }
}
