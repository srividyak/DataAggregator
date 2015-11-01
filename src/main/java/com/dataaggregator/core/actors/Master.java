package com.dataaggregator.core.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.dataaggregator.core.ClientComponent;
import com.dataaggregator.core.PlatformCommand;
import com.dataaggregator.util.PropertyReader;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.*;

/**
 * Created by srividyak on 15/01/15.
 */
public class Master extends UntypedActor {

    private ActorSystem actorSystem;

    private Map<String, ActorRef> platformClientActorRefMap;

    private ActorRef propsWatcherRef;

    private static final Logger LOG = Logger.getLogger(Master.class);

    private ApplicationContext appContext;

    private static final PropertyReader reader = PropertyReader.getInstance();
    private static final String platformClients = reader.getProperty("platformClients");
    private static Map<String, String> propFileToClientMap = new HashMap<String, String>();

    /**
     *
     * @param rootPackage root package containing all classes annotated with @ClientComponent
     * @param context spring application context
     */
    public Master(ActorSystem actorSystem, String rootPackage, ApplicationContext context) {
        appContext = context;
        this.actorSystem = actorSystem;
        platformClientActorRefMap = new HashMap<String, ActorRef>();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                startCleanup();
            }
        });
        initWatcher();
        initializePlatformClients(rootPackage);
    }

    private void initWatcher() {
        String propsDir = System.getProperty("config.path", System.getProperty("user.dir") + File.separator + "config");
        propsWatcherRef = actorSystem.actorOf(Props.create(PropertiesWatcher.class, propsDir));
        propsWatcherRef.tell(PlatformCommand.START, getSelf());
    }

    private void initializePlatformClients(String rootPackage) {
        Reflections reflections = new Reflections(rootPackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(ClientComponent.class);
        Iterator<Class<?>> iterator = annotatedClasses.iterator();
        while (iterator.hasNext()) {
            Class<?> clazz = iterator.next();
            ClientComponent annotation = clazz.getAnnotation(ClientComponent.class);
            ActorRef actorRef = actorSystem.actorOf(Props.create(clazz, appContext));
            platformClientActorRefMap.put(annotation.name(), actorRef);
            if (!annotation.propertiesFileName().isEmpty()) {
                propFileToClientMap.put(annotation.propertiesFileName(), annotation.name());
            }
            actorRef.tell(PlatformCommand.START, getSelf());
        }
    }

    private void startCleanup() {
        if (platformClientActorRefMap != null) {
            for (Map.Entry<String, ActorRef> entry : platformClientActorRefMap.entrySet()) {
                ActorRef ref = entry.getValue();
                LOG.debug("shutting down actor: " + ref);
                ref.tell(PlatformCommand.SHUTDOWN, getSelf());
            }
        }
        actorSystem.shutdown();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        // from properties watcher
        if (o instanceof String) {
            if (propFileToClientMap.containsKey(o)) {
                String client = propFileToClientMap.get(o);
                if (platformClientActorRefMap.containsKey(client)) {
                    LOG.debug("restarting client: " + client);
                    platformClientActorRefMap.get(client).tell(PlatformCommand.RESTART, getSelf());
                }
            }
        }
    }
}
