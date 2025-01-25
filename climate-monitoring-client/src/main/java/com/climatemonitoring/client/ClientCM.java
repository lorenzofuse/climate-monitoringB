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

public class ClientCM extends Application {
    private static ClimateMonitoringService service;
    private Stage primaryStage;
    private BorderPane rootLayout;

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

    private void initRootLayout()  throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        scene.getStylesheets().add(getClass().getResource("/fxml/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

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

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
