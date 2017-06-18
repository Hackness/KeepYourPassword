package manager.listener.impl;

import manager.NodeType;
import manager.controller.AbstractController;
import manager.listener.Listener;

/**
 * Created by Hack
 * Date: 05.05.2017 12:19
 */
public interface OnWindowLoaded extends Listener {
    void onAction(NodeType scene, AbstractController controller);
}
