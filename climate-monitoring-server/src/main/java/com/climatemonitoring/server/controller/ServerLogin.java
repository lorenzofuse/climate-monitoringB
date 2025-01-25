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
    @FXML public Button disconnectButton;
    @FXML private TextArea logArea;

    private ServerCM mainApp;

    @FXML
    private void initialize() {
        connectButton.setOnAction(event -> handleConnessioni());
        disconnectButton.setOnAction(event -> handleDisconnessione());
        disconnectButton.setDisable(true);
    }

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

    private void appendLog(String message) {
        logArea.appendText(message + "\n");
    }

    public void setMainApp(ServerCM mainApp) {
        this.mainApp = mainApp;
    }
}