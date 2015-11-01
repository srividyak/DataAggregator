package com.dataaggregator.core;

import akka.actor.Actor;

/**
 * Created by srividyak on 04/01/15.
 */
public interface IPlatformClient extends Actor {

    void initialize();

    void shutdown();

    void reinitialize();

    boolean isClientEnabled();

}
