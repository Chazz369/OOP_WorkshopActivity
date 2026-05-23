package com.inventory.app;

import com.inventory.app.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX Application entry point for the Inventory Management System.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize the SceneManager with the primary stage
        SceneManager.getInstance().init(primaryStage);

        // Configure the primary stage
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        // Load the login screen as the initial scene
        SceneManager.getInstance().switchScene("/com/inventory/app/fxml/login.fxml",
                "Inventory Management System");

        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
