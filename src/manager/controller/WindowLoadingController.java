package manager.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import manager.ThreadPoolManager;
import manager.Util;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Hack
 * Date: 05.05.2017 6:30
 */
public class WindowLoadingController extends AbstractController {
    @FXML private ProgressBar bar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        bar.progressProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
    }

    public void addProgress(int count) {
        ThreadPoolManager.getInstance().execute(() -> {
            for (int i = 0; i < count; i++) {
                Platform.runLater(() -> {
                    bar.setProgress(bar.getProgress() + 0.01);
                    if (bar.getProgress() >= 1)
                        Util.closeWindow(bar);
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void close() {
        Util.closeWindow(bar);
    }
}
