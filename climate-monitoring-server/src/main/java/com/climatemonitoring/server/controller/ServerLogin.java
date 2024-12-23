package com.climatemonitoring.server.controller;

import com.climatemonitoring.server.ServerCM;
import com.climatemonitoring.server.util.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ServerLogin {

    @FXML private TextField hostField;

    @FXML private TextField usernameField;

    @FXML private PasswordField passwordField;

    @FXML private Button connectButton;
    @FXML private TextArea logArea;

    private ServerCM mainApp;

    @FXML
    private void initialize() {
        connectButton.setOnAction(event -> handleConnect());
    }

    private void handleConnect() {
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

            // Disabilita i campi dopo la connessione
            hostField.setDisable(true);
            usernameField.setDisable(true);
            passwordField.setDisable(true);
            connectButton.setDisable(true);

        } catch (Exception e) {
            appendLog("Errore durante la connessione al database: " + e.getMessage());
        }
    }

    private void appendLog(String message) {
        logArea.appendText(message + "\n");
    }

    public void setMainApp(ServerCM mainApp) {
        this.mainApp = mainApp;
    }
}