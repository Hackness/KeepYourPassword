package manager.controller;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import manager.NodeType;

/**
 * Created by Hack
 * Date: 14.04.2017 1:31
 */
public abstract class AbstractPaneController extends AbstractController {
    protected abstract Pane getPane();

    void add(Node node) {
        getPane().getChildren().add(node);
    }

    void add(NodeType nodeType, AbstractController controller) {
        getPane().getChildren().add(nodeType.loadFXML(controller));
    }

    void remove(Node node) {
        getPane().getChildren().remove(node);
    }

    int size() {
        return getPane().getChildren().size();
    }

    ObservableList<Node> getList() {
        return getPane().getChildren();
    }

    void clear() {
        getPane().getChildren().clear();
    }
}
