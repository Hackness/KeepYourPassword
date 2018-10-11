package ru.hackness.KeepYourPassword.controller;

import ru.hackness.KeepYourPassword.Main;
import ru.hackness.KeepYourPassword.node.NodeType;
import ru.hackness.KeepYourPassword.ThreadPoolManager;
import ru.hackness.KeepYourPassword.data.DataHolder;
import ru.hackness.KeepYourPassword.data.DataManager;
import ru.hackness.KeepYourPassword.listener.Listener;
import ru.hackness.KeepYourPassword.listener.Listeners;
import ru.hackness.KeepYourPassword.listener.impl.OnDataInitialized;
import ru.hackness.KeepYourPassword.listener.impl.OnDataLoaded;
import ru.hackness.KeepYourPassword.listener.impl.OnWindowLoaded;
import ru.hackness.KeepYourPassword.properties.Properties;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Created by Hack
 * Date: ?
 *
 * Controller of login window. Contains access check and loading processes
 */
public class WindowLoginController extends AbstractController {
    @FXML private PasswordField pwdFld;
    @FXML private Button loginBtn;
    @FXML private Button exitBtn;
    private EventHandler<ActionEvent> pwdMatchAction = event -> {
        WindowLoadingController loading = Main.createAndShowLoading();
        List<Listener> listeners = new ArrayList<>();
        loading.addProgress(10);
        if (Properties.DATA_LOADED)
            loading.addProgress(30);
        else
            //FIXME: can be casted meanwhile data already loaded
            listeners.add(Listeners.add(OnDataLoaded.class, () -> loading.addProgress(30), true));
        listeners.add(Listeners.add(OnDataInitialized.class, () -> loading.addProgress(20), true));
        listeners.add(Listeners.add(OnWindowLoaded.class, new OnWindowLoaded() {
            @Override
            public void onAction(NodeType scene, AbstractController controller) {
                if (scene == NodeType.WINDOW_MAIN) {
                    loading.addProgress(40);
                    loading.close();
                    Listeners.remove(this);
                }
            }
        }));

        ThreadPoolManager.getInstance().dependentExecute(() -> {
            DataHolder holder = DataManager.getInstance().getHolder();
            if (holder == null || holder.hashCheck(pwdFld.getText())) {
                Properties.LOGIN_PASSWORD = pwdFld.getText();
                DataManager.getInstance().init();
                Platform.runLater(() -> Main.showScene(NodeType.WINDOW_MAIN.getScene()));
                Properties.AUTHORIZED = true;
            } else {
                Platform.runLater(() -> {
                    Main.showScene(NodeType.WINDOW_LOGIN.getScene());
                    loading.close();
                    listeners.forEach(Listener::removeMe);
                });
            }
        }, "authorization", "dataLoad", "configLoad");
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exitBtn.setOnAction(event -> ((Stage) exitBtn.getScene().getWindow()).close());
        loginBtn.setOnAction(pwdMatchAction);
        pwdFld.setOnAction(pwdMatchAction);
    }
}
