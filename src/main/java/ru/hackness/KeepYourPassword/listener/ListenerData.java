package ru.hackness.KeepYourPassword.listener;

import java.util.function.Consumer;

/**
 * Created by Hack
 * Date: 05.05.2017 17:29
 */
public class ListenerData<T extends Listener> {
    private T listener;
    private Consumer<T> onDoneAction;

    public ListenerData(T listener, Consumer<T> onDoneAction) {
        this.onDoneAction = onDoneAction;
        this.listener = listener;
    }

    public ListenerData() {

    }

    public void onListenerDone() {
        if (onDoneAction != null)
            onDoneAction.accept(listener);
    }
    public T getListener() {
        return listener;
    }
}
