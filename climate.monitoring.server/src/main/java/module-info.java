module climate.monitoring.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.rmi;
    requires climate.monitoring.common;
    requires org.postgresql.jdbc;

    opens com.climatemonitoring.server to javafx.fxml;
    opens com.climatemonitoring.server.controller to javafx.fxml;

    exports com.climatemonitoring.server;
    exports com.climatemonitoring.server.controller;
    exports com.climatemonitoring.server.server to java.rmi;

    uses com.climatemonitoring.common.service.ClimateMonitoringService;
}