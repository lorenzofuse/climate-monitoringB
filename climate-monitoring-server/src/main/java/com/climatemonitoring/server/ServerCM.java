package com.climatemonitoring.server;

import com.climatemonitoring.server.server.ClimateMonitoringServiceImpl;
import com.climatemonitoring.server.util.DatabaseManager;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class ServerCM {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DatabaseManager dbManager = null;

        while (dbManager == null) {
            try {

                System.out.println("=== Configurazione Database ===\n");
                System.out.print("Inserisci host DB (es. localhost:5432): "); //da rimuovere quando si consegna l'es
                String dbHost = scanner.nextLine().trim();

                System.out.print("Inserisci username DB: ");
                String dbUser = scanner.nextLine().trim();

                System.out.print("Inserisci password DB: ");
                String dbPassword = scanner.nextLine().trim();

                dbManager = DatabaseManager.initialize(dbHost, dbUser, dbPassword);

                if (!dbManager.testConnection()) {
                    System.err.println("Errore: Impossibile connettersi al database. Verificare le credenziali.");
                    dbManager = null; // Reset per riprovare
                    continue;
                }

                System.out.println("Connessione al database stabilita con successo!");

            } catch (Exception e) {
                System.err.println("Errore durante la connessione al database: " + e.getMessage());
                System.out.println("Vuoi riprovare? (s/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                    System.out.println("Avvio del server annullato.");
                    return;
                }
            }
        }

        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("Registry RMI avviato sulla porta 1099");
            ClimateMonitoringServiceImpl climateService = new ClimateMonitoringServiceImpl(dbManager);
            Naming.rebind("rmi://localhost/ClimateMonitoringService", climateService);
            System.out.println("Servizio ClimateMonitoring registrato nel Registry RMI");

            System.out.println("\nServer in esecuzione. Premi Ctrl+C per terminare.");

        } catch (Exception e) {
            System.err.println("Errore durante l'avvio del server RMI: " + e.getMessage());
            e.printStackTrace();
        }finally{
            dbManager.closeConnection();
        }
    }
}