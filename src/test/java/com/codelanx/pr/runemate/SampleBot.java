package com.codelanx.pr.runemate;

import com.codelanx.pr.runemate.event.CustomChatEvent;
import com.codelanx.pr.runemate.patch.DummyEventDispatcher;
import com.runemate.game.api.script.framework.core.EventDispatcher;
import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class SampleBot extends TreeBot {

    private final EventBus bus;
    private final EventDispatcher dispatcher;

    public static void main(String... args) {
        SampleBot bot = new SampleBot();
        bot._getEventBus().listen(CustomChatEvent.class, event -> System.out.println("chat event: " + event.getMessage()));
        CustomChatEvent event = new CustomChatEvent("yolo");
        bot._getEventBus().fire(event);
        bot._getEventDispatcher().process(event);
    }

    public SampleBot() {
        this.bus = new RMEventBus();
        this.dispatcher = new DummyEventDispatcher(this, this.bus);
    }

    @Override
    public TreeTask createRootTask() {
        return null; //no need
    }

    public EventDispatcher _getEventDispatcher() {
        return this.dispatcher;
    }

    public EventBus _getEventBus() {
        return this.bus;
    }
}
