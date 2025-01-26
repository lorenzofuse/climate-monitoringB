package com.climatemonitoring.server;

import com.climatemonitoring.server.controller.ServerLogin;
import com.climatemonitoring.server.server.ClimateMonitoringServiceImpl;
import com.climatemonitoring.server.util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
/**
 * Classe principale per l'applicazione server di Climate Monitoring.
 *
 * Questa classe gestisce l'avvio e l'inizializzazione del server RMI per
 * il monitoraggio climatico, implementando un'interfaccia JavaFX per
 * l'interazione grafica e la configurazione del server.
 *
 * Responsabilità principali:
 * - Inizializzare l'interfaccia grafica del server
 * - Gestire l'avvio del server RMI
 * - Configurare la connessione al database
 * - Gestire il ciclo di vita del server (avvio, esecuzione, arresto)
 *
 * Flusso di esecuzione:
 * 1. Caricamento del layout root
 * 2. Visualizzazione della schermata di login
 * 3. Avvio del server RMI tramite {@link #startRMIServer(DatabaseManager)}
 * 4. Gestione della chiusura con pulizia delle risorse
 *
 * @author
 * @author
 * @author
 */
public class ServerCM extends Application {
    /**
     * Stage principale dell'applicazione JavaFX.
     * Rappresenta la finestra principale in cui verranno caricati i vari layout.
     */
    private Stage primaryStage;

    /**
     * Layout principale che ospiterà i vari componenti dell'interfaccia.
     * Utilizza un BorderPane per organizzare gli elementi grafici.
     */
    private BorderPane rootLayout;

    /**
     * Gestore del database per le operazioni di persistenza.
     * Mantiene la connessione e gestisce le interazioni con l'archivio dati.
     */
    private DatabaseManager dbManager;

    /**
     * Flag per verificare se il server RMI è già stato avviato.
     * Previene l'avvio multiplo accidentale del server.
     */
    private static boolean rmiStarted = false;


    private ClimateMonitoringServiceImpl serviceImpl;

    /**
     * Metodo principale di avvio dell'applicazione JavaFX.
     *
     * Configura la finestra principale, imposta il titolo e gestisce
     * l'inizializzazione del layout e della vista di login.
     *
     * In caso di errori durante l'inizializzazione:
     * - Visualizza un messaggio di errore
     * - Termina l'applicazione
     *
     * @param stage Stage principale fornito dall'ambiente JavaFX
     */
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("ClimateMonitoring Server");

        try {
            initRootLayout();
            loginView();
        } catch (Exception e) {
            System.err.println("Errore durante l'inizializzazione:");
            e.printStackTrace();
            errore("Errore di Inizializzazione", "Impossibile avviare il server", "Dettaglio: " + e.getMessage());
            System.exit(1);
        }
    }
    /**
     * Inizializza il layout principale dell'applicazione server.
     *
     * Questo metodo svolge diverse funzioni cruciali:
     * 1. Carica il file FXML per il layout root
     * 2. Configura la scena principale dell'applicazione
     * 3. Applica i fogli di stile CSS per l'interfaccia grafica
     *
     * Gestisce inoltre gli scenari di errore, come:
     * - Risorse FXML non trovate
     * - Problemi di caricamento del layout
     *
     * @throws IOException Se ci sono problemi nel caricamento delle risorse FXML
     * @throws RuntimeException Per errori critici durante l'inizializzazione
     */
    private void initRootLayout() throws IOException {
        try {
            URL resourceUrl = getClass().getResource("/fxml/RootLayout.fxml");
            if (resourceUrl == null) {
                throw new IOException("Impossibile trovare RootLayout.fxml nel classpath");
            }
            System.out.println("Loading FXML from: " + resourceUrl);

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            rootLayout = loader.load();

            URL cssUrl = getClass().getResource("/fxml/style.css");
            if (cssUrl == null) {
                System.err.println("Warning: styles.css non trovato");
            } else {
                Scene scene = new Scene(rootLayout);
                scene.getStylesheets().add(cssUrl.toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.show();
            }
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento del layout:");
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * Configura e visualizza la schermata di login del server.
     *
     * Operazioni principali:
     * - Carica la vista di login dal file FXML
     * - Imposta la vista al centro del layout root
     * - Inizializza il controller della schermata di login
     *
     * Gestisce in modo robusto potenziali errori, tra cui:
     * - File FXML non trovati
     * - Errori di caricamento dell'interfaccia
     * - Problemi di inizializzazione del controller
     *
     * @throws IOException In caso di problemi di caricamento della vista
     * @throws RuntimeException Se il controller non può essere inizializzato
     */
    public void loginView() {
        try {
            URL loginViewUrl = getClass().getResource("/fxml/ServerLogin.fxml");
            if (loginViewUrl == null) {
                throw new IOException("Impossibile trovare ServerLogin.fxml nel classpath");
            }
            System.out.println("Loading login view from: " + loginViewUrl);

            FXMLLoader loader = new FXMLLoader(loginViewUrl);
            BorderPane loginView = loader.load();
            rootLayout.setCenter(loginView);

            ServerLogin controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Controller non inizializzato correttamente");
            }
            controller.setMainApp(this);
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento della vista di login:");
            e.printStackTrace();
            errore("Errore di Caricamento",
                    "Impossibile caricare la vista di login",
                    e.getMessage());
        }
    }

    /**
     * Avvia il server RMI per il servizio di monitoraggio climatico.
     *
     * Operazioni principali:
     * - Crea un registry RMI sulla porta standard 1099
     * - Registra l'implementazione del servizio ClimateMonitoring
     * - Gestisce eventuali errori durante l'avvio
     *
     * @param dbManager Gestore del database per le operazioni di persistenza
     * @throws RemoteException In caso di problemi durante la configurazione RMI
     */
    public void startRMIServer(DatabaseManager dbManager) {
        if (rmiStarted) {
            errore("Server già avviato", "Il server RMI è già in esecuzione", "Non è possibile avviare più istanze del server RMI");
            return;
        }

        try {
            this.dbManager = dbManager;
            LocateRegistry.createRegistry(1099);
            ClimateMonitoringServiceImpl climateService = new ClimateMonitoringServiceImpl(dbManager);
            Naming.rebind("rmi://localhost/ClimateMonitoringService", climateService);
            rmiStarted = true;

            successo("Server Avviato", "Il server RMI è stato avviato con successo", "In ascolto sulla porta 1099");

        } catch (Exception e) {
            errore("Errore RMI", "Impossibile avviare il server RMI", e.getMessage());
        }
    }


    /**
     * Visualizza un messaggio di errore mediante un dialogo JavaFX.
     *
     * Questo metodo standardizza la presentazione degli errori
     * nell'applicazione, garantendo:
     * - Informazioni chiare e concise
     * - Un'interfaccia utente coerente per la gestione degli errori
     *
     * Utilizza un Alert di tipo ERROR per attirare l'attenzione
     * dell'utente sui problemi critici.
     *
     * @param title Titolo della finestra di dialogo
     * @param header Intestazione che riassume il tipo di errore
     * @param content Descrizione dettagliata dell'errore
     */
    private void errore(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    /**
     * Visualizza un messaggio informativo di successo mediante un dialogo JavaFX.
     *
     * Complementare al metodo {@link #errore()}, questo metodo:
     * - Fornisce feedback positivi all'utente
     * - Usa un Alert di tipo INFORMATION per messaggi non critici
     * - Mantiene un'interfaccia utente coerente e professionale
     *
     * Utile per confermare operazioni completate con successo,
     * come l'avvio del server o operazioni critiche.
     *
     * @param title Titolo della finestra di dialogo
     * @param header Intestazione che riassume l'evento positivo
     * @param content Descrizione dettagliata del successo
     */
    private void successo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Metodo di chiusura dell'applicazione.
     *
     * Gestisce la pulizia delle risorse:
     * - Disconnessione dal registro RMI
     * - Chiusura della connessione al database
     * - Rilascio delle risorse di sistema
     */
    @Override
    public void stop() {
        try {
            if (dbManager != null && rmiStarted) {
                try {
                    Registry registry = LocateRegistry.getRegistry(1099);
                    registry.unbind("ClimateMonitoringService");
                } catch (Exception e) {
                    System.err.println("Warning durante l'unbind del servizio: " + e.getMessage());
                }

                try {
                    if (serviceImpl != null) {
                        UnicastRemoteObject.unexportObject(serviceImpl, true);
                    }
                } catch (Exception e) {
                    System.err.println("Warning durante l'unexport del servizio: " + e.getMessage());
                }

                rmiStarted = false;
                serviceImpl = null;

                dbManager.closeConnection();
                dbManager = null;

                System.out.println("Server RMI arrestato con successo");
            }
        } catch (Exception e) {
            System.err.println("Errore durante la chiusura del server: " + e.getMessage());
        }finally {
            System.exit(0);
        }

    }

    /**
     * Punto di ingresso principale dell'applicazione.
     * Lancia l'applicazione JavaFX.
     *
     * @param args Argomenti della riga di comando (non utilizzati in questo contesto)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
