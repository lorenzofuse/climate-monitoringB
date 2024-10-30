module com.climatemonitoring.common {
    requires java.rmi;
    requires java.sql;

    // Export common packages
    exports com.climatemonitoring.common.model;
    exports com.climatemonitoring.common.service;
    exports com.climatemonitoring.common.exception;
}
