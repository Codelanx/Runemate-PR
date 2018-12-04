package com.codelanx.pr.runemate.patch;

import com.codelanx.pr.runemate.EventBus;
import com.runemate.game.api.script.framework.AbstractBot;
import com.runemate.game.api.script.framework.core.EventDispatcher;
import com.runemate.game.api.script.framework.listeners.events.Event;

import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public class DummyEventDispatcher extends EventDispatcher {

    //can be replaced with a getter in AbstractBot, then constructor matches
    private final EventBus bus;

    public DummyEventDispatcher(AbstractBot bot, EventBus delegate) {
        super(bot);
        this.bus = delegate;
    }

    @Override
    public void addListener(EventListener listener) {
        this.bus.listen(listener);
    }

    //TODO
    @Override
    public void shutdown() {
        this.bus.stop();
        this.bus.clear();
    }

    //TODO
    @Override
    public List<EventListener> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public void process(Event event) {
        this.bus.fire(event);
    }

    @Override
    public boolean removeListener(EventListener listener) {
        return this.bus.cancel(listener);
    }
}
