package com.climatemonitoring.client.controller;

import com.climatemonitoring.client.ClientCM;
import com.climatemonitoring.common.model.OperatoriRegistrati;
import com.climatemonitoring.common.service.ClimateMonitoringService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class LoginController {
    @FXML public Button registerButton;
    @FXML private TextField userIdField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button guestButton;

    private ClientCM mainApp;
    private  ClimateMonitoringService service;

    public LoginController(){
    }

    public void setService(ClimateMonitoringService service) {
        if (service == null) {
            throw new IllegalArgumentException("ClimateMonitoringService cannot be null");
        }
        this.service = service;
    }

    public void setMainApp(ClientCM mainApp) {
        if (mainApp == null) {
            throw new IllegalArgumentException("MainApp cannot be null");
        }
        this.mainApp = mainApp;
    }


    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        guestButton.setOnAction(event -> handleGuestLogin());
        registerButton.setOnAction(event -> showRegistrationDialog());
    }

    @FXML
    private void handleLogin() {

        if (service == null) {
            showAlert(Alert.AlertType.ERROR, "Errore di Sistema",
                    "Servizio non inizializzato",
                    "Si è verificato un errore di inizializzazione del servizio.");
            return;
        }

        if (mainApp == null) {
            showAlert(Alert.AlertType.ERROR, "Errore di Sistema",
                    "Applicazione non inizializzata",
                    "Si è verificato un errore di inizializzazione dell'applicazione.");
            return;
        }

        String userId = userIdField.getText().trim();
        String password = passwordField.getText().trim();

        if (userId.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Errore di Login", "Campi vuoti", "Inserisci sia username che password.");
            return;
        }

        try {
            boolean authenticated = service.autenticaOperatore(userId, password);
            if (authenticated) {
                OperatoriRegistrati user = service.getUserById(userId);
                if (user != null) {
                    MainController mainController = mainApp.showMainView();
                    mainController.setCurrentUser(user);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Errore di Login",
                            "Utente non trovato", "Impossibile recuperare i dati dell'utente.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Errore di Login",
                        "Credenziali non valide", "Username o password non corretti.");
            }
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di Connessione",
                    "Errore del Server", "Si è verificato un errore durante il login: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuestLogin() {
        mainApp.showMainView();
    }

    @FXML
    private void showRegistrationDialog() {
        Dialog<OperatoriRegistrati> dialog = new Dialog<>();
        dialog.setTitle("Registrazione nuovo operatore");
        dialog.setHeaderText("Inserisci i tuoi dati per registrarti");

        ButtonType registraButtonType = new ButtonType("Registra", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registraButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome");
        TextField cognomeField = new TextField();
        cognomeField.setPromptText("Cognome");
        TextField codiceFiscaleField = new TextField();
        codiceFiscaleField.setPromptText("Codice Fiscale");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField newUserIdField = new TextField();
        newUserIdField.setPromptText("User ID");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Password");

        content.getChildren().addAll(
                new Label("Nome:"), nomeField,
                new Label("Cognome:"), cognomeField,
                new Label("Codice Fiscale:"), codiceFiscaleField,
                new Label("Email:"), emailField,
                new Label("User ID:"), newUserIdField,
                new Label("Password:"), newPasswordField
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registraButtonType) {
                try {
                    String nome = nomeField.getText();
                    String cognome = cognomeField.getText();
                    String codiceFiscale = codiceFiscaleField.getText();
                    String email = emailField.getText();
                    String userId = newUserIdField.getText();
                    String password = newPasswordField.getText();

                    if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() ||
                            email.isEmpty() || userId.isEmpty() || password.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione",
                                "Campi vuoti", "Compila tutti i campi per registrarti.");
                        return null;
                    }

                    boolean registrationSuccess = service.registrazione(nome, cognome, codiceFiscale,
                            email, userId, password);
                    if (registrationSuccess) {
                        showAlert(Alert.AlertType.INFORMATION, "Registrazione Completata",
                                "Registrazione avvenuta con successo",
                                "Puoi ora effettuare il login con le tue credenziali.");
                        return new OperatoriRegistrati(0, nome, cognome, codiceFiscale,
                                email, userId, password);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione",
                                "Registrazione fallita",
                                "Si è verificato un errore durante la registrazione.");
                        return null;
                    }
                } catch (RemoteException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore di Connessione",
                            "Errore del Server",
                            "Si è verificato un errore durante la registrazione: " + e.getMessage());
                    return null;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}