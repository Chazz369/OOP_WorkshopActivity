module com.inventory.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.prefs;
    requires jbcrypt;
    requires io.github.cdimascio.dotenv.java;

    opens com.inventory.app to javafx.fxml;
    opens com.inventory.app.controller to javafx.fxml;
    opens com.inventory.app.model to javafx.fxml, javafx.base;

    exports com.inventory.app;
    exports com.inventory.app.controller;
    exports com.inventory.app.model;
    exports com.inventory.app.config;
    exports com.inventory.app.db;
    exports com.inventory.app.dao;
    exports com.inventory.app.service;
    exports com.inventory.app.util;
}
