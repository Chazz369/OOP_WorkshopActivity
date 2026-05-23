package com.inventory.app.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Singleton utility for managing JavaFX scene transitions.
 * Handles switching scenes on the primary stage and opening modal windows.
 */
public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;

    private static final String STYLESHEET_PATH = "/com/inventory/app/css/styles.css";

    private SceneManager() {
        // Private constructor for singleton
    }

    /**
     * Returns the singleton instance of SceneManager.
     *
     * @return the SceneManager instance
     */
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    /**
     * Initializes the SceneManager with the primary stage reference.
     *
     * @param primaryStage the application's primary Stage
     */
    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Loads an FXML file and sets it as the current scene on the primary stage.
     *
     * @param fxmlPath the path to the FXML file, relative to resources
     *                 (e.g., "/com/inventory/app/fxml/login.fxml")
     */
    public void switchScene(String fxmlPath) {
        switchScene(fxmlPath, null);
    }

    /**
     * Loads an FXML file, sets it as the current scene, and updates the stage title.
     *
     * @param fxmlPath the path to the FXML file, relative to resources
     * @param title    the title to set on the primary stage (null to keep current title)
     */
    public void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            applyStylesheet(scene);

            primaryStage.setScene(scene);

            if (title != null) {
                primaryStage.setTitle(title);
            }

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    /**
     * Opens a new Stage as a modal dialog with the given FXML and title.
     *
     * @param fxmlPath the path to the FXML file for the modal
     * @param title    the title of the modal window
     * @return the controller associated with the loaded FXML
     */
    public Object openModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            applyStylesheet(scene);

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(primaryStage);
            modalStage.setTitle(title);
            modalStage.setScene(scene);
            modalStage.showAndWait();

            return loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load modal FXML: " + fxmlPath, e);
        }
    }

    /**
     * Returns the primary Stage.
     *
     * @return the primary Stage
     */
    public Stage getStage() {
        return primaryStage;
    }

    /**
     * Applies the application stylesheet to the given scene.
     */
    private void applyStylesheet(Scene scene) {
        String css = getClass().getResource(STYLESHEET_PATH) != null
                ? getClass().getResource(STYLESHEET_PATH).toExternalForm()
                : null;

        if (css != null) {
            scene.getStylesheets().add(css);
        }
    }
}
