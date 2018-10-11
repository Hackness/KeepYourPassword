package ru.hackness.KeepYourPassword.listener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by Hack
 * Date: 05.05.2017 8:10
 */
public class Listeners {
    private static final Map<Class<? extends Listener>, Map<Listener, ListenerData>> listeners = new HashMap<>();

    public static <T extends Listener> T add(Class<T> listenerClass, T listener, ListenerData data) {
        listeners.computeIfAbsent(listenerClass, key -> new ConcurrentHashMap<>());
        listeners.get(listenerClass).put(listener, data);
        return listener;
    }

    public static <T extends Listener> T add(Class<T> listenerClass, T listener, Consumer<T> onDoneAction) {
        return add(listenerClass, listener, new ListenerData<>(listener, onDoneAction));
    }

    public static <T extends Listener> T add(Class<T> listenerClass, T listener, boolean removeOnDone) {
        return add(listenerClass, listener, new ListenerData<>(listener, removeOnDone ? Listener::removeMe : null));
    }

    public static <T extends Listener> T add(Class<T> listenerClass, T listener) {
        return add(listenerClass, listener, new ListenerData());
    }

    public static <T extends Listener> void onAction(Class<T> listenerClass, Consumer<T> func) {
        Optional.ofNullable(listeners.get(listenerClass)).ifPresent(map -> map.forEach((listener, data) -> {
            func.accept(listenerClass.cast(listener));
//            Optional.ofNullable(data).ifPresent(ListenerData::onListenerDone);
            data.onListenerDone();
        }));
    }

    public static <T extends Listener> void onAction(Class<T> listenerClass) {
        Optional.ofNullable(listeners.get(listenerClass)).ifPresent(map -> map.forEach((listener, data) -> {
            listener.onAction();
//            Optional.ofNullable(data).ifPresent(ListenerData::onListenerDone);
            data.onListenerDone();
        }));
    }

    public static <T extends Listener> void remove(T listener) {
        listeners.values().forEach(l -> l.remove(listener));
    }
}
