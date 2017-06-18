package manager.listener;

/**
 * Created by Hack
 * Date: 05.05.2017 8:12
 *
 * This is the superclass of any listener.
 */
public interface Listener {
    /**
     * Override this method if you want to use simple listener notifier with class param only.
     * @see Listeners .onAction(Class<T> listenerClass);
     */
    default void onAction() {

    }

    default void removeMe() {
        Listeners.remove(this);
    }
}
