package com.climatemonitoring.client.controller;

import com.climatemonitoring.client.ClientCM;
import com.climatemonitoring.common.model.OperatoriRegistrati;
import com.climatemonitoring.common.service.ClimateMonitoringService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.sql.SQLException;


/**
 * Controller per la gestione del login e della registrazione degli operatori
 * nell'applicazione di Climate Monitoring.
 *
 * Questa classe gestisce l'autenticazione degli utenti, la registrazione di nuovi operatori
 * e le interazioni con l'interfaccia utente per il processo di login.
 *
 * @author Fusè Lorenzo 753168
 * @author Ciminella Alessandro 753369
 * @author Dragan Cosmin 754427
 */
public class LoginController {

    /**
     * Bottone per aprire la schermata di registrazione di un nuovo operatore.
     * Consente agli utenti di creare un nuovo account nel sistema.
     */
    @FXML public Button registerButton;

    /**
     * Campo di testo per l'inserimento dell'identificativo utente durante il login.
     * Raccoglie l'username dell'operatore che sta tentando di accedere.
     */
    @FXML private TextField userIdField;

    /**
     * Campo per l'inserimento della password in modo sicuro.
     * Nasconde i caratteri durante la digitazione per proteggere la privacy.
     */
    @FXML private PasswordField passwordField;

    /**
     * Bottone per confermare il processo di login.
     * Avvia l'autenticazione quando l'utente fa clic.
     */
    @FXML private Button loginButton;

    /**
     * Bottone per accedere come utente ospite (cittadino non registrato).
     * Permette l'accesso limitato alle funzionalità del sistema.
     */
    @FXML private Button guestButton;

    /**
     * Riferimento all'applicazione principale client.
     * Utilizzato per gestire la navigazione tra diverse schermate.
     */
    private ClientCM mainApp;

    /**
     * Servizio per le operazioni remote di Climate Monitoring.
     * Gestisce le chiamate al server per autenticazione e registrazione.
     */
    private  ClimateMonitoringService service;


    /**
     * Costruttore di default per il controller di login.
     * Lasciato vuoto per consentire l'inizializzazione di JavaFX.
     */
    public LoginController(){
    }

    /**
     * Imposta il servizio per le operazioni di Climate Monitoring.
     *
     * @param service Servizio da utilizzare per le operazioni remote
     * @throws IllegalArgumentException se il servizio fornito è nullo
     */
    public void setService(ClimateMonitoringService service) {
        if (service == null) {
            throw new IllegalArgumentException("ClimateMonitoringService cannot be null");
        }
        this.service = service;
    }

    /**
     * Imposta il riferimento all'applicazione principale.
     *
     * @param mainApp Istanza dell'applicazione client principale
     * @throws IllegalArgumentException se l'applicazione principale è nulla
     */
    public void setMainApp(ClientCM mainApp) {
        if (mainApp == null) {
            throw new IllegalArgumentException("MainApp cannot be null");
        }
        this.mainApp = mainApp;
    }

    /**
     * Metodo di inizializzazione chiamato da JavaFX dopo il caricamento del FXML.
     * Configura i listener per i bottoni di login, accesso ospite e registrazione.
     */
    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        guestButton.setOnAction(event -> handleCittadinoNonRegistrato());
        registerButton.setOnAction(event -> mostraDialogRegistrazione());
    }

    /**
     * Gestisce il processo di autenticazione dell'operatore.
     *
     * Verifica:
     * - Inizializzazione del servizio e dell'applicazione
     * - Campi username e password non vuoti
     * - Credenziali valide tramite chiamata al servizio remoto
     *
     * Mostra avvisi in caso di errori durante il processo di login.
     */
    @FXML
    private void handleLogin() {

        if (service == null) {
            showAlert(Alert.AlertType.ERROR, "Errore di Sistema","Servizio non inizializzato", "Si è verificato un errore di inizializzazione del servizio.");
            return;
        }

        if (mainApp == null) {
            showAlert(Alert.AlertType.ERROR, "Errore di Sistema", "Applicazione non inizializzata", "Si è verificato un errore di inizializzazione dell'applicazione.");
            return;
        }

        String userId = userIdField.getText().trim();
        String password = passwordField.getText().trim();

        if (userId.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Errore di Login", "Campi vuoti", "Inserisci sia username che password.");
            return;
        }

        try {
            boolean autentica = service.autenticaOperatore(userId, password);
            if (autentica) {
                OperatoriRegistrati user = service.getUserById(userId);
                if (user != null) {
                    MainController mainController = mainApp.mainView();
                    mainController.setCurrentUser(user);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Errore di Login", "Utente non trovato", "Impossibile recuperare i dati dell'utente.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Errore di Login", "Credenziali non valide", "Username o password non corretti.");
            }
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di Connessione", "Errore del Server", "Si è verificato un errore durante il login: " + e.getMessage());
        }
    }

    /**
     * Gestisce l'accesso come cittadino non registrato.
     * Apre la vista principale dell'applicazione senza autenticazione.
     */
    @FXML
    private void handleCittadinoNonRegistrato() {
        mainApp.mainView();
    }


    /**
     * Mostra un dialog per la registrazione di un nuovo operatore.
     *
     * Raccoglie:
     * - Dati anagrafici (nome, cognome)
     * - Informazioni di contatto (codice fiscale, email)
     * - Credenziali di accesso (user ID, password)
     *
     * Esegue validazioni sui dati inseriti prima della registrazione.
     */
    @FXML
    private void mostraDialogRegistrazione() {
        Dialog<OperatoriRegistrati> dialog = new Dialog<>();
        dialog.setTitle("Registrazione nuovo operatore");
        dialog.setHeaderText("Inserisci i tuoi dati per registrarti");

        ButtonType registraBtn = new ButtonType("Registra", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registraBtn, ButtonType.CANCEL);

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
            if (dialogButton == registraBtn) {
                try {
                    String nome = nomeField.getText();
                    String cognome = cognomeField.getText();
                    String codiceFiscale = codiceFiscaleField.getText();
                    String email = emailField.getText();
                    String userId = newUserIdField.getText();
                    String password = newPasswordField.getText();


                    if (!isValidNome(nome)) {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Nome non valido", "Il nome deve contenere almeno 2 caratteri e solo lettere.");
                        return null;
                    }

                    if (!isValidCognome(cognome)) {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Cognome non valido", "Il cognome deve contenere almeno 2 caratteri e solo lettere.");
                        return null;
                    }

                    if (!isValidCF(codiceFiscale)) {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Codice Fiscale non valido", "Inserisci un codice fiscale valido.");
                        return null;
                    }

                    if (!isValidEmail(email)) {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Email non valida", "Inserisci un'email valida.");
                        return null;
                    }

                    if (!isValidUserId(userId)) {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "User ID non valido", "L'User ID deve contenere almeno 5 caratteri alfanumerici.");
                        return null;
                    }

                    if (!isValidPassword(password)) {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Password non valida", "La password deve essere lunga almeno 8 caratteri e contenere lettere, numeri e un carattere speciale.");
                        return null;
                    }

                    boolean registrazioneSucc = service.registrazione(nome, cognome, codiceFiscale, email, userId, password);
                    if (registrazioneSucc) {
                        showAlert(Alert.AlertType.INFORMATION, "Registrazione Completata", "Registrazione avvenuta con successo.", "Puoi ora effettuare il login con le tue credenziali.");
                        return new OperatoriRegistrati(0, nome, cognome, codiceFiscale, email, userId, password);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Registrazione fallita", "Si è verificato un errore durante la registrazione.");
                        return null;
                    }
                } catch (SQLException | RemoteException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore di Connessione", "Errore del Server", "Si è verificato un errore durante la registrazione: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Convalida il nome inserito durante la registrazione.
     *
     * @param nome Nome da validare
     * @return true se il nome è valido (almeno 2 caratteri, solo lettere)
     */
    private boolean isValidNome(String nome) {
        return nome != null && nome.matches("[A-Za-z]{2,}");
    }

    /**
     * Convalida il cognome inserito durante la registrazione.
     *
     * @param cognome Cognome da validare
     * @return true se il cognome è valido (almeno 2 caratteri, solo lettere)
     */
    private boolean isValidCognome(String cognome) {
        return cognome != null && cognome.matches("[A-Za-z]{2,}");
    }

    /**
     * Convalida il codice fiscale inserito durante la registrazione.
     *
     * @param codiceFiscale Codice fiscale da validare
     * @return true se il codice fiscale è valido (16 caratteri alfanumerici)
     */
    private boolean isValidCF(String codiceFiscale) {
        return codiceFiscale != null && codiceFiscale.matches("^[A-Z0-9]{16}$");
    }

    /**
     * Convalida l'email inserita durante la registrazione.
     *
     * @param email Email da validare
     * @return true se l'email è in un formato valido
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Convalida l'user ID inserito durante la registrazione.
     *
     * @param userId User ID da validare
     * @return true se l'user ID è valido (almeno 5 caratteri alfanumerici)
     */
    private boolean isValidUserId(String userId) {
        return userId != null && userId.matches("[A-Za-z0-9_]{5,}");
    }

    /**
     * Convalida la password inserita durante la registrazione.
     *
     * Requisiti:
     * - Lunghezza minima 8 caratteri
     * - Contiene almeno una lettera minuscola
     * - Contiene almeno una lettera maiuscola
     * - Contiene almeno un numero
     * - Contiene almeno un carattere speciale
     *
     * @param password Password da validare
     * @return true se la password soddisfa tutti i criteri di sicurezza
     */
    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    /**
     * Metodo di utilità per mostrare messaggi di alert all'utente.
     *
     * @param alertType Tipo di alert (INFORMATION, WARNING, ERROR, ecc.)
     * @param title Titolo della finestra di alert
     * @param header Intestazione del messaggio
     * @param content Contenuto dettagliato del messaggio
     */
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}