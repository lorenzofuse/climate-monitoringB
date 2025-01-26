package com.climatemonitoring.server.controller;

import com.climatemonitoring.server.ServerCM;
import com.climatemonitoring.server.util.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField
        ;
/**
 * Controller per la gestione dell'interfaccia di login del server per il monitoraggio climatico.
 *
 * Questa classe svolge un ruolo cruciale nel processo di connessione al database e
 * nell'inizializzazione del server RMI (Remote Method Invocation). Funge da ponte
 * tra l'interfaccia utente JavaFX e la logica di connessione del server.
 *
 * Responsabilità principali:
 * - Raccogliere le credenziali di connessione al database
 * - Gestire la connessione e disconnessione dal database
 * - Controllare l'abilitazione/disabilitazione dei campi dell'interfaccia
 * - Registrare e visualizzare messaggi di log delle operazioni
 *
 * Flusso di lavoro tipico:
 * 1. L'utente inserisce host, username e password
 * 2. Viene verificata la connessione al database
 * 3. In caso di successo, viene avviato il server RMI
 * 4. L'interfaccia viene aggiornata per riflettere lo stato di connessione
 *
 * @author [Nome dell'autore]
 * @version 1.0
 * @since [Data di implementazione]
 */
public class ServerLogin {

    /**
     * Campo di testo per l'inserimento dell'host del database.
     * Annotato con @FXML per l'iniezione automatica da parte di JavaFX.
     */
    @FXML private TextField hostField;


    /**
     * Campo di testo per l'inserimento del nome utente di connessione.
     * Utilizzato per l'autenticazione al database.
     */
    @FXML private TextField usernameField;


    /**
     * Campo password per l'inserimento delle credenziali di accesso.
     * Utilizza PasswordField per mascherare l'input sensibile.
     */
    @FXML private PasswordField passwordField;

    /**
     * Pulsante per avviare la connessione al database.
     * Attiva il processo di connessione e inizializzazione del server.
     */
    @FXML private Button connectButton;


    /**
     * Pulsante per disconnettere il server e chiudere la connessione.
     * Consente all'utente di terminare la sessione corrente.
     */
    @FXML public Button disconnectButton;

    /**
     * Area di testo per la registrazione e visualizzazione dei log.
     * Fornisce feedback all'utente sulle operazioni in corso.
     */
    @FXML private TextArea logArea;


    /**
     * Riferimento all'applicazione principale del server.
     * Utilizzato per gestire le operazioni di avvio e arresto del server.
     */
    private ServerCM mainApp;

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX.
     * Configura i listener per i pulsanti di connessione e disconnessione.
     *
     * Dettagli chiave:
     * - Imposta gli handler per i pulsanti connect e disconnect
     * - Disabilita inizialmente il pulsante di disconnessione
     */
    @FXML
    private void initialize() {
        connectButton.setOnAction(event -> handleConnessioni());
        disconnectButton.setOnAction(event -> handleDisconnessione());
        disconnectButton.setDisable(true);
    }

    /**
     * Gestisce il processo di connessione al database.
     *
     * Passaggi principali:
     * 1. Convalida l'input (host e username non vuoti)
     * 2. Tenta di inizializzare il DatabaseManager
     * 3. Verifica la connessione al database
     * 4. Avvia il server RMI in caso di connessione riuscita
     * 5. Aggiorna lo stato dell'interfaccia utente
     *
     * Gestisce eventuali eccezioni e fornisce feedback tramite log
     */
    private void handleConnessioni() {
        String host = hostField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (host.isEmpty() || username.isEmpty()) {
            appendLog("Errore: Tutti i campi sono obbligatori");
            return;
        }

        try {
            DatabaseManager dbManager = DatabaseManager.initialize(host, username, password);

            if (!dbManager.testConnection()) {
                appendLog("Errore: Impossibile connettersi al database. Verificare le credenziali.");
                return;
            }

            appendLog("Connessione al database stabilita con successo!");
            mainApp.startRMIServer(dbManager);

            hostField.setDisable(true);
            usernameField.setDisable(true);
            passwordField.setDisable(true);
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);

        } catch (Exception e) {
            appendLog("Errore durante la connessione al database: " + e.getMessage());
        }
    }

    /**
     * Gestisce la disconnessione dal database e l'arresto del server.
     *
     * Azioni principali:
     * - Arresta l'applicazione server
     * - Ripristina l'interfaccia utente allo stato iniziale
     * - Cancella i campi di input
     * - Registra l'esito dell'operazione
     */
    private void handleDisconnessione() {
        try {
            if (mainApp != null) {
                mainApp.stop();
            }

            hostField.setDisable(false);
            usernameField.setDisable(false);
            passwordField.setDisable(false);
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);

            appendLog("Disconnessione dal database effettuata con successo");


            hostField.clear();
            usernameField.clear();
            passwordField.clear();

        } catch (Exception e) {
            appendLog("Errore durante la disconnessione: " + e.getMessage());
        }
    }

    /**
     * Utility method per aggiungere messaggi al log.
     *
     * @param message Messaggio da aggiungere all'area di log
     */
    private void appendLog(String message) {
        logArea.appendText(message + "\n");
    }

    /**
     * Imposta il riferimento all'applicazione principale del server.
     *
     * @param mainApp Istanza principale dell'applicazione server
     */
    public void setMainApp(ServerCM mainApp) {
        this.mainApp = mainApp;
    }
}