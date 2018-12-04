package com.codelanx.pr.runemate;

import com.runemate.game.api.script.framework.listeners.events.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * An event bus which can handle event calculations on a separate thread
 *
 * @author 1Rogue
 */
public class RMEventBus implements EventBus {

    private final Listeners listeners = new Listeners();
    private final AtomicBoolean paused = new AtomicBoolean(false);

    @Override
    public void stop() {
        this.paused.set(true);
    }

    @Override
    public void resume() {
        this.paused.set(false);
    }

    @Override
    public boolean isRunning() {
        return !this.paused.get();
    }

    @Override
    public List<RegisteredListener<?>> clear() {
        return this.listeners.clear();
    }

    @Override
    public <T extends Event> RegisteredListener<T> listen(Class<? extends T> type, Priority priority, Listener<? super T> listener) {
        return this.listeners.add(new RegisteredListener<>(this, type, priority, listener));
    }

    @Override
    public boolean cancel(RegisteredListener<?> listener) {
        return this.listeners.remove(listener);
    }

    @Override
    public <T extends Event> CompletableFuture<T> fire(T event) {
        if (this.paused.get()) {
            return CompletableFuture.completedFuture(event);
        }
        if (event instanceof CEvent && ((CEvent) event).isAsync()) {
            return CompletableFuture.supplyAsync(() -> this.fireSync(event));
        }
        return CompletableFuture.completedFuture(this.fireSync(event));
    }

    private <T extends Event> T fireSync(T event) {
        RegisteredListener[][] fire = this.listeners.getListeners(event.getClass());
        if (fire == null) {
            return event;
        }
        for (int p = 0; p < fire.length; p++) {
            if (fire[p] == null) {
                continue;
            }
            if (p == Priority.MONITOR.ordinal()) {
                for (int i = 0; i < fire[p].length; i++) {
                    fire[p][i].fire(event);
                }
            } else {
                for (int i = 0; i < fire[p].length; i++) {
                    event = (T) fire[p][i].fire(event);
                }
            }
        }
        return event;
    }

    private static class Listeners {

        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Map<Class<? extends Event>, RegisteredListener[][]> listeners = new HashMap<>();

        private <T extends Event> RegisteredListener[][] getListeners(Class<T> type) {
            return operateLock(this.lock.readLock(), () -> {
                RegisteredListener[][] current = this.listeners.get(type);
                if (current == null) {
                    return null;
                }
                RegisteredListener[][] back = new RegisteredListener[current.length][];
                System.arraycopy(current, 0, back, 0, back.length);
                for (int i = 0; i < back.length; i++) {
                    back[i] = this.copy(back[i], 0, true);
                }
                return back;
            });
        }

        private RegisteredListener[] copy(RegisteredListener[] old, int extend) {
            return this.copy(old, extend, false);
        }

        private RegisteredListener[] copy(RegisteredListener[] old, int extend, boolean keepNull) {
            if (old == null || old.length == 0) {
                if (extend == 0) {
                    return !keepNull && old == null ? new RegisteredListener[0] : old;
                }
                return new RegisteredListener[extend];
            }
            if (old.length + extend <= 0) {
                return new RegisteredListener[0];
            }
            RegisteredListener[] back = new RegisteredListener[old.length + extend];
            System.arraycopy(old, 0, back, 0, old.length);
            return back;
        }

        private <T extends Event> RegisteredListener<T> add(RegisteredListener<T> listener) {
            int i = listener.getProirity().ordinal();
            operateLock(this.lock.writeLock(), () -> {
                this.listeners.compute(listener.getType(), (key, old) -> {
                    if (old == null) {
                        old = new RegisteredListener[Priority.VALUES.length][];
                        //Arrays.fill(old, null); //TODO: necessary?
                    }
                    RegisteredListener[] arr = this.copy(old[i], 1);
                    arr[arr.length - 1] = listener;
                    old[i] = arr;
                    return old;
                });
            });
            return listener;
        }

        private boolean remove(RegisteredListener<?> listener) {
            AtomicBoolean changed = new AtomicBoolean(false);
            operateLock(this.lock.writeLock(), () -> {
                this.listeners.computeIfPresent(listener.getType(), (key, old) -> {
                    RegisteredListener[] curr = old[listener.getProirity().ordinal()];
                    if (curr == null || curr.length <= 0) {
                        return old;
                    }
                    int at = Arrays.binarySearch(curr, listener);
                    if (at < 0) {
                        return old;
                    }
                    RegisteredListener[] back = this.copy(curr, -1);
                    System.arraycopy(curr, at + 1, back, at, back.length - at);
                    old[listener.getProirity().ordinal()] = back;
                    changed.set(true);
                    return old;
                });
            });
            return changed.get();
        }

        private List<RegisteredListener<?>> clear() {
            List<RegisteredListener[][]> wiped = operateLock(this.lock.writeLock(), () -> {
                Collection<RegisteredListener[][]> vals = this.listeners.values();
                List<RegisteredListener[][]> back = new ArrayList<>(vals);
                vals.clear();
                return back;
            });
            return wiped.stream().filter(Objects::nonNull).flatMap(Arrays::stream).filter(Objects::nonNull).flatMap(Arrays::stream).collect(Collectors.toList());
        }

    }


    //Copied from CodelanxCommons TODO code out


    /**
     * Performs an operation with a lock, saving room by not requiring a lot of
     * {@code try-finally} blocks
     *
     * @since 0.2.0
     * @version 0.2.0
     *
     * @param lock The {@link Lock} to utilize
     * @param operation The code to be run
     */
    private static void operateLock(Lock lock, Runnable operation) {
        lock.lock();
        try {
            operation.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Performs an operation with a lock, saving room by not requiring a lot of
     * {@code try-finally} blocks
     *
     * @since 0.2.0
     * @version 0.2.0
     *
     * @param <R> The return type of the {@link Supplier}
     * @param lock      The {@link Lock} to utilize
     * @param operation The code to be run
     * @return A value returned from the inner {@link Supplier}
     */
    private static <R> R operateLock(Lock lock, Supplier<R> operation) {
        lock.lock();
        try {
            return operation.get();
        } finally {
            lock.unlock();
        }
    }
}
