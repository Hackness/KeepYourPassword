package ru.hackness.KeepYourPassword.listener.impl;

import ru.hackness.KeepYourPassword.node.NodeType;
import ru.hackness.KeepYourPassword.controller.AbstractController;
import ru.hackness.KeepYourPassword.listener.Listener;

/**
 * Created by Hack
 * Date: 05.05.2017 12:19
 */
public interface OnWindowLoaded extends Listener {
    void onAction(NodeType scene, AbstractController controller);
}
