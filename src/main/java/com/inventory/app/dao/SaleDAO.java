package com.inventory.app.dao;

import com.inventory.app.db.DatabaseConnection;
import com.inventory.app.model.SaleLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for SaleLog entities.
 * Provides insert and retrieval operations against the sales_log table.
 */
public class SaleDAO {

    /**
     * Inserts a new sale log entry into the sales_log table.
     *
     * @param sale the SaleLog object to insert
     */
    public void insert(SaleLog sale) {
        String sql = "INSERT INTO sales_log (item_id, quantity_sold, sold_by, sold_at) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, sale.getItemId());
            stmt.setInt(2, sale.getQuantitySold());
            stmt.setObject(3, sale.getSoldBy());
            stmt.setTimestamp(4, sale.getSoldAt() != null
                    ? Timestamp.valueOf(sale.getSoldAt())
                    : Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert sale log: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all sale log entries with item and user names resolved via JOINs.
     *
     * @return a list of all sale logs
     */
    public List<SaleLog> findAll() {
        String sql = "SELECT s.id, s.item_id, i.name AS item_name, "
                   + "s.quantity_sold, s.sold_by, u.full_name AS sold_by_name, s.sold_at "
                   + "FROM sales_log s "
                   + "LEFT JOIN items i ON s.item_id = i.id "
                   + "LEFT JOIN users u ON s.sold_by = u.id "
                   + "ORDER BY s.sold_at DESC";

        List<SaleLog> sales = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sales.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    /**
     * Maps a ResultSet row to a SaleLog object.
     */
    private SaleLog mapRow(ResultSet rs) throws SQLException {
        SaleLog sale = new SaleLog();
        sale.setId(UUID.fromString(rs.getString("id")));
        sale.setItemId(UUID.fromString(rs.getString("item_id")));
        sale.setItemName(rs.getString("item_name"));
        sale.setQuantitySold(rs.getInt("quantity_sold"));

        String soldById = rs.getString("sold_by");
        if (soldById != null) {
            sale.setSoldBy(UUID.fromString(soldById));
        }

        sale.setSoldByName(rs.getString("sold_by_name"));
        sale.setSoldAt(rs.getTimestamp("sold_at").toLocalDateTime());
        return sale;
    }
}
