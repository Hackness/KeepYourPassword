package manager;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Hack
 * Date: 02.05.2017 2:14
 */
public class ThreadPoolManager {
    private static final ThreadPoolManager instance = new ThreadPoolManager();
    private ExecutorService executor = Executors.newFixedThreadPool(4);
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
        executor.shutdown();
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
}
