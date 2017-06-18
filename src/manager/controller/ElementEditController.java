package manager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import manager.data.DataEntry;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Hack
 * Date: 09.04.2017 13:27
 */
public class ElementEditController extends AbstractController {
    @FXML private Label name;
    @FXML private TextField value;
    @FXML private Button deleteBtn;
    @FXML private TextField editLoc;
    @FXML private TextField fakeEditLoc;
    private WindowEditController basicController;
    private boolean editable = true;
    private boolean newField = true;
    private String initName = "FieldName";
    private String prompt = "Value";
    private String initValue = "";

    public ElementEditController(WindowEditController controller) {
        basicController = controller;
    }

    public ElementEditController(WindowEditController controller, String name) {
        switch (name) {
            case DataEntry.LOCATION:
            case DataEntry.LOGIN:
            case DataEntry.PASSWORD:
                editable = false;
                prompt = "Required Value";
        }
        this.basicController = controller;
        initName = name;
    }

    public ElementEditController(WindowEditController controller, String name, String value) {
        this(controller, name);
        initValue = value;
        newField = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setText(initName);
        value.setText(initValue);
        value.setPromptText(prompt);
        deleteBtn.setVisible(editable);
        if (deleteBtn.isVisible())
            deleteBtn.setOnAction(event -> basicController.remove(this));
        if (editable) {
            name.setOnMouseClicked(event -> setEditVisible(true, true));
            name.setOnMouseEntered(event -> fakeEditLoc.setVisible(true));
            name.setOnMouseExited(event -> fakeEditLoc.setVisible(false));
            name.setOnMouseClicked(event -> {
                fakeEditLoc.setVisible(false);
                setEditVisible(true, true);
            });
            editLoc.setOnAction(event -> setEditVisible(false, true));
            value.setOnMouseClicked(event -> {
                if (isElementEditing())
                    setEditVisible(false, false);
            });
            if (newField)
                setEditVisible(true, true);
        }
    }

    private void setEditVisible(boolean visible, boolean focus) {
        if (visible) {
            basicController.stopEditing();
            name.setVisible(false);
            editLoc.setVisible(true);
            if (focus)
                editLoc.requestFocus();
        } else {
            if (editLoc.getText().isEmpty()) {
                setEditVisible(true, true);
                return;
            }
            name.setText(editLoc.getText());
            editLoc.setVisible(false);
            name.setVisible(true);
            if (focus)
                value.requestFocus();
        }
    }

    public Label getName() {
        return name;
    }

    public TextField getValue() {
        return value;
    }

    public boolean isElementEditing() {
        return editLoc.isVisible();
    }

    public boolean check() {
        if (name.getText().isEmpty()) {
            setEditVisible(true, true);
            return false;
        }
        if (isElementEditing() && editLoc.getText().isEmpty()) {
            editLoc.requestFocus();
            return false;
        }
        if (value.getText().isEmpty()) {
            value.requestFocus();
            return false;
        }
        return true;
    }

    public boolean stopElementEditing() {
        if (isElementEditing()) {
            if (!check())
                return false;
            setEditVisible(false, false);
        }
        return true;
    }
}
