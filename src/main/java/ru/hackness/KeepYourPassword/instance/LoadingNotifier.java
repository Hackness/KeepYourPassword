package ru.hackness.KeepYourPassword.instance;

import ru.hackness.KeepYourPassword.node.NodeType;
import ru.hackness.KeepYourPassword.controller.AbstractController;
import ru.hackness.KeepYourPassword.listener.Listeners;
import ru.hackness.KeepYourPassword.listener.impl.*;

/**
 * Created by Hack
 * Date: 05.05.2017 19:54
 */
public class LoadingNotifier {
    public static final LoadingNotifier instance = new LoadingNotifier();
    private long init;
    private long millis;


    public static LoadingNotifier getInstance() {
        return instance;
    }

    public void init() {
        Listeners.add(OnDataLoadStart.class, this::flush, true);
        Listeners.add(OnDataLoaded.class, () -> printAndFlush("Data file successfully loaded"), true);
        Listeners.add(OnPwdHashCheck.class, new OnPwdHashCheck() {
            @Override
            public void onAction(boolean result) {
                flushAll();
                if (result) {
                    System.out.println("========== Load Section ==========");
                    removeMe();
                }
                printAndFlush("Password hash checked. Result: " + result + ", time");
            }
        });
        Listeners.add(OnDataInitialized.class, () -> printAndFlush("DataManager initialized"), true);
        Listeners.add(OnWindowLoaded.class, new OnWindowLoaded() {
            @Override
            public void onAction(NodeType scene, AbstractController controller) {
                if (scene != NodeType.WINDOW_MAIN)
                    return;
                printAndFlush("Main scene loaded");
                printFinally();
                removeMe();
            }
        });
    }

    private void flush() {
        millis = System.currentTimeMillis();
    }

    private void flushAll() {
        init = System.currentTimeMillis();
        millis = System.currentTimeMillis();
    }

    private void printAndFlush(String name) {
        System.out.println(name + ": " + (System.currentTimeMillis() - millis));
        flush();
    }

    private void printFinally() {
        System.out.println("Finally: " + (System.currentTimeMillis() - init));
        System.out.println("========== Load End ==========");
    }
}
