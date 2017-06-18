package manager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import manager.NodeType;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Hack
 * Date: 10.04.2017 2:03
 *
 * Controller of add button in edit window
 */
public class ElementEditBtnController extends AbstractController {
    @FXML private Button addBtn;
    private WindowEditController basicController;

    public ElementEditBtnController(WindowEditController basicController) {
        this.basicController = basicController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBtn.setOnAction(event -> {
            if (!basicController.stopEditing())
                return;
            ElementEditController controller = new ElementEditController(basicController);
            basicController.add(NodeType.ELEMENT_EDIT.loadFXML(controller), controller);
        });
    }
}
