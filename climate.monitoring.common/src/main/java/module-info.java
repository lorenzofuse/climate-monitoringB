module climate.monitoring.common {
    requires java.rmi;
    requires java.sql;

    exports com.climatemonitoring.common.model;
    exports com.climatemonitoring.common.service;
}