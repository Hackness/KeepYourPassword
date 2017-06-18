package manager.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import manager.NodeType;
import manager.Util;
import manager.data.DataEntry;
import manager.data.DataManager;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Hack
 * Date: 09.04.2017 13:18
 */
public class WindowEditController extends AbstractPaneController {
    @FXML private VBox vbox;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private ScrollPane scroll;
    private Map<ElementEditController, Node> addElements = new LinkedHashMap<>();
    private DataEntry entry;
    private final boolean isNewEntry;

    public WindowEditController() {
        entry = new DataEntry();
        isNewEntry = true;
    }

    public WindowEditController(DataEntry entry) {
        this.entry = entry;
        isNewEntry = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (isNewEntry) {
            add(NodeType.ELEMENT_EDIT, new ElementEditController(this, DataEntry.LOCATION));
            add(NodeType.ELEMENT_EDIT, new ElementEditController(this, DataEntry.LOGIN));
            add(NodeType.ELEMENT_EDIT, new ElementEditController(this, DataEntry.PASSWORD));
        } else {
            entry.forEach((name, value) -> add(NodeType.ELEMENT_EDIT, new ElementEditController(this, name, value)));
        }
        add(NodeType.ELEMENT_EDIT_BTN, new ElementEditBtnController(this));
        cancelBtn.setOnAction(event -> ((Stage) cancelBtn.getScene().getWindow()).close());
        saveBtn.setOnAction(event -> {
            if (!check())
                return;
            if (!stopEditing())
                return;
//            entry.clear();
            String loc = entry.getLocation();
            String log = entry.getLogin();
            addElements.keySet().forEach(c -> entry.put(c.getName().getText(), c.getValue().getText()));
            if (isNewEntry)
                DataManager.getInstance().add(entry);
            else
                DataManager.getInstance().editData(loc, log, e -> DataManager.getInstance().fullCopy(entry, e));
//                DataManager.getInstance().remove(entry.getLocation(), entry.getLogin())
//            DataManager.getInstance().editData(entry.getLocation(), entry.getLogin(), e -> e.setAsFullCopyBy(entry));
            Util.closeWindow(saveBtn);
        });
        vbox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scroll.setVvalue(newValue.doubleValue());
            }
        });
    }

    public void add(Node node, ElementEditController controller) {
        ObservableList<Node> list = getList();
        Node addBtn = list.get(list.size() - 1);
        list.remove(addBtn);
        list.addAll(node, addBtn);
        addElements.put(controller, node);
    }

    private void add(NodeType nodeType, ElementEditController controller) {
        Parent node = nodeType.loadFXML(controller);
        addElements.put(controller, node);
        super.add(node);
    }

    public void remove(ElementEditController controller) {
        if (controller.isElementEditing())
            controller.stopElementEditing();
        super.remove(addElements.get(controller));
        addElements.remove(controller);
    }

    @Override
    protected Pane getPane() {
        return vbox;
    }

    public boolean isEditing() {
        return addElements.keySet().stream().anyMatch(ElementEditController::isElementEditing);
    }

    private boolean check() {
        return addElements.keySet().stream().allMatch(ElementEditController::check);
    }

    public boolean stopEditing() {
        return addElements.keySet().stream().allMatch(ElementEditController::stopElementEditing);
    }
}
