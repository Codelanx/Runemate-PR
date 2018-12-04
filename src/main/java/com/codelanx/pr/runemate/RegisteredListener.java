package com.codelanx.pr.runemate;

import com.runemate.game.api.script.framework.listeners.events.Event;

/**
 * Represents an active listener
 *
 * @author 1Rogue
 *
 * @param <E> The type of {@link Event} being listened to
 */
public final class RegisteredListener<E extends Event> implements Listener<E> {

    private final EventBus bus;
    private final Class<? extends E> type;
    private final Listener<? super E> listener;
    private final Priority priority;

    RegisteredListener(EventBus bus, Class<? extends E> type, Priority priority, Listener<? super E> listener) {
        this.bus = bus;
        this.type = type;
        this.priority = priority;
        this.listener = listener;
    }

    /**
     * Cancels this listener and stops events from being fired on it
     */
    public void cancel() {
        this.bus.cancel(this);
    }

    /**
     * {@inheritDoc}
     *
     * @param event {@inheritDoc}
     */
    @Override
    public void handle(E event) {
        this.listener.handle(event);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Priority getProirity() {
        return this.priority;
    }

    //dummy method, ended up being a complicated hot mess to clone events without a concrete class
    private <T extends Event> T clone(T event) {
        if (event instanceof CEvent) {

        }
        return event; //TODO: Clone handling :(
    }


    //the event type in use by this listener
    Class<? extends E> getType() {
        return this.type;
    }

    //fires this listener
    E fire(E event) {
        E cl = this.clone(event);
        try {
            this.handle(event);
        } catch (Exception ex) {
            //TODO: uncaught exception handler
            return event;
        }
        return cl;
    }
}
