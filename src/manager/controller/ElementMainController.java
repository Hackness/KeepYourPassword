package manager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import manager.Main;
import manager.NodeType;
import manager.Util;
import manager.data.DataEntry;
import manager.data.DataManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Hack
 * Date: 08.04.2017 16:43
 */
public class ElementMainController extends AbstractController {
    private DataEntry data;
    @FXML private Label location;
    @FXML private TextField login;
    @FXML private Button showBtn;
    @FXML private Button deleteBtn;
    @FXML private Button loginC;
    @FXML private Button passwordC;

    public ElementMainController(DataEntry data) {
        this.data = data;
    }

    @Override
    public void initialize(URL location0, ResourceBundle resources) {
        location.setText(data.getLocation());
        login.setText(data.getLogin());
        loginC.setOnAction(event -> Util.copyToClipboard(data.getLogin()));
        passwordC.setOnAction(event -> Util.copyToClipboard(data.getPassword()));
        deleteBtn.setOnAction(event -> Main.showModal(NodeType.WINDOW_DELETE_CONFIRM.getScene(
                new WindowDeleteConfirmController(location.getText(), login.getText())), "Confirm"));
//        deleteBtn.setOnAction(event -> DataManager.getInstance().remove(location.getText(), login.getText()));
        showBtn.setOnAction(event -> Main.showModal(NodeType.WINDOW_EDIT.getScene(new WindowEditController(data)), "Show"));
    }
}
