package com.codelanx.pr.runemate.event;

import com.runemate.game.api.script.framework.listeners.events.Event;

import java.util.concurrent.ThreadLocalRandom;

public class CustomChatEvent implements Event {

    private String message;
    private int wpm;

    public CustomChatEvent(String message) {
        this.message = message;
        this.wpm = ThreadLocalRandom.current().nextInt(0, 50) + 75;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getWPM() {
        return this.wpm;
    }

    public void setWPM(int wpm) {
        this.wpm = wpm;
    }
}
