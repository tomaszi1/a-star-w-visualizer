package pl.edu.agh.idziak.asw.visualizer;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tomasz on 14.03.2017.
 */
public enum GlobalEventBus {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(GlobalEventBus.class);
    private final EventBus eventBus;

    GlobalEventBus() {
        SubscriberExceptionHandler exceptionHandler = new SubscriberExceptionHandler() {
            @Override public void handleException(Throwable exception, SubscriberExceptionContext context) {
                LOG.error("Event bus subscriber threw exception", exception);
            }
        };
        eventBus = new EventBus(exceptionHandler);
    }


    public EventBus get() {
        return eventBus;
    }
}
