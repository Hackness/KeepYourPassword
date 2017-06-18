package manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import manager.controller.AbstractController;
import manager.listener.Listeners;
import manager.listener.impl.OnWindowLoaded;

import java.io.IOException;

/**
 * Created by Hack
 * Date: 08.04.2017 0:29
 *
 * Enum of all allowed scenes. Scene name should be equal with enum name (case ignoring)
 */
public enum NodeType {
    WINDOW_LOGIN(180, 119),
    WINDOW_MAIN(630, 390),
    ELEMENT_MAIN(),
    WINDOW_EDIT(300, 250),
    ELEMENT_EDIT(),
    ELEMENT_EDIT_BTN(),
    WINDOW_DELETE_CONFIRM(245, 85),
    WINDOW_ERROR(245, 85),
    WINDOW_LOADING(245, 85),
    ;
    private int width;
    private int height;

    NodeType(int width, int height) {
        this.width = width;
        this.height = height;
    }

    NodeType() {}

    /**
     * Load new scene from fxml file
     * @param controller - controller of fxml
     * @return - created scene
     */
    public Scene getScene(AbstractController controller) {
        Scene scene = init(new Scene(loadFXML(controller), width, height));
        Listeners.onAction(OnWindowLoaded.class, l -> l.onAction(this, controller));
        return scene;
    }

    public Scene getScene() {
        return getScene(null);
    }

    public Scene init(Scene scene) {
        return scene;
    }

    /**
     * Load some fxml as Parent object
     * @param controller - controller of fxml
     * @return - created Parent
     */
    public Parent loadFXML(Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/" + name().toLowerCase() + ".fxml"));
            if (controller != null)
                loader.setController(controller);
            return (Parent) loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error while loading " + name().toLowerCase() + ".fxml!", e);
        }
    }
}
