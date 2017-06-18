package manager.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import manager.Main;
import manager.NodeType;
import manager.data.DataEntry;
import manager.data.DataManager;

import java.net.URL;
import java.util.*;

/**
 * Created by Hack
 * Date: 08.04.2017 3:36
 *
 * Controller of main window
 */
public class WindowMainController extends AbstractPaneController {
    @FXML private VBox vbox;
    @FXML private TextField srch;
    @FXML private Button addBtn;
    @FXML private ScrollPane scroll;
    private Map<DataEntry, Node> cache = new LinkedHashMap<>();

    public WindowMainController() {
        Main.setMainController(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateCache();
        srch.textProperty().addListener(((observable, oldValue, newValue) -> updateObs()));
        addBtn.setOnAction(event -> Main.showModal(NodeType.WINDOW_EDIT.getScene(new WindowEditController()), "Create"));
        vbox.heightProperty().addListener((observable, oldValue, newValue) -> scroll.setVvalue(newValue.doubleValue()));
    }

    private void addEntry(DataEntry data) {
        cache.put(data, NodeType.ELEMENT_MAIN.loadFXML(new ElementMainController(data)));
    }

    public void updateCache() {
        cache.clear();
        DataManager.getInstance().getDecryptedData().forEach(this::addEntry);
        updateObs();
    }

    private void updateObs() {
        clear();
        cache.entrySet().stream()
                .filter(entry -> srchMatch(entry.getKey().getLocation()))
                .forEach(entry -> add(entry.getValue()));
    }

    private boolean srchMatch(String value) {
        return value.toLowerCase().contains(srch.getText().toLowerCase());
    }

    @Override
    protected Pane getPane() {
        return vbox;
    }
}
