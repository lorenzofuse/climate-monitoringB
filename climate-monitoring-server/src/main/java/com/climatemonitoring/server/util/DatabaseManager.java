package com.climatemonitoring.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private static DatabaseManager dbManager;
    private Connection connection;

    private DatabaseManager(String host, String user, String password) {

        this.dbUrl = String.format("jdbc:postgresql://%s/ClimateMonitoring", host);
        this.dbUser = user;
        this.dbPassword = password;

        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL caricato correttamente");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver non trovato.");
            throw new RuntimeException("PostgreSQL JDBC Driver non trovato", e);
        }
    }

    public static synchronized DatabaseManager initialize(String host, String user, String password) {
        if (dbManager == null) {
            System.out.println("Inizializzazione DatabaseManager con:");
            System.out.println("Host: " + host);
            System.out.println("User: " + user);
            System.out.println("URL completo: jdbc:postgresql://" + host + "/ClimateMonitoring");
            dbManager = new DatabaseManager(host, user, password);
        } else {
            throw new IllegalStateException("DatabaseManager è già stato inizializzato");
        }
        return dbManager;
    }

    public static synchronized DatabaseManager getInstance() {
        if (dbManager == null) {
            throw new IllegalStateException("DatabaseManager deve essere inizializzato prima dell'uso");
        }
        return dbManager;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("Tentativo di connessione al database...");
            System.out.println("URL: " + dbUrl);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connessione stabilita con successo!");
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("Connessione al database chiusa con successo");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }

    public boolean testConnection() {
        try {
            Connection testConn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Test di connessione completato con successo");
            return true;
        } catch (SQLException e) {
            System.err.println("Test di connessione fallito:");
            System.err.println("Messaggio: " + e.getMessage());
            System.err.println("Codice errore SQL: " + e.getSQLState());
            return false;
        }
    }
}