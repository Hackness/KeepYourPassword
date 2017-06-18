package manager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import manager.Util;
import manager.data.DataManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Hack
 * Date: 03.05.2017 5:50
 */
public class WindowDeleteConfirmController extends AbstractController {
    @FXML private Button confirmBtn;
    @FXML private Button cancelBtn;
    @FXML private Label location;
    @FXML private Label login;
    private String sLoc;
    private String sLog;

    public WindowDeleteConfirmController(String sLoc, String sLog) {
        this.sLoc = sLoc;
        this.sLog = sLog;
    }

    @Override
    public void initialize(URL location0, ResourceBundle resources) {
        location.setText(sLoc);
        login.setText(sLog);
        confirmBtn.setOnAction(event -> {
            DataManager.getInstance().remove(sLoc, sLog);
            Util.closeWindow(confirmBtn);
        });
        cancelBtn.setOnAction(event -> Util.closeWindow(cancelBtn));
    }
}
