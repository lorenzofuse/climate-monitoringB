package com.climatemonitoring;

import com.climatemonitoring.controller.MainController;

import com.climatemonitoring.controller.*;
import com.climatemonitoring.service.ClimateMonitoringService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientCM extends Application {
    private static ClimateMonitoringService service;
    private LoginController loginController;
    private MainController mainController;
    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("ClimateMonitoring");

        if (initRMIService()) {
            initRootLayout();
            showLoginView();
        } else {
            System.err.println("Impossibile inizializzare il servizio RMI");
            System.exit(1);
        }
    }

    private boolean initRMIService() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (ClimateMonitoringService) registry.lookup("ClimateMonitoringService");
            System.out.println("Connessione RMI stabilita con successo");

            // Inizializzazione dei controller dopo aver stabilito la connessione
            loginController = new LoginController(service);
            mainController = new MainController(service);

            return true;
        } catch (Exception e) {
            System.err.println("Errore durante la connessione RMI: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(getClass().getResource("/fxml/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Errore durante l'inizializzazione del layout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public MainController showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            BorderPane mainView = loader.load();
            rootLayout.setCenter(mainView);

            MainController controller = loader.getController();
            controller.setMainApp(this);

            return controller;
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento della vista principale: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            BorderPane loginView = loader.load();
            rootLayout.setCenter(loginView);

            LoginController controller = loader.getController();
            controller.setMainApp(this);

            if (!primaryStage.getScene().getStylesheets().contains(getClass().getResource("/fxml/styles.css").toExternalForm())) {
                primaryStage.getScene().getStylesheets().add(getClass().getResource("/fxml/styles.css").toExternalForm());
            }
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento della vista di login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ClimateMonitoringService getService() {
        return service;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}