package com.codelanx.pr.runemate.event;

import com.codelanx.pr.runemate.Listener;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;

public class ItemEventHandler implements Listener<ItemEvent> {

    private final InventoryListener listener;

    public ItemEventHandler(InventoryListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(ItemEvent event) {
        switch (event.getType()) {
            case ADDITION:
                this.listener.onItemAdded(event);
                break;
            case REMOVAL:
                this.listener.onItemRemoved(event);
        }
    }

    public static ItemEvent toItemEvent(SpriteItem item, int change) {
        return change >= 0 ? new ItemAddedEvent(item, change) : new ItemRemovedEvent(item, change);
    }
}
