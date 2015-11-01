package com.dataaggregator;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.dataaggregator.core.actors.Master;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;

/**
 * Created by srividyak on 01/01/15.
 */
public class Main {

    private ActorSystem actorSystem;

    private ActorRef master;

    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        String configDir = System.getProperty("config.path", "config" + File.separator);
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext(configDir + "applicationContext.xml");
        new Main().init(applicationContext);
    }

    public void init(ApplicationContext context) {
        actorSystem = ActorSystem.create("root");
        master = actorSystem.actorOf(Props.create(Master.class, actorSystem, this.getClass().getPackage().getName(), context));
    }

}
