package manager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import manager.controller.WindowErrorController;
import manager.controller.WindowLoadingController;
import manager.controller.WindowMainController;
import manager.data.DataManager;
import manager.listener.Listeners;
import manager.listener.impl.OnDataLoaded;
import manager.model.instance.LoadingNotifier;

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
public class Main extends Application {
    private static Stage primaryStage;
    public static String loginPassword = "";
    private static WindowMainController mainController;
    private static boolean debugMode = false;
    private static boolean dataLoaded = false;
    private static boolean authorised = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        assert setDebugMode();
        DataManager.logging();
        LoadingNotifier.getInstance().init();
        Main.primaryStage = primaryStage;
        Listeners.add(OnDataLoaded.class, () -> dataLoaded = true, true);
        ThreadPoolManager.getInstance().dependentExecute(() -> DataManager.getInstance().load(), "dataLoad");
        primaryStage.setResizable(false);
        primaryStage.setTitle("KeepYourPassword");
        showScene(NodeType.WINDOW_LOGIN.getScene());
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
        primaryStage.close();
        primaryStage.setScene(scene);
        primaryStage.show();
        SessionManager.getInstance().startListening(primaryStage);
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
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        SessionManager.getInstance().startListening(stage);
        stage.setOnCloseRequest(e -> SessionManager.getInstance().startListening(primaryStage));
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

    public static boolean isDataLoaded() {
        return dataLoaded;
    }

    private static boolean setDebugMode() {
        return (debugMode = true);
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setMainController(WindowMainController controller) {
        mainController = controller;
    }

    public static WindowMainController getMainController() {
        return mainController;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void logout() {
        loginPassword = "";
        showScene(NodeType.WINDOW_LOGIN.getScene());
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static boolean isAuthorised() {
        return authorised;
    }

    public static void setAuthorised() {
        Main.authorised = true;
    }
}
