package com.inventory.app.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JavaFX-compatible model representing a row in the {@code sales_log} table.
 * <p>
 * Includes derived display fields ({@code itemName}, {@code soldByName})
 * that are populated via JOINs for convenient UI rendering.
 */
public class SaleLog {

    private final ObjectProperty<UUID> id           = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> itemId       = new SimpleObjectProperty<>();
    private final StringProperty       itemName     = new SimpleStringProperty();  // display-only
    private final IntegerProperty      quantitySold = new SimpleIntegerProperty();
    private final ObjectProperty<UUID> soldBy       = new SimpleObjectProperty<>();
    private final StringProperty       soldByName   = new SimpleStringProperty();  // display-only
    private final ObjectProperty<LocalDateTime> soldAt = new SimpleObjectProperty<>();

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    public SaleLog() {
    }

    public SaleLog(UUID id, UUID itemId, String itemName,
                   int quantitySold, UUID soldBy, String soldByName,
                   LocalDateTime soldAt) {
        setId(id);
        setItemId(itemId);
        setItemName(itemName);
        setQuantitySold(quantitySold);
        setSoldBy(soldBy);
        setSoldByName(soldByName);
        setSoldAt(soldAt);
    }

    // ------------------------------------------------------------------
    // id
    // ------------------------------------------------------------------

    public ObjectProperty<UUID> idProperty() {
        return id;
    }

    public UUID getId() {
        return id.get();
    }

    public void setId(UUID id) {
        this.id.set(id);
    }

    // ------------------------------------------------------------------
    // itemId
    // ------------------------------------------------------------------

    public ObjectProperty<UUID> itemIdProperty() {
        return itemId;
    }

    public UUID getItemId() {
        return itemId.get();
    }

    public void setItemId(UUID itemId) {
        this.itemId.set(itemId);
    }

    // ------------------------------------------------------------------
    // itemName (display-only, populated via JOIN)
    // ------------------------------------------------------------------

    public StringProperty itemNameProperty() {
        return itemName;
    }

    public String getItemName() {
        return itemName.get();
    }

    public void setItemName(String itemName) {
        this.itemName.set(itemName);
    }

    // ------------------------------------------------------------------
    // quantitySold
    // ------------------------------------------------------------------

    public IntegerProperty quantitySoldProperty() {
        return quantitySold;
    }

    public int getQuantitySold() {
        return quantitySold.get();
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold.set(quantitySold);
    }

    // ------------------------------------------------------------------
    // soldBy
    // ------------------------------------------------------------------

    public ObjectProperty<UUID> soldByProperty() {
        return soldBy;
    }

    public UUID getSoldBy() {
        return soldBy.get();
    }

    public void setSoldBy(UUID soldBy) {
        this.soldBy.set(soldBy);
    }

    // ------------------------------------------------------------------
    // soldByName (display-only, populated via JOIN)
    // ------------------------------------------------------------------

    public StringProperty soldByNameProperty() {
        return soldByName;
    }

    public String getSoldByName() {
        return soldByName.get();
    }

    public void setSoldByName(String soldByName) {
        this.soldByName.set(soldByName);
    }

    // ------------------------------------------------------------------
    // soldAt
    // ------------------------------------------------------------------

    public ObjectProperty<LocalDateTime> soldAtProperty() {
        return soldAt;
    }

    public LocalDateTime getSoldAt() {
        return soldAt.get();
    }

    public void setSoldAt(LocalDateTime soldAt) {
        this.soldAt.set(soldAt);
    }

    // ------------------------------------------------------------------
    // Object overrides
    // ------------------------------------------------------------------

    @Override
    public String toString() {
        return "SaleLog{" +
                "id=" + getId() +
                ", itemName='" + getItemName() + '\'' +
                ", qtySold=" + getQuantitySold() +
                ", soldByName='" + getSoldByName() + '\'' +
                ", soldAt=" + getSoldAt() +
                '}';
    }
}
