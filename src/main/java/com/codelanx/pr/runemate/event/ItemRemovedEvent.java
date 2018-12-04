package com.codelanx.pr.runemate.event;

import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;

public class ItemRemovedEvent extends ItemEvent {

    public ItemRemovedEvent(SpriteItem item, int change) {
        super(item, change);
    }

    @Override
    public Type getType() {
        return Type.REMOVAL;
    }
}
