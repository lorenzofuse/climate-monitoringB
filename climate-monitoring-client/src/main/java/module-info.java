module com.climatemonitoring.client {
    // JavaFX modules required
    requires javafx.controls;
    requires javafx.fxml;

    // RMI for remote service calls
    requires java.rmi;
    requires java.sql;

    // Dependency on the common module
    requires com.climatemonitoring.common;

    // Export client packages
    exports com.climatemonitoring.client;
    exports com.climatemonitoring.client.controller;

    // Open packages to JavaFX for FXML loading
    opens com.climatemonitoring.client to javafx.fxml;
    opens com.climatemonitoring.client.controller to javafx.fxml;
}
