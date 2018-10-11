package ru.hackness.KeepYourPassword;

import ru.hackness.KeepYourPassword.controller.WindowLoadingController;
import ru.hackness.KeepYourPassword.data.DataManager;
import ru.hackness.KeepYourPassword.listener.impl.OnDataLoaded;
import ru.hackness.KeepYourPassword.instance.LoadingNotifier;
import ru.hackness.KeepYourPassword.node.NodeType;
import ru.hackness.KeepYourPassword.properties.ConfigLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.hackness.KeepYourPassword.controller.WindowErrorController;
import ru.hackness.KeepYourPassword.listener.Listeners;
import ru.hackness.KeepYourPassword.properties.Properties;

//+ error messages
//+ delete confirm
//+ listeners
//+ loading
//+ threading with dependencies
//TODO: system tray
//TODO: password gen
//TODO: import settings
//TODO: options page
//TODO: slf4j integration
//TODO: url references into browser from location field
//TODO: global refactor
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        assert setDebugMode();
        DataManager.logging();
        LoadingNotifier.getInstance().init();
        Properties.PRIMARY_STAGE_REF = primaryStage;
        Listeners.add(OnDataLoaded.class, () -> Properties.DATA_LOADED = true, true);
        ThreadPoolManager.getInstance().dependentExecute(() -> DataManager.getInstance().load(), "dataLoad");
        ThreadPoolManager.getInstance().dependentExecute(() -> ConfigLoader.getInstance().load(), "configLoad");
        primaryStage.setResizable(false);
        primaryStage.setTitle("KeepYourPassword");
        showScene(NodeType.WINDOW_LOGIN.getScene());
//        Pane p = new GridPane();
//        p.getChildren().add(new Label("Location"));
//        p.getChildren().add(new TextField());
//        p.getChildren().add(new TextField());
//        showScene(new Scene(p, 500, 500));

    }

    @Override
    public void stop() throws Exception {
        ThreadPoolManager.getInstance().shutdown();
        super.stop();
    }

    /**
     * show some scene in primary stage
     */
    public static void showScene(Scene scene) {
        Stage stage = Properties.PRIMARY_STAGE_REF;
        stage.close();
        stage.setScene(scene);
        stage.show();
        SessionManager.getInstance().startListening(stage);
    }

    /**
     * show some modal scene in additional stage
     * @param wait - if true primary stage will be sleeping
     */
    public static void showModal(Scene scene, String title, boolean wait) {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.initOwner(Properties.PRIMARY_STAGE_REF);
        stage.initModality(Modality.WINDOW_MODAL);
        SessionManager.getInstance().startListening(stage);
        stage.setOnCloseRequest(e -> SessionManager.getInstance().startListening(Properties.PRIMARY_STAGE_REF));
        if (wait)
            stage.showAndWait();
        else
            stage.show();
    }

    /**
     * This method will create loading scene in new stage, show it to the user, and then return the object of loading
     * @return - created loading object
     */
    public static WindowLoadingController createAndShowLoading() {
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setTitle("Loading");
        WindowLoadingController controller = new WindowLoadingController();
        stage.setScene(NodeType.WINDOW_LOADING.getScene(controller));
        stage.show();
        return controller;
    }

    /**
     * Show some String error in special window
     * @param desc - description of error
     */
    public static void showError(String desc) {
        System.out.println(desc);
        showModal(NodeType.WINDOW_ERROR.getScene(new WindowErrorController(desc)), "Error");
    }

    public static void showModal(Scene scene, String title) {
        showModal(scene, title, true);
    }

    private static boolean setDebugMode() {
        return (Properties.DEBUG_MODE = true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
