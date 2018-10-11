package ru.hackness.KeepYourPassword.listener.impl;

import ru.hackness.KeepYourPassword.listener.Listener;

/**
 * Created by Hack
 * Date: 05.05.2017 8:11
 */
@FunctionalInterface
public interface OnDataLoaded extends Listener {
    void onAction();
}
