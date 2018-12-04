package com.codelanx.pr.runemate;

import com.runemate.game.api.script.framework.listeners.events.Event;

/**
 * Represents changes to the base {@link Event} class
 *
 * @author 1Rogue
 */
public interface CEvent extends Event {

    /**
     * Whether this event should be fired asynchronously or not. If {@code true}, the event will
     * fire upon listeners via the common {@link java.util.concurrent.ForkJoinPool}. If {@code false},
     * the event will be executed on the same thread which calls it (a blocking call).
     *
     * @return {@code true} if the event can be fired asynchronously, {@code false} otherwise
     */
    default public boolean isAsync() {
        return true;
    }

    /**
     * Whether the values of the event can be set by the listeners it is passed to.
     * Returning {@code true} for this allows for some optimizations in firing the event
     *
     * @return {@code true} if the event is not externally modifiable (immutable), {@code false} otherwise
     */
    default public boolean isImmutable() {
        return false;
    }
}
