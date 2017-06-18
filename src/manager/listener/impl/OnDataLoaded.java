package manager.listener.impl;

import manager.listener.Listener;

/**
 * Created by Hack
 * Date: 05.05.2017 8:11
 */
@FunctionalInterface
public interface OnDataLoaded extends Listener {
    void onAction();
}
