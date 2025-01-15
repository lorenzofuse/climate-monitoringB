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
import java.rmi.registry.LocateRegistry;

public class ServerCM extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private DatabaseManager dbManager;
    private static boolean rmiStarted = false;

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

    private void errore(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void successo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        if (dbManager != null) {
            dbManager.closeConnection();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
