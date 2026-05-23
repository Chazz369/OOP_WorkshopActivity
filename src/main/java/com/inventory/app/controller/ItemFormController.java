package com.inventory.app.controller;

import com.inventory.app.dao.CategoryDAO;
import com.inventory.app.dao.ItemDAO;
import com.inventory.app.model.Category;
import com.inventory.app.model.Item;
import com.inventory.app.service.AlertService;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller for the Add/Edit Item modal form.
 * Handles field validation, category loading, and save/update operations.
 */
public class ItemFormController {

    @FXML private Label formTitle;
    @FXML private TextField skuField;
    @FXML private TextField nameField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private TextField priceField;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Spinner<Integer> thresholdSpinner;

    // Error labels
    @FXML private Label skuError;
    @FXML private Label nameError;
    @FXML private Label categoryError;
    @FXML private Label priceError;
    @FXML private Label quantityError;
    @FXML private Label thresholdError;

    private Item editingItem = null;
    private final ItemDAO itemDAO = new ItemDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final AlertService alertService = new AlertService();

    @FXML
    public void initialize() {
        // Load categories into ComboBox
        loadCategories();

        // Set up quantity spinner (0 to 999999, default 0)
        SpinnerValueFactory.IntegerSpinnerValueFactory qtyFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999999, 0);
        quantitySpinner.setValueFactory(qtyFactory);
        quantitySpinner.setEditable(true);

        // Set up threshold spinner (0 to 999999, default 5)
        SpinnerValueFactory.IntegerSpinnerValueFactory threshFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999999, 5);
        thresholdSpinner.setValueFactory(threshFactory);
        thresholdSpinner.setEditable(true);

        // Restrict price field to numeric input
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*\\.?\\d*")) {
                priceField.setText(oldVal);
            }
        });
    }

    /**
     * Sets the item to edit. Called by InventoryController for edit mode.
     * Populates all form fields with the item's current values.
     */
    public void setItem(Item item) {
        this.editingItem = item;
        formTitle.setText("Edit Item");

        // Populate fields
        skuField.setText(item.getSku());
        nameField.setText(item.getName());
        priceField.setText(item.getUnitPrice() != null ? item.getUnitPrice().toPlainString() : "0");
        quantitySpinner.getValueFactory().setValue(item.getQuantity());
        thresholdSpinner.getValueFactory().setValue(item.getThreshold());

        // Select the matching category in the ComboBox
        if (item.getCategoryId() > 0) {
            for (Category cat : categoryCombo.getItems()) {
                if (cat.getId() == item.getCategoryId()) {
                    categoryCombo.setValue(cat);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) {
            return;
        }

        // Gather form data
        String sku = skuField.getText().trim();
        String name = nameField.getText().trim();
        Category selectedCategory = categoryCombo.getValue();
        BigDecimal price = new BigDecimal(priceField.getText().trim());
        int quantity = quantitySpinner.getValue();
        int threshold = thresholdSpinner.getValue();

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (editingItem != null) {
                    // Update existing item
                    editingItem.setSku(sku);
                    editingItem.setName(name);
                    editingItem.setCategoryId(selectedCategory.getId());
                    editingItem.setCategoryName(selectedCategory.getName());
                    editingItem.setUnitPrice(price);
                    editingItem.setQuantity(quantity);
                    editingItem.setThreshold(threshold);
                    itemDAO.update(editingItem);
                } else {
                    // Create new item
                    Item newItem = new Item();
                    newItem.setId(UUID.randomUUID());
                    newItem.setSku(sku);
                    newItem.setName(name);
                    newItem.setCategoryId(selectedCategory.getId());
                    newItem.setCategoryName(selectedCategory.getName());
                    newItem.setUnitPrice(price);
                    newItem.setQuantity(quantity);
                    newItem.setThreshold(threshold);
                    newItem.setCreatedAt(LocalDateTime.now());
                    newItem.setUpdatedAt(LocalDateTime.now());
                    itemDAO.insert(newItem);
                }
                return null;
            }
        };

        saveTask.setOnSucceeded(event -> {
            // Check for low stock alerts
            try {
                alertService.checkAndAlertLowStock();
            } catch (Exception ignored) {
            }
            getStage().close();
        });

        saveTask.setOnFailed(event -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save item. Please try again.");
            alert.showAndWait();
        });

        Thread thread = new Thread(saveTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleCancel() {
        getStage().close();
    }

    /**
     * Validates all form fields and shows per-field error messages.
     * @return true if all fields are valid
     */
    private boolean validateFields() {
        boolean valid = true;

        // Reset all errors
        clearFieldError(skuError);
        clearFieldError(nameError);
        clearFieldError(categoryError);
        clearFieldError(priceError);
        clearFieldError(quantityError);
        clearFieldError(thresholdError);

        // Product ID validation
        String sku = skuField.getText();
        if (sku == null || sku.trim().isEmpty()) {
            showFieldError(skuError, "Product ID is required");
            valid = false;
        } else {
            sku = sku.trim();
            if (itemDAO.existsBySku(sku, editingItem != null ? editingItem.getId() : null)) {
                showFieldError(skuError, "Product ID already exists");
                valid = false;
            }
        }

        // Name validation
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showFieldError(nameError, "Item name is required");
            valid = false;
        }

        // Category validation
        if (categoryCombo.getValue() == null) {
            showFieldError(categoryError, "Please select a category");
            valid = false;
        }

        // Price validation
        String priceText = priceField.getText();
        if (priceText == null || priceText.trim().isEmpty()) {
            showFieldError(priceError, "Price is required");
            valid = false;
        } else {
            try {
                double price = Double.parseDouble(priceText.trim());
                if (price < 0) {
                    showFieldError(priceError, "Price must be a positive number");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                showFieldError(priceError, "Please enter a valid number");
                valid = false;
            }
        }

        // Quantity validation
        if (quantitySpinner.getValue() == null || quantitySpinner.getValue() <= 0) {
            showFieldError(quantityError, "Quantity must be greater than 0");
            valid = false;
        }

        return valid;
    }

    private void showFieldError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearFieldError(Label errorLabel) {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void loadCategories() {
        Task<List<Category>> task = new Task<>() {
            @Override
            protected List<Category> call() throws Exception {
                return categoryDAO.findAll();
            }
        };

        task.setOnSucceeded(event -> {
            categoryCombo.setItems(FXCollections.observableArrayList(task.getValue()));
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private Stage getStage() {
        return (Stage) skuField.getScene().getWindow();
    }
}
