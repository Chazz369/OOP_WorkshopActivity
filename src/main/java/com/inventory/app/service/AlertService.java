package com.inventory.app.service;

import com.inventory.app.dao.ItemDAO;
import com.inventory.app.model.Item;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for checking inventory levels and alerting
 * users about low-stock items via JavaFX alerts.
 */
public class AlertService {

    private final ItemDAO itemDAO;

    public AlertService() {
        this.itemDAO = new ItemDAO();
    }

    /**
     * Checks for low-stock items and displays a JavaFX warning alert if any are found.
     * The alert is shown on the JavaFX Application Thread using Platform.runLater().
     *
     * @return the count of low-stock items (useful for badge display)
     */
    public int checkAndAlertLowStock() {
        List<Item> lowStockItems = itemDAO.findLowStockItems();

        if (!lowStockItems.isEmpty()) {
            String itemDetails = lowStockItems.stream()
                    .map(item -> "• " + item.getName() + " (Qty: " + item.getQuantity() + ", Threshold: " + item.getThreshold() + ")")
                    .collect(Collectors.joining("\n"));

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Low Stock Warning");
                alert.setHeaderText(lowStockItems.size() + " item(s) are running low on stock!");
                alert.setContentText(itemDetails);
                alert.getDialogPane().setMinWidth(400);
                alert.showAndWait();
            });
        }

        return lowStockItems.size();
    }

    /**
     * Returns the count of low-stock items without showing an alert.
     *
     * @return the count of low-stock items
     */
    public int getLowStockCount() {
        return itemDAO.findLowStockItems().size();
    }
}
