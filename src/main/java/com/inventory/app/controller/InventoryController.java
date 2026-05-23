package com.inventory.app.controller;

import com.inventory.app.dao.ItemDAO;
import com.inventory.app.model.Item;
import com.inventory.app.service.AlertService;
import com.inventory.app.util.SceneManager;
import com.inventory.app.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the main inventory dashboard.
 * Manages the inventory table, search/filter, CRUD operations, and navigation.
 */
public class InventoryController {

    // Top bar
    @FXML private Label pageTitle;
    @FXML private Label usernameLabel;

    // Toolbar
    @FXML private TextField searchField;
    @FXML private Button addItemBtn;

    // Table
    @FXML private TableView<Item> inventoryTable;
    @FXML private TableColumn<Item, String> skuCol;
    @FXML private TableColumn<Item, String> nameCol;
    @FXML private TableColumn<Item, String> categoryCol;
    @FXML private TableColumn<Item, Integer> quantityCol;
    @FXML private TableColumn<Item, BigDecimal> priceCol;
    @FXML private TableColumn<Item, Integer> thresholdCol;
    @FXML private TableColumn<Item, String> statusCol;
    @FXML private TableColumn<Item, Void> actionsCol;

    // Status bar
    @FXML private Label itemCountLabel;

    private final ObservableList<Item> itemList = FXCollections.observableArrayList();
    private FilteredList<Item> filteredList;
    private final ItemDAO itemDAO = new ItemDAO();
    private final AlertService alertService = new AlertService();

    @FXML
    public void initialize() {
        // Set username display
        if (SessionManager.getInstance().getCurrentUser() != null) {
            usernameLabel.setText(SessionManager.getInstance().getCurrentUser().getFullName());
        }

        // Configure table columns
        skuCol.setCellValueFactory(cellData -> cellData.getValue().skuProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryNameProperty());
        quantityCol.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        priceCol.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty());
        thresholdCol.setCellValueFactory(cellData -> cellData.getValue().thresholdProperty().asObject());

        // Configure Status column with custom cell factory
        statusCol.setCellValueFactory(cellData -> {
            boolean lowStock = cellData.getValue().isLowStock();
            return new SimpleStringProperty(lowStock ? "LOW STOCK" : "IN STOCK");
        });
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("status-ok", "status-low");
                } else {
                    setText(status);
                    getStyleClass().removeAll("status-ok", "status-low");
                    if ("LOW STOCK".equals(status)) {
                        getStyleClass().add("status-low");
                    } else {
                        getStyleClass().add("status-ok");
                    }
                }
            }
        });

        // Configure Actions column with custom cell factory
        actionsCol.setCellFactory(column -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().addAll("btn-default", "btn-table-action");
                deleteBtn.getStyleClass().addAll("btn-danger", "btn-table-action");
                container.setAlignment(javafx.geometry.Pos.CENTER);

                editBtn.setOnAction(event -> {
                    Item item = getTableRow().getItem();
                    if (item != null) {
                        openItemFormModal(item);
                    }
                });

                deleteBtn.setOnAction(event -> {
                    Item item = getTableRow().getItem();
                    if (item != null) {
                        handleDeleteItem(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });

        // Format price column to show currency
        priceCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("₱%.2f", price));
                }
            }
        });

        // Set up filtered list with search binding
        filteredList = new FilteredList<>(itemList, p -> true);

        // Add search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return item.getName().toLowerCase().contains(lowerCaseFilter)
                        || (item.getCategoryName() != null && item.getCategoryName().toLowerCase().contains(lowerCaseFilter))
                        || (item.getSku() != null && item.getSku().toLowerCase().contains(lowerCaseFilter));
            });
            updateItemCount();
        });

        // Wrap in SortedList and bind to table
        SortedList<Item> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(inventoryTable.comparatorProperty());
        inventoryTable.setItems(sortedList);

        // Load data
        loadItems();
    }

    /**
     * Loads all items from the database on a background thread.
     */
    private void loadItems() {
        Task<List<Item>> loadTask = new Task<>() {
            @Override
            protected List<Item> call() throws Exception {
                return itemDAO.findAll();
            }
        };

        loadTask.setOnSucceeded(event -> {
            itemList.setAll(loadTask.getValue());
            updateItemCount();
        });

        loadTask.setOnFailed(event -> {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load inventory items.");
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleAddItem() {
        openItemFormModal(null);
    }

    /**
     * Opens the item form modal. If item is null, opens in Add mode; otherwise Edit mode.
     */
    private void openItemFormModal(Item item) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/inventory/app/fxml/item_form.fxml"));
            Parent root = loader.load();

            ItemFormController controller = loader.getController();
            if (item != null) {
                controller.setItem(item);
            }

            Scene scene = new Scene(root);
            String css = getClass().getResource("/com/inventory/app/css/styles.css") != null
                    ? getClass().getResource("/com/inventory/app/css/styles.css").toExternalForm()
                    : null;
            if (css != null) {
                scene.getStylesheets().add(css);
            }

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(SceneManager.getInstance().getStage());
            modalStage.setTitle(item != null ? "Edit Item" : "Add New Item");
            modalStage.setScene(scene);
            modalStage.showAndWait();

            loadItems();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open item form.");
        }
    }

    private void handleDeleteItem(Item item) {
        if (item == null) {
            return;
        }

        // Confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Item");
        confirmation.setHeaderText("Delete \"" + item.getName() + "\"?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Task<Void> deleteTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    itemDAO.delete(item.getId());
                    return null;
                }
            };

            deleteTask.setOnSucceeded(event -> {
                loadItems();
            });

            deleteTask.setOnFailed(event -> {
                Throwable exception = deleteTask.getException();
                String details = (exception != null && exception.getCause() != null)
                        ? exception.getCause().getMessage()
                        : (exception != null ? exception.getMessage() : "Unknown error");
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete item.\n\nReason: " + details);
            });

            Thread thread = new Thread(deleteTask);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    private void handleRecordSale() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/inventory/app/fxml/sale_form.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            String css = getClass().getResource("/com/inventory/app/css/styles.css") != null
                    ? getClass().getResource("/com/inventory/app/css/styles.css").toExternalForm()
                    : null;
            if (css != null) {
                scene.getStylesheets().add(css);
            }

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(SceneManager.getInstance().getStage());
            modalStage.setTitle("Record Sale");
            modalStage.setScene(scene);
            modalStage.showAndWait();

            loadItems();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open sale form.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadItems();
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().switchScene("/com/inventory/app/fxml/login.fxml",
                "Inventory Management System - Login");
    }

    private void updateItemCount() {
        int totalItems = itemList.size();
        int filteredItems = filteredList.size();
        if (totalItems == filteredItems) {
            itemCountLabel.setText(totalItems + " item" + (totalItems != 1 ? "s" : "") + " total");
        } else {
            itemCountLabel.setText(filteredItems + " of " + totalItems + " items shown");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
