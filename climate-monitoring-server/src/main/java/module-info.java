module com.climatemonitoring.server {
    requires java.rmi;
    requires java.sql;
    requires com.climatemonitoring.common;


    // Export server packages
    exports com.climatemonitoring.server.server;
}
