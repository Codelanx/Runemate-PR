package com.codelanx.pr.runemate;

import com.codelanx.pr.runemate.event.ItemEventHandler;
import com.runemate.game.api.script.framework.listeners.AnimationListener;
import com.runemate.game.api.script.framework.listeners.ChatboxListener;
import com.runemate.game.api.script.framework.listeners.EngineListener;
import com.runemate.game.api.script.framework.listeners.GrandExchangeListener;
import com.runemate.game.api.script.framework.listeners.HitsplatListener;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.MenuInteractionListener;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.SkillListener;
import com.runemate.game.api.script.framework.listeners.VarbitListener;
import com.runemate.game.api.script.framework.listeners.VarpListener;
import com.runemate.game.api.script.framework.listeners.events.AnimationEvent;
import com.runemate.game.api.script.framework.listeners.events.EngineEvent;
import com.runemate.game.api.script.framework.listeners.events.Event;
import com.runemate.game.api.script.framework.listeners.events.GrandExchangeEvent;
import com.runemate.game.api.script.framework.listeners.events.HitsplatEvent;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;
import com.runemate.game.api.script.framework.listeners.events.MenuInteractionEvent;
import com.runemate.game.api.script.framework.listeners.events.MessageEvent;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.listeners.events.SkillEvent;
import com.runemate.game.api.script.framework.listeners.events.VarbitEvent;
import com.runemate.game.api.script.framework.listeners.events.VarpEvent;

import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * Represents an object which can receive {@link Event events} and fire them to {@link Listener listeners}.
 *
 * @author 1Rogue
 */
public interface EventBus {


    public <T extends Event> RegisteredListener<T> listen(Class<? extends T> type, Priority priority, Listener<? super T> onEvent);

    default public <T extends Event> RegisteredListener<T> listen(Class<? extends T> type, Listener<? super T> onEvent) {
        return this.listen(type, Priority.NORMAL, onEvent);
    }

    default public RegisteredListener<?> listen(EventListener oldListener) {
        if (oldListener instanceof InventoryListener) {
            return this.listen(ItemEvent.class, new ItemEventHandler((InventoryListener) oldListener));
        }
        Class<? extends Event> clazz = RMCompat.mapOldListener(oldListener);
        Listener<? super Event> listen = RMCompat.makeListener(oldListener);
        if (clazz == null || listen == null) {
            throw new IllegalArgumentException("No appropriate type found for EventListener, did you want a different method? (class: " + clazz + " listener: " + listen + ")");
        }
        RegisteredListener<?> back = this.listen(clazz, listen);
        RMCompat.noteOldListener(oldListener, back);
        return back;
    }

    public boolean cancel(RegisteredListener<?> listener);

    @Deprecated
    default boolean cancel(EventListener listener) {
        return this.cancel(RMCompat.removeOldListener(listener));
    }

    default public Collection<EventListener> getOldListeners() {
        return RMCompat.OLD_REGISTERED.keySet();
    }

    /**
     * Fires an event to any active listeners
     *
     * @param event The event to fire
     * @param <T> Any subclass of {@link Event}
     * @return A {@link CompletableFuture CompletableFuture<T>}
     */
    //with the public nature of eventbus, we could expose api in a way which separates firing and listening to two
    //distinct interfaces, making it so you reference those kinds of methods in different locations so as not to
    //confuse newer authors of the api
    public <T extends Event> CompletableFuture<T> fire(T event);

    //Additionally we can make methods we don't want public not exposed

    /**
     * Stops firing events to any active listeners
     */
    public void stop();

    /**
     * Resumes firing events to any active listeners (default state)
     */
    public void resume();

    /**
     * Whether or not the event bus is firing events to active listeners
     *
     * @return {@code true} if firing events, {@code false} otherwise
     */
    public boolean isRunning();

    /**
     * Removes any active listeners from the event bus
     *
     * @return A {@link List List<RegisteredListener<?>>} of the removed listeners
     */
    public List<RegisteredListener<?>> clear();


    public static class RMCompat {
        private static final Map<Class<? extends EventListener>, Class<? extends Event>> OLD_LISTENERS = new HashMap<>();
        private static final Map<Class<? extends EventListener>, BiConsumer<? super EventListener, ? super Event>> OLD_METHODS = new HashMap<>();
        private static final Map<EventListener, RegisteredListener<?>> OLD_REGISTERED = new HashMap<>();

        static {
            register(AnimationListener.class, AnimationEvent.class, AnimationListener::onAnimationChanged);
            register(EngineListener.class, EngineEvent.class, EngineListener::onCycleStart);
            register(GrandExchangeListener.class, GrandExchangeEvent.class, GrandExchangeListener::onSlotUpdated);
            register(HitsplatListener.class, HitsplatEvent.class, HitsplatListener::onHitsplatAdded);
            register(MenuInteractionListener.class, MenuInteractionEvent.class, MenuInteractionListener::onInteraction);
            register(MoneyPouchListener.class, MoneyPouchEvent.class, MoneyPouchListener::onContentsChanged);
            register(SkillListener.class, SkillEvent.class, SkillListener::onExperienceGained);
            register(VarbitListener.class, VarbitEvent.class, VarbitListener::onValueChanged);
            register(VarpListener.class, VarpEvent.class, VarpListener::onValueChanged);
            register(ChatboxListener.class, MessageEvent.class, ChatboxListener::onMessageReceived);
            //Warning: this handles two events
            //register(InventoryListener.class, ItemEvent.class, null);
            //not handled in api
            //register(null.class, VarcEvent.class);
        }

        private static <L extends EventListener, T extends Event> void register(Class<L> oldListener, Class<T> event, BiConsumer<L, T> onEvent) {
            OLD_LISTENERS.put(oldListener, event);
            OLD_METHODS.put(oldListener, (BiConsumer<? super EventListener, ? super Event>) (BiConsumer<?, ?>) onEvent);
        }

        private static <L extends EventListener, T extends Event> void register(Class<L> oldListener, Class<T> event, Consumer<L> onEvent) {
            register(oldListener, event, (list, evnt) -> {
                onEvent.accept(list);
            });
        }

        static Class<? extends Event> mapOldListener(EventListener old) {
            return mapOldListener(old.getClass());
        }

        static Class<? extends Event> mapOldListener(Class<? extends EventListener> old) {
            return OLD_LISTENERS.get(old);
        }

        static Listener<? super Event> makeListener(EventListener old) {
            BiConsumer<? super EventListener, ? super Event> input = OLD_METHODS.get(old.getClass());
            return input == null ? null : event -> input.accept(old, event);
        }

        static void noteOldListener(EventListener old, RegisteredListener<?> active) {
            OLD_REGISTERED.put(old, active);
        }

        static RegisteredListener<?> getActiveOldListener(EventListener old) {
            return OLD_REGISTERED.get(old);
        }

        static RegisteredListener<?> removeOldListener(EventListener old) {
            return OLD_REGISTERED.remove(old);
        }

        static void onClear() {
            OLD_REGISTERED.clear();
        }

    }

}
