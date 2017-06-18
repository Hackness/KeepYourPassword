package manager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import manager.Util;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Hack
 * Date: 03.05.2017 18:29
 *
 * Controller of error window
 */
public class WindowErrorController extends AbstractController {
    private String sDesc;
    @FXML private Label desc;
    @FXML private Button btn;

    public WindowErrorController(String sDesc) {
        this.sDesc = sDesc;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        desc.setText(sDesc);
        btn.setOnAction(event -> Util.closeWindow(desc));
    }
}
