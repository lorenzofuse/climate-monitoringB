package com.climatemonitoring.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestore della connessione al database PostgreSQL per l'applicazione Climate Monitoring.
 *
 * Questa classe implementa il pattern Singleton per garantire un punto di accesso
 * centralizzato e controllato alla connessione del database. Offre funzionalità
 * essenziali per:
 * - Inizializzazione della connessione al database
 * - Gestione delle credenziali di accesso
 * - Apertura e chiusura delle connessioni
 * - Test di connettività
 *
 * Caratteristiche principali:
 * - Caricamento dinamico del driver JDBC PostgreSQL
 * - Connessione sicura e gestita al database
 * - Metodi per test e gestione della connessione
 *
 * Pattern di utilizzo:
 * 1. Inizializzare con {@link #initialize(String, String, String)}
 * 2. Ottenere l'istanza con {@link #getInstance()}
 * 3. Utilizzare {@link #getConnection()} per operazioni di database
 * 4. Chiudere la connessione con {@link #closeConnection()}
 *
 * @author [Nome Sviluppatore]
 */
public class DatabaseManager {

    /**
     * URL di connessione al database PostgreSQL.
     *
     * Formato: jdbc:postgresql://[host]/ClimateMonitoring
     * Costruito dinamicamente in base all'host fornito durante l'inizializzazione.
     */
    private final String dbUrl;

    /**
     * Nome utente per l'accesso al database.
     * Memorizzato in modo sicuro per stabilire la connessione.
     */
    private final String dbUser;

    /**
     * Password per l'autenticazione al database.
     * Gestita con attenzione per garantire la sicurezza delle credenziali.
     */
    private final String dbPassword;

    /**
     * Istanza singleton del DatabaseManager.
     * Garantisce un unico punto di accesso alla gestione del database.
     */
    private static DatabaseManager dbManager;

    /**
     * Connessione attiva al database PostgreSQL.
     * Gestita per essere riutilizzata o chiusa secondo necessità.
     */
    private Connection connection;


    /**
     * Costruttore privato per implementare il pattern Singleton.
     *
     * Carica il driver JDBC PostgreSQL e prepara la configurazione
     * per la connessione al database.
     *
     * @param host Indirizzo del server di database
     * @param user Nome utente per l'accesso
     * @param password Password di autenticazione
     * @throws RuntimeException Se il driver JDBC non può essere caricato
     */
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


    /**
     * Metodo di inizializzazione del gestore database.
     *
     * Crea una singola istanza del DatabaseManager seguendo il pattern Singleton.
     * Garantisce che:
     * - Un solo gestore database sia creato
     * - Le credenziali siano impostate una sola volta
     *
     * @param host Indirizzo del server di database
     * @param user Nome utente per l'accesso
     * @param password Password di autenticazione
     * @return Istanza singleton del DatabaseManager
     * @throws IllegalStateException Se si tenta di inizializzare più volte
     */
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


    /**
     * Recupera l'istanza singleton del DatabaseManager.
     *
     * Metodo di accesso globale al gestore database, utilizzabile
     * dopo l'inizializzazione.
     *
     * @return Istanza corrente del DatabaseManager
     * @throws IllegalStateException Se il DatabaseManager non è stato inizializzato
     */
    public static synchronized DatabaseManager getInstance() {
        if (dbManager == null) {
            throw new IllegalStateException("DatabaseManager deve essere inizializzato prima dell'uso");
        }
        return dbManager;
    }


    /**
     * Ottiene una connessione attiva al database.
     *
     * Gestisce in modo intelligente la connessione:
     * - Crea una nuova connessione se non esistente
     * - Riutilizza la connessione esistente se valida
     * - Reconnette automaticamente in caso di connessione chiusa
     *
     * @return Connessione attiva al database PostgreSQL
     * @throws SQLException In caso di errori durante la connessione
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("Tentativo di connessione al database...");
            System.out.println("URL: " + dbUrl);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connessione stabilita con successo!");
        }
        return connection;
    }


    /**
     * Chiude la connessione corrente al database.
     *
     * Operazioni principali:
     * - Verifica l'esistenza di una connessione aperta
     * - Chiude la connessione in modo sicuro
     * - Gestisce eventuali errori durante la chiusura
     *
     * Best practice: chiamare sempre questo metodo al termine delle operazioni di database
     */
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


    /**
     * Verifica la connettività al database.
     *
     * Esegue un test di connessione rapido per:
     * - Confermare la raggiungibilità del server
     * - Validare le credenziali di accesso
     * - Diagnosticare eventuali problemi di connessione
     *
     * @return true se la connessione è riuscita, false altrimenti
     */
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