package com.codelanx.pr.runemate;

import com.codelanx.pr.runemate.event.CustomChatEvent;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.Varbit;
import com.runemate.game.api.hybrid.local.Varc;
import com.runemate.game.api.hybrid.local.Varp;
import com.runemate.game.api.hybrid.net.GrandExchange.Slot;
import com.runemate.game.api.script.framework.listeners.events.AnimationEvent;
import com.runemate.game.api.script.framework.listeners.events.EngineEvent;
import com.runemate.game.api.script.framework.listeners.events.Event;
import com.runemate.game.api.script.framework.listeners.events.GrandExchangeEvent;
import com.runemate.game.api.script.framework.listeners.events.HitsplatEvent;
import com.runemate.game.api.script.framework.listeners.events.MenuInteractionEvent;
import com.runemate.game.api.script.framework.listeners.events.MessageEvent;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.listeners.events.SkillEvent;
import com.runemate.game.api.script.framework.listeners.events.SkillEvent.Type;
import com.runemate.game.api.script.framework.listeners.events.VarbitEvent;
import com.runemate.game.api.script.framework.listeners.events.VarcEvent;
import com.runemate.game.api.script.framework.listeners.events.VarpEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class Test {

    private static final EventBus BUS = new RMEventBus();
    private static final Supplier<Event>[] RANDOM_EVENTS = new Supplier[] {
            () -> new AnimationEvent(null, ThreadLocalRandom.current().nextInt(42000)),
            () -> new EngineEvent(),
            () -> new GrandExchangeEvent(new Slot(ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextInt(32), null), GrandExchangeEvent.Type.OFFER_CREATED),
            () -> new HitsplatEvent(null, null),
            () -> new MenuInteractionEvent(0, 0, 0, 0, "click", "butts", 69, 69),
            () -> new MoneyPouchEvent(ThreadLocalRandom.current().nextInt(1338), ThreadLocalRandom.current().nextInt(42)),
            () -> new SkillEvent(Skill.INVENTION, Type.EXPERIENCE_GAINED, ThreadLocalRandom.current().nextInt(70) + 1337, 1337),
            () -> new VarpEvent(new Varp(1337), 0, 1),
            () -> new VarcEvent(new Varc(69), 0, 1),
            () -> new VarbitEvent(new Varbit(69, 0, 9001, 4, 2), 0, 1),
            () -> new MessageEvent(0, "ian", ThreadLocalRandom.current().nextBoolean() ? "cache" : "kkona")
    };

    public static void main(String... args) {
        int runs = 1;
        System.out.println("Time for " + runs + " runs: " + fireAndReturn(runs) + "ns");
    }

    private static long fireAndReturn(int runs) {
        BUS.listen(CustomChatEvent.class, Priority.LOWEST,  event -> {
            event.setMessage(event.getMessage().replace("banana", "boat"));
        });
        BUS.listen(CustomChatEvent.class, Priority.LOWEST,  event -> {
            event.setMessage("banana");
        });
        BUS.listen(CustomChatEvent.class, Priority.HIGHEST,  event -> {
            event.setMessage(event.getMessage().replace("banana", "phone"));
        });
        return fire(runs, () -> {
            CompletableFuture<CustomChatEvent> fired = BUS.fire(new CustomChatEvent("banana"));
            fired.whenComplete((event, ex) -> {
                String result = event.getMessage();
                System.out.println("Sending chat message: " + result);
            });
        });
    }

    private static long fireModifiers(int runs) {
        BUS.listen(CustomChatEvent.class, Priority.MONITOR,  event -> {
            System.out.println("Event outcome: " + event.getMessage());
        });
        BUS.listen(CustomChatEvent.class, Priority.LOWEST,  event -> {
            event.setMessage(event.getMessage().replace("banana", "boat"));
        });
        BUS.listen(CustomChatEvent.class, Priority.LOWEST,  event -> {
            event.setMessage(event.getMessage().replace("banana", "butt"));
        });
        BUS.listen(CustomChatEvent.class, Priority.HIGHEST,  event -> {
            event.setMessage(event.getMessage().replace("banana", "phone"));
        });
        return fire(runs, () -> BUS.fire(new CustomChatEvent("banana")));
    }

    private static long fireRandom(int runs) {
        BUS.listen(AnimationEvent.class, event -> printf("Actor event (actor: %s, id: %d)\n", event.getSource(), event.getAnimationId()));
        BUS.listen(EngineEvent.class, event -> printf("Engine event (event: %s)\n", event.toString()));
        BUS.listen(GrandExchangeEvent.class, event -> printf("GE event (type: %s, slot: %s)\n", event.getType().name(), event.getSlot().toString()));
        BUS.listen(HitsplatEvent.class, event -> printf("Hitsplat event (source: %s, hitsplat: %s)\n", event.getSource(), event.getHitsplat()));
        BUS.listen(MenuInteractionEvent.class, event -> printf("MenuInteraction event (event: %s)\n", event.toString()));
        BUS.listen(MoneyPouchEvent.class, event -> printf("MoneyPuch event (event: %s)\n", event.toString()));
        BUS.listen(SkillEvent.class, event -> printf("Skill event (event: %s)\n", event.toString()));
        BUS.listen(VarpEvent.class, event -> printf("Varp event (event: %s)\n", event.toString()));
        BUS.listen(VarcEvent.class, event -> printf("Varc event (event: %s)\n", event.toString()));
        BUS.listen(VarbitEvent.class, event -> printf("Varbit event (event: %s)\n", event.toString()));
        BUS.listen(MessageEvent.class, event -> printf("Message event (event: %s)\n", event.toString()));
        return fire(runs, () -> BUS.fire(RANDOM_EVENTS[ThreadLocalRandom.current().nextInt(RANDOM_EVENTS.length)].get()));
    }

    private static long fire(int runs, Runnable operation) {
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            operation.run();
        }
        return System.nanoTime() - start;
    }
    
    private static void printf(String format, Object... out) {
        //System.out.printf(format, out);
    }
}
