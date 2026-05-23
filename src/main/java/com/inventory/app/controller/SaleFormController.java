package com.inventory.app.controller;

import com.inventory.app.dao.ItemDAO;
import com.inventory.app.dao.SaleDAO;
import com.inventory.app.model.Item;
import com.inventory.app.model.SaleLog;
import com.inventory.app.service.AlertService;
import com.inventory.app.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for the Record Sale modal form.
 * Handles item selection, stock display, and sale recording.
 */
public class SaleFormController {

    @FXML private ComboBox<Item> itemCombo;
    @FXML private Label currentStockLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Label errorLabel;

    private final ItemDAO itemDAO = new ItemDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final AlertService alertService = new AlertService();

    @FXML
    public void initialize() {
        // Load items into ComboBox
        loadItems();

        // Set up item display format (name + SKU)
        itemCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Item item) {
                if (item == null) return null;
                return item.getName() + " (" + item.getSku() + ")";
            }

            @Override
            public Item fromString(String string) {
                return null; // Not needed for non-editable ComboBox
            }
        });

        // Set up quantity spinner with initial values
        SpinnerValueFactory.IntegerSpinnerValueFactory spinnerFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1);
        quantitySpinner.setValueFactory(spinnerFactory);
        quantitySpinner.setEditable(true);

        // Add listener to item selection to update stock and spinner max
        itemCombo.valueProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                int stock = newItem.getQuantity();
                currentStockLabel.setText(String.valueOf(stock));
                currentStockLabel.setStyle(stock <= newItem.getThreshold() ?
                        "-fx-text-fill: #e63946; -fx-font-weight: bold;" :
                        "-fx-text-fill: #2ec4b6; -fx-font-weight: bold;");

                // Update spinner max to current stock
                SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Math.max(1, stock), 1);
                quantitySpinner.setValueFactory(factory);
            } else {
                currentStockLabel.setText("—");
                currentStockLabel.setStyle("");
            }
            hideError();
        });
    }

    @FXML
    private void handleConfirm() {
        hideError();

        // Validate item selected
        Item selectedItem = itemCombo.getValue();
        if (selectedItem == null) {
            showError("Please select an item.");
            return;
        }

        int quantityToSell = quantitySpinner.getValue();
        if (quantityToSell <= 0) {
            showError("Quantity must be greater than zero.");
            return;
        }

        if (quantityToSell > selectedItem.getQuantity()) {
            showError("Insufficient stock. Available: " + selectedItem.getQuantity());
            return;
        }

        int newQuantity = selectedItem.getQuantity() - quantityToSell;

        Task<Void> saleTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Update item quantity
                itemDAO.updateQuantity(selectedItem.getId(), newQuantity);

                // Record the sale
                SaleLog saleLog = new SaleLog();
                saleLog.setItemId(selectedItem.getId());
                saleLog.setItemName(selectedItem.getName());
                saleLog.setQuantitySold(quantityToSell);
                saleLog.setSoldAt(LocalDateTime.now());
                if (SessionManager.getInstance().getCurrentUser() != null) {
                    saleLog.setSoldBy(SessionManager.getInstance().getCurrentUser().getId());
                    saleLog.setSoldByName(SessionManager.getInstance().getCurrentUser().getFullName());
                }
                saleDAO.insert(saleLog);

                return null;
            }
        };

        saleTask.setOnSucceeded(event -> {
            // Check low stock alerts
            try {
                alertService.checkAndAlertLowStock();
            } catch (Exception ignored) {
            }
            getStage().close();
        });

        saleTask.setOnFailed(event -> {
            showError("Failed to record sale. Please try again.");
        });

        Thread thread = new Thread(saleTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleCancel() {
        getStage().close();
    }

    private void loadItems() {
        Task<List<Item>> loadTask = new Task<>() {
            @Override
            protected List<Item> call() throws Exception {
                return itemDAO.findAll();
            }
        };

        loadTask.setOnSucceeded(event -> {
            itemCombo.setItems(FXCollections.observableArrayList(loadTask.getValue()));
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private Stage getStage() {
        return (Stage) itemCombo.getScene().getWindow();
    }
}
