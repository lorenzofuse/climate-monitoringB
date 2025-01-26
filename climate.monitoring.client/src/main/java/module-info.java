module climate.monitoring.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.sql;
    requires climate.monitoring.common;

    exports com.climatemonitoring.client;
    exports com.climatemonitoring.client.controller;

    opens com.climatemonitoring.client to javafx.fxml;
    opens com.climatemonitoring.client.controller to javafx.fxml;
}