package manager.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import manager.Main;
import manager.NodeType;
import manager.ThreadPoolManager;
import manager.data.DataHolder;
import manager.data.DataManager;
import manager.listener.Listener;
import manager.listener.Listeners;
import manager.listener.impl.OnDataInitialized;
import manager.listener.impl.OnDataLoaded;
import manager.listener.impl.OnWindowLoaded;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WindowLoginController extends AbstractController {
    @FXML private PasswordField pwdFld;
    @FXML private Button loginBtn;
    @FXML private Button exitBtn;
    private EventHandler<ActionEvent> pwdMatchAction = event -> {
        WindowLoadingController loading = Main.showLoading();
        List<Listener> listeners = new ArrayList<>();
        loading.addProgress(10);
        if (Main.isDataLoaded())
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
                Main.loginPassword = pwdFld.getText();
                DataManager.getInstance().init();
                Platform.runLater(() -> Main.showScene(NodeType.WINDOW_MAIN.getScene()));
            } else {
                Platform.runLater(() -> {
                    Main.showScene(NodeType.WINDOW_LOGIN.getScene());
                    loading.close();
                    listeners.forEach(Listener::removeMe);
                });
            }
        }, "", "dataLoad");
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exitBtn.setOnAction(event -> ((Stage) exitBtn.getScene().getWindow()).close());
        loginBtn.setOnAction(pwdMatchAction);
        pwdFld.setOnAction(pwdMatchAction);
    }
}
