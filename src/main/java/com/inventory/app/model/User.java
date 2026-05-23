package com.inventory.app.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JavaFX-compatible model representing a row in the {@code users} table.
 * <p>
 * Exposes both standard getters/setters <em>and</em> JavaFX property
 * accessors so that it works seamlessly with {@code TableView} column
 * bindings and property listeners.
 */
public class User {

    private final ObjectProperty<UUID> id            = new SimpleObjectProperty<>();
    private final StringProperty       username      = new SimpleStringProperty();
    private final StringProperty       passwordHash  = new SimpleStringProperty();
    private final StringProperty       fullName      = new SimpleStringProperty();
    private final StringProperty       role          = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    public User() {
    }

    public User(UUID id, String username, String passwordHash,
                String fullName, String role, LocalDateTime createdAt) {
        setId(id);
        setUsername(username);
        setPasswordHash(passwordHash);
        setFullName(fullName);
        setRole(role);
        setCreatedAt(createdAt);
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
    // username
    // ------------------------------------------------------------------

    public StringProperty usernameProperty() {
        return username;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    // ------------------------------------------------------------------
    // passwordHash
    // ------------------------------------------------------------------

    public StringProperty passwordHashProperty() {
        return passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash.get();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash.set(passwordHash);
    }

    // ------------------------------------------------------------------
    // fullName
    // ------------------------------------------------------------------

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public String getFullName() {
        return fullName.get();
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    // ------------------------------------------------------------------
    // role
    // ------------------------------------------------------------------

    public StringProperty roleProperty() {
        return role;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
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
    // Object overrides
    // ------------------------------------------------------------------

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", role='" + getRole() + '\'' +
                '}';
    }
}
