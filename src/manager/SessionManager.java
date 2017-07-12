package manager;

import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.Future;

/**
 * Created by Hack
 * Date: 12.07.2017 7:48
 */
public class SessionManager {
    private static SessionManager ourInstance = new SessionManager();
    private Future<?> logoutTask;
    private boolean shutdown = false;
    private Stage currentStage;
    private ChangeListener<? super Boolean> focusListener = ((observable, oldValue, newValue) -> {
        if (newValue)
            stopReopenTask();
        else
            startReopenTast();
    });

    public static SessionManager getInstance() {
        return ourInstance;
    }

    private SessionManager() {
    }

    public void startListening(Stage stage) {
        if (currentStage != null) {
            currentStage.focusedProperty().removeListener(focusListener);
            stopReopenTask();
        }
        currentStage = stage;
        if (stage != null)
            currentStage.focusedProperty().addListener(focusListener);
    }

    public void stopReopenTask() {
        if (logoutTask != null && !logoutTask.isCancelled())
            logoutTask.cancel(false);
    }

    public void startReopenTast() {
        if (shutdown || !Main.isAuthorised())
            return;
        stopReopenTask();
        logoutTask = ThreadPoolManager.getInstance().schedule(() -> {
            try {
                Runtime.getRuntime().exec("java -jar " +
                        new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 300000);
    }
}