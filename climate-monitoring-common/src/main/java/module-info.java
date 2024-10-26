module com.climatemonitoring.common {
    requires java.rmi;
    requires java.sql;

    exports com.climatemonitoring.model;
    exports com.climatemonitoring.service;
}