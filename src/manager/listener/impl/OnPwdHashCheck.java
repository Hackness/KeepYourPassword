package manager.listener.impl;

import manager.listener.Listener;

/**
 * Created by Hack
 * Date: 05.05.2017 11:20
 */
public interface OnPwdHashCheck extends Listener {
    void onAction(boolean result);
}
