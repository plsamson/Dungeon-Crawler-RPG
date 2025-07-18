package game.gui;

import game.mecanique.GameLoop;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//Main UI handler - Initializes and switches between main scenes
public class UiInitializer extends Application {

    public static UiController controller;
    private static UiInitializer instance;
    private Stage stage;
    private String activeScene;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        instance = this;
        stage.setTitle("Dungeon Crawler RPG");
        loadMenu();
    }

    private void loadFXML(String fxmlFile) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        controller = loader.getController();
        controller.setUiInitializer(this);
        if (activeScene.equals("game")) {
            GameLoop.start();
        }
        stage.show();
        if (activeScene.equals("game")) {
            controller.initializeUI();
        }
    }

    protected void loadMenu() throws Exception {
        activeScene = "menu";
        loadFXML("/gui/MenuUI.fxml");
    }

    protected void loadGame() throws Exception {
        activeScene = "game";
        loadFXML("/gui/GameUI.fxml");
    }

    public static UiInitializer getInstance() {
        return instance;
    }

    public static void initialize() {
        launch();
    }


}