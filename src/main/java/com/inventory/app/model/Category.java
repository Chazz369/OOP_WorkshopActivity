package com.inventory.app.model;

/**
 * Simple POJO representing a row in the {@code categories} table.
 * <p>
 * {@link #toString()} returns the category name so that instances
 * render correctly when used directly inside a JavaFX {@code ComboBox}.
 */
public class Category {

    private int    id;
    private String name;

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    public Category() {
    }

    public Category(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    // ------------------------------------------------------------------
    // Getters / Setters
    // ------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ------------------------------------------------------------------
    // Object overrides
    // ------------------------------------------------------------------

    /**
     * Returns the category name — used by {@code ComboBox} for display.
     */
    @Override
    public String toString() {
        return name;
    }
}
