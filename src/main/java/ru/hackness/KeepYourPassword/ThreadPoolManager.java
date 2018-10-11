package ru.hackness.KeepYourPassword;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Hack
 * Date: 02.05.2017 2:14
 */
public class ThreadPoolManager {
    private static final ThreadPoolManager instance = new ThreadPoolManager();
    private ExecutorService executor = Executors.newFixedThreadPool(getThreadNumber());
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(getThreadNumber());
    private Map<String, Future<?>> dependencies = new ConcurrentHashMap<>();

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    public void execute(Runnable r) {
        executor.execute(r);
    }

    public Future<?> submit(Runnable r) {
        return executor.submit(r);
    }

    public void shutdown() {
        executor.shutdownNow();
        scheduledExecutor.shutdownNow();
    }

    public ScheduledFuture<?> schedule(Runnable r, long millisDelay) {
        return scheduledExecutor.schedule(r, millisDelay, TimeUnit.MILLISECONDS);
    }

    public void dependentExecute(Runnable r, String myNameAsDependency, String ... myDependencies) {
        for (String dependName : myDependencies) {
            Future<?> future = dependencies.get(dependName);
            if (future != null)
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
        }
        if (myNameAsDependency != null && !myNameAsDependency.isEmpty())
            dependencies.put(myNameAsDependency, executor.submit(r));
        else
            executor.execute(r);
    }

    private int getThreadNumber() {
        return Math.min(4, Math.max(1, Runtime.getRuntime().availableProcessors()));
    }
}
