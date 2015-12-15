package example.service;

import org.springframework.context.ApplicationEvent;

/**
 * This is an optional class used in publishing application events.
 * This can be used to inject events into the Spring Boot audit management endpoint.
 */
public class ProductServiceEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ProductServiceEvent(Object source) {
        super(source);
    }

    @Override
    public String toString() {
        return "ProductServiceEvent{}";
    }
}
