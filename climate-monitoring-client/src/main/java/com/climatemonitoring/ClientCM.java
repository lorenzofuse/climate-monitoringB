package com.climatemonitoring;


import com.climatemonitoring.service.ClimateMonitoringService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientCM {
    private ClimateMonitoringService service;
//    private LoginController loginController;
//    private MainController mainController;

    public void start() {
        try {
            // Connessione al servizio RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (ClimateMonitoringService) registry.lookup("ClimateMonitoringService");

            // Inizializzazione dei controller
//            loginController = new LoginController(service);
//            mainController = new MainController(service);
//
//            // Avvio dell'interfaccia utente
//            loginController.showLoginView();
        } catch (Exception e) {
            System.err.println("Errore durante l'avvio del client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ClientCM().start();
    }
}