package com.inventory.app.dao;

import com.inventory.app.db.DatabaseConnection;
import com.inventory.app.model.Item;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data Access Object for Item entities.
 * Provides CRUD operations against the items table using raw JDBC.
 */
public class ItemDAO {

    /**
     * Retrieves all items with their category names, ordered by item name.
     *
     * @return a list of all items
     */
    public List<Item> findAll() {
        String sql = "SELECT i.id, i.sku, i.name, i.category_id, c.name AS category_name, "
                   + "i.unit_price, i.quantity, i.threshold, i.created_at, i.updated_at "
                   + "FROM items i LEFT JOIN categories c ON i.category_id = c.id "
                   + "ORDER BY i.name";

        List<Item> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Finds an item by its UUID.
     *
     * @param id the UUID of the item
     * @return an Optional containing the Item if found, or empty if not
     */
    public Optional<Item> findById(UUID id) {
        String sql = "SELECT i.id, i.sku, i.name, i.category_id, c.name AS category_name, "
                   + "i.unit_price, i.quantity, i.threshold, i.created_at, i.updated_at "
                   + "FROM items i LEFT JOIN categories c ON i.category_id = c.id "
                   + "WHERE i.id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Inserts a new item into the items table.
     *
     * @param item the Item object to insert
     */
    public void insert(Item item) {
        String sql = "INSERT INTO items (id, sku, name, category_id, unit_price, quantity, threshold, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, item.getId());
            stmt.setString(2, item.getSku());
            stmt.setString(3, item.getName());
            stmt.setInt(4, item.getCategoryId());
            stmt.setBigDecimal(5, item.getUnitPrice());
            stmt.setInt(6, item.getQuantity());
            stmt.setInt(7, item.getThreshold());
            stmt.setTimestamp(8, Timestamp.valueOf(item.getCreatedAt()));
            stmt.setTimestamp(9, Timestamp.valueOf(item.getUpdatedAt()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert item: " + e.getMessage(), e);
        }
    }

    /**
     * Updates all editable fields of an existing item and sets updated_at to now.
     *
     * @param item the Item object with updated values
     */
    public void update(Item item) {
        String sql = "UPDATE items SET sku = ?, name = ?, category_id = ?, unit_price = ?, "
                   + "quantity = ?, threshold = ?, updated_at = NOW() WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getSku());
            stmt.setString(2, item.getName());
            stmt.setInt(3, item.getCategoryId());
            stmt.setBigDecimal(4, item.getUnitPrice());
            stmt.setInt(5, item.getQuantity());
            stmt.setInt(6, item.getThreshold());
            stmt.setObject(7, item.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update item: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes an item by its UUID.
     *
     * @param id the UUID of the item to delete
     */
    public void delete(UUID id) {
        String deleteSalesSql = "DELETE FROM sales_log WHERE item_id = ?";
        String deleteItemSql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete associated sales logs first
                try (PreparedStatement stmtSales = conn.prepareStatement(deleteSalesSql)) {
                    stmtSales.setObject(1, id);
                    stmtSales.executeUpdate();
                }

                // Delete the item
                try (PreparedStatement stmtItem = conn.prepareStatement(deleteItemSql)) {
                    stmtItem.setObject(1, id);
                    stmtItem.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete item: " + e.getMessage(), e);
        }
    }

    /**
     * Finds all items where the quantity is at or below the threshold.
     *
     * @return a list of low stock items
     */
    public List<Item> findLowStockItems() {
        String sql = "SELECT i.id, i.sku, i.name, i.category_id, c.name AS category_name, "
                   + "i.unit_price, i.quantity, i.threshold, i.created_at, i.updated_at "
                   + "FROM items i LEFT JOIN categories c ON i.category_id = c.id "
                   + "WHERE i.quantity <= i.threshold "
                   + "ORDER BY i.quantity ASC";

        List<Item> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Updates only the quantity of an item by its UUID.
     *
     * @param id          the UUID of the item
     * @param newQuantity the new quantity value
     */
    public void updateQuantity(UUID id, int newQuantity) {
        String sql = "UPDATE items SET quantity = ?, updated_at = NOW() WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setObject(2, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update item quantity: " + e.getMessage(), e);
        }
    }

    /**
     * Maps a ResultSet row to an Item object.
     */
    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(UUID.fromString(rs.getString("id")));
        item.setSku(rs.getString("sku"));
        item.setName(rs.getString("name"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setCategoryName(rs.getString("category_name"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setQuantity(rs.getInt("quantity"));
        item.setThreshold(rs.getInt("threshold"));
        item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        item.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

        return item;
    }

    /**
     * Checks if an item with the given SKU/Product ID exists.
     * Optionally excludes a specific item UUID (used when editing).
     */
    public boolean existsBySku(String sku, UUID excludeId) {
        String sql = "SELECT COUNT(*) FROM items WHERE sku = ?" + (excludeId != null ? " AND id != ?" : "");
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sku);
            if (excludeId != null) {
                stmt.setObject(2, excludeId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
