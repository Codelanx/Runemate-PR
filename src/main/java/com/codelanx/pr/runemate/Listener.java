package com.codelanx.pr.runemate;

import com.runemate.game.api.script.framework.listeners.events.Event;

import java.util.EventListener;

/**
 * Represents a basic listener which accepts the event that was fired
 *
 * @author 1Rogue
 *
 * @param <E> The type of {@link Event} this listener handles
 */
@FunctionalInterface
public interface Listener<E extends Event> extends EventListener {

    /**
     * Applies this listener to the given {@link Event}
     *
     * @param event The {@link Event} being handled
     */
    public void handle(E event);

    /**
     * The {@link Priority} of this listener, which assigns the order in which listeners are fired
     *
     * @return Default {@link Priority#NORMAL}, can be overridden by an implemented {@link Listener}
     */
    default public Priority getProirity() {
        return Priority.NORMAL;
    }
}
