package com.codelanx.pr.runemate.event;

import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;

public class ItemAddedEvent extends ItemEvent {

    public ItemAddedEvent(SpriteItem item, int change) {
        super(item, change);
    }

    @Override
    public Type getType() {
        return this.getQuantityChange() <= 0 ? Type.UNKNOWN : Type.ADDITION;
    }
}
