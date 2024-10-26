module com.climatemonitoring {
    // JavaFX modules required
    requires javafx.controls;
    requires javafx.fxml;

    // RMI for remote service calls
    requires java.rmi;
    requires java.sql;

    // Add require to common module
    requires com.climatemonitoring.common;

    // Export the main package and controllers for JavaFX
    exports com.climatemonitoring;
    exports com.climatemonitoring.controller;

    // Open packages to JavaFX for FXML loading
    opens com.climatemonitoring to javafx.fxml;
    opens com.climatemonitoring.controller to javafx.fxml;

    // Export model package if needed by other modules
}