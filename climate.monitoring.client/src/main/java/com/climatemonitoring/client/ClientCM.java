package com.climatemonitoring.client;

import com.climatemonitoring.client.controller.LoginController;
import com.climatemonitoring.client.controller.MainController;
import com.climatemonitoring.common.service.ClimateMonitoringService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * La classe ClientCM è l'applicazione JavaFX principale per il client di Climate Monitoring.
 * Gestisce l'inizializzazione del servizio RMI, del layout principale e delle viste dell'applicazione.
 *
 * Responsabilità principali:
 * - Stabilire la connessione RMI al Servizio di Monitoraggio Climatico
 * - Configurare la finestra principale dell'applicazione
 * - Caricare e gestire diverse viste (login, principale)
 * - Gestire l'inizializzazione dell'applicazione e gli scenari di errore
 *
 * @author Fusè Lorenzo 753168
 * @author Ciminella Alessandro 753369
 * @author Dragan Cosmin 754427
 */

public class ClientCM extends Application {

    /**
     * Servizio RMI per le operazioni di monitoraggio climatico.
     * Fornisce capacità di invocazione remota dei metodi per la comunicazione client-server.
     */
    private static ClimateMonitoringService service;

    /**
     * Finestra principale dell'applicazione JavaFX.
     * Funge da finestra principale dell'applicazione.
     */
    private Stage primaryStage;

    /**
     * Layout radice dell'applicazione.
     * Funge da contenitore principale per gli altri componenti dell'interfaccia utente.
     */
    private BorderPane rootLayout;

    /**
     * Inizializza l'applicazione JavaFX configurando il servizio RMI, il layout principale e la vista di login.
     *
     * Questo metodo viene chiamato automaticamente dal runtime JavaFX all'avvio dell'applicazione.
     * Esegue i seguenti passaggi principali:
     * - Imposta il titolo della finestra principale
     * - Inizializza il servizio RMI
     * - Crea il layout principale
     * - Carica la vista di login
     *
     * @param stage La finestra principale per l'applicazione
     */
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("ClimateMonitoring");

        try{
            initRMIService();
            initRootLayout();
            loginView();
        }catch(Exception e){
            showError("Errore di Inizializzazione", "Impossibile avviare l'applicazione", "Dettaglio: " + e.getMessage());
            System.exit(1);
        }
    }


    /**
     * Stabilisce la connessione al registro RMI e cerca il Servizio di Monitoraggio Climatico.
     *
     * Questo metodo:
     * - Si connette al registro RMI locale sulla porta 1099
     * - Recupera il ClimateMonitoringService
     * - Convalida la disponibilità del servizio
     *
     * @throws Exception se il servizio RMI non può essere inizializzato o trovato
     */
    private void initRMIService() throws Exception {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (ClimateMonitoringService) registry.lookup("ClimateMonitoringService");


            if(service==null){
                throw new Exception("Servizio RMI non trovato nel registry");
            }

            System.out.println("Connessione RMI stabilita con successo");
        } catch (Exception e) {
            System.err.println("Errore durante l'inizializzazione del servizio RMI: " + e.getMessage());
            throw new Exception("Errore durante la connessione RMI: " + e.getMessage());
        }
    }


    /**
     * Inizializza il layout principale dell'applicazione.
     *
     * Questo metodo:
     * - Carica il layout principale FXML
     * - Imposta la scena con il CSS associato
     * - Visualizza la finestra principale
     *
     * @throws IOException se il caricamento FXML fallisce
     */
    private void initRootLayout()  throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        scene.getStylesheets().add(getClass().getResource("/fxml/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    /**
     * Carica e configura la vista di login nel layout principale.
     *
     * Questo metodo:
     * - Carica l'FXML della vista di login
     * - Imposta la vista di login al centro del layout principale
     * - Configura il controller di login con l'applicazione principale e il servizio
     * - Applica lo stile CSS
     */
    public void loginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            BorderPane loginView = loader.load();
            rootLayout.setCenter(loginView);

            LoginController controller = loader.getController();
            controller.setMainApp(this);
            controller.setService(service);

            if (!primaryStage.getScene().getStylesheets().contains(getClass().getResource("/fxml/styles.css").toExternalForm())) {
                primaryStage.getScene().getStylesheets().add(getClass().getResource("/fxml/styles.css").toExternalForm());
            }
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento della vista di login: " + e.getMessage());
            showError("Errore di Caricamento", "Impossibile caricare la vista di login",
                    e.getMessage());
        }
    }


    /**
     * Carica e configura la vista principale dell'applicazione.
     *
     * Questo metodo:
     * - Carica l'FXML della vista principale
     * - Imposta la vista principale al centro del layout principale
     * - Configura il controller principale con l'applicazione e il servizio
     *
     * @return MainController il controller per la vista principale, o null se il caricamento fallisce
     */
    public MainController mainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            BorderPane mainView = loader.load();
            rootLayout.setCenter(mainView);

            MainController controller = loader.getController();
            controller.setMainApp(this);
            controller.setService(service);

            return controller;
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento della vista principale: " + e.getMessage());
            showError("Errore di Caricamento", "Impossibile caricare la vista principale",
                    e.getMessage());
            return null;
        }
    }

    /**
     * Visualizza una finestra di dialogo con informazioni dettagliate sull'errore.
     *
     * @param title il titolo della finestra di dialogo dell'errore
     * @param header il testo dell'intestazione che descrive l'errore
     * @param content messaggio di errore dettagliato
     */
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Metodo principale per avviare l'applicazione JavaFX.
     *
     * @param args argomenti della riga di comando (non utilizzati in questa applicazione)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
