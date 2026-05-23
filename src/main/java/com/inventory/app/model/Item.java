package com.inventory.app.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JavaFX-compatible model representing a row in the {@code items} table.
 * <p>
 * All fields are backed by JavaFX properties so they can be bound
 * directly to {@code TableView} columns via
 * {@code PropertyValueFactory} or lambda cell-value factories.
 */
public class Item {

    private final ObjectProperty<UUID>       id           = new SimpleObjectProperty<>();
    private final StringProperty             sku          = new SimpleStringProperty();
    private final StringProperty             name         = new SimpleStringProperty();
    private final IntegerProperty            categoryId   = new SimpleIntegerProperty();
    private final StringProperty             categoryName = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> unitPrice    = new SimpleObjectProperty<>();
    private final IntegerProperty            quantity     = new SimpleIntegerProperty();
    private final IntegerProperty            threshold    = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    public Item() {
    }

    public Item(UUID id, String sku, String name, int categoryId, String categoryName,
                BigDecimal unitPrice, int quantity, int threshold,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        setId(id);
        setSku(sku);
        setName(name);
        setCategoryId(categoryId);
        setCategoryName(categoryName);
        setUnitPrice(unitPrice);
        setQuantity(quantity);
        setThreshold(threshold);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
    }

    // ------------------------------------------------------------------
    // Derived / helper methods
    // ------------------------------------------------------------------

    /**
     * Returns {@code true} when the current quantity is at or below the
     * low-stock threshold.
     *
     * @return whether this item is considered low-stock
     */
    public boolean isLowStock() {
        return getQuantity() <= getThreshold();
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
    // sku
    // ------------------------------------------------------------------

    public StringProperty skuProperty() {
        return sku;
    }

    public String getSku() {
        return sku.get();
    }

    public void setSku(String sku) {
        this.sku.set(sku);
    }

    // ------------------------------------------------------------------
    // name
    // ------------------------------------------------------------------

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    // ------------------------------------------------------------------
    // categoryId
    // ------------------------------------------------------------------

    public IntegerProperty categoryIdProperty() {
        return categoryId;
    }

    public int getCategoryId() {
        return categoryId.get();
    }

    public void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
    }

    // ------------------------------------------------------------------
    // categoryName
    // ------------------------------------------------------------------

    public StringProperty categoryNameProperty() {
        return categoryName;
    }

    public String getCategoryName() {
        return categoryName.get();
    }

    public void setCategoryName(String categoryName) {
        this.categoryName.set(categoryName);
    }

    // ------------------------------------------------------------------
    // unitPrice
    // ------------------------------------------------------------------

    public ObjectProperty<BigDecimal> unitPriceProperty() {
        return unitPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice.get();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice.set(unitPrice);
    }

    // ------------------------------------------------------------------
    // quantity
    // ------------------------------------------------------------------

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    // ------------------------------------------------------------------
    // threshold
    // ------------------------------------------------------------------

    public IntegerProperty thresholdProperty() {
        return threshold;
    }

    public int getThreshold() {
        return threshold.get();
    }

    public void setThreshold(int threshold) {
        this.threshold.set(threshold);
    }

    // ------------------------------------------------------------------
    // createdAt
    // ------------------------------------------------------------------

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    // ------------------------------------------------------------------
    // updatedAt
    // ------------------------------------------------------------------

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    // ------------------------------------------------------------------
    // Object overrides
    // ------------------------------------------------------------------

    @Override
    public String toString() {
        return "Item{" +
                "id=" + getId() +
                ", sku='" + getSku() + '\'' +
                ", name='" + getName() + '\'' +
                ", qty=" + getQuantity() +
                ", threshold=" + getThreshold() +
                ", lowStock=" + isLowStock() +
                '}';
    }
}
