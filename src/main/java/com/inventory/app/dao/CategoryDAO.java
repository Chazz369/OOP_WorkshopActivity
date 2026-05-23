package com.inventory.app.dao;

import com.inventory.app.db.DatabaseConnection;
import com.inventory.app.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Category entities.
 * Provides retrieval and insertion operations against the categories table.
 */
public class CategoryDAO {

    /**
     * Retrieves all categories ordered by name.
     *
     * @return a list of all categories
     */
    public List<Category> findAll() {
        String sql = "SELECT id, name FROM categories ORDER BY name";

        List<Category> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Inserts a new category and returns the created Category with its generated id.
     *
     * @param name the name of the category to create
     * @return the newly created Category with its auto-generated id
     */
    public Category insert(String name) {
        String sql = "INSERT INTO categories (name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Category category = new Category();
                    category.setId(generatedKeys.getInt(1));
                    category.setName(name);
                    return category;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert category: " + e.getMessage(), e);
        }

        throw new RuntimeException("Failed to retrieve generated key for category.");
    }
}
