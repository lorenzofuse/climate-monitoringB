package com.climatemonitoring.client.controller;

import com.climatemonitoring.client.ClientCM;
import com.climatemonitoring.common.model.CoordinateMonitoraggio;
import com.climatemonitoring.common.service.ClimateMonitoringService;
import com.climatemonitoring.common.model.OperatoriRegistrati;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public class MainController {


    @FXML private TextField searchField;
    @FXML private TextField stateField;
    @FXML public TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private Button searchButton;
    @FXML private TextArea resultArea;
    @FXML private TextArea coordinateResultArea;
    @FXML private Button logoutButton;
    @FXML private Tab operatorTab;
    @FXML private TextField paeseField;
    @FXML private TextArea paeseResultArea;
    @FXML public TextArea operatorResultArea;
    @FXML private TabPane mainTabPane;
    @FXML private ComboBox<CoordinateMonitoraggio> areaComboBox;
    @FXML private ComboBox<CoordinateMonitoraggio> areaComboBox2 = new ComboBox<>();
    @FXML private TextField areaNameField;
    @FXML private TextField areaStateField;
    @FXML private TextArea climateDataResultArea;
    @FXML private TextField monitoringAreaNameField;
    @FXML private TextField monitoringAreaStatusField;
    @FXML private TextArea monitoringAreaResultArea;
    @FXML private Button viewMonitoringAreaButton;
    @FXML private Tab visualizzaAreaCentroTab;

    private OperatoriRegistrati currentUser;
    private ClientCM mainApp;
    private ClimateMonitoringService service;


    public MainController() { }

    public void setService(ClimateMonitoringService service) {
        this.service = service;
    }

    public void setMainApp(ClientCM mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        searchButton.setOnAction(event -> handlecercaAreaGeograficaNome());
        logoutButton.setOnAction(event -> handleLogout());

        if (operatorTab != null) {
            operatorTab.setDisable(true);
        }
        updateAreaComboBox();
    }

    public void setCurrentUser(OperatoriRegistrati user) {
        this.currentUser = user;
        if (user != null && operatorTab != null) {
            operatorTab.setDisable(false);
            updateAreaComboBox();
        } else {
            operatorTab.setDisable(true);
        }
    }

    @FXML
    private void handlecercaAreaGeograficaNome() {
        String cityName = searchField.getText().trim();
        String stateName = stateField.getText().trim();

        if (cityName.isEmpty() || stateName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Errore di Ricerca", "Campi vuoti", "Inserisci sia la città che lo stato.");
            return;
        }

        try {
            List<CoordinateMonitoraggio> results = service.cercaAreaGeograficaNome(cityName, stateName);

            if (results.isEmpty()) {
                resultArea.setText("Nessun risultato trovato\nInserisci il nome di una Città e Stato valido");
            } else {
                StringBuilder sb = new StringBuilder();
                for (CoordinateMonitoraggio area : results) {
                    sb.append("Città: ").append(area.getNomeCitta())
                            .append("\nStato: ").append(area.getStato())
                            .append("\nPaese: ").append(area.getPaese())
                            .append("\nLatitudine: ").append(area.getLatitudine())
                            .append("\nLongitudine: ").append(area.getLongitudine())
                            .append("\n\n");
                }
                resultArea.setText(sb.toString());
            }
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di Connessione", "Errore del Server",
                    "Si è verificato un errore durante la ricerca: " + e.getMessage());
        }
    }


    @FXML
    private void handlecercaAreaGeograficaCoordinate() {
        String latStr = latitudeField.getText().trim();
        String lonStr = longitudeField.getText().trim();

        if (latStr.isEmpty() || lonStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Errore di input", "Campi vuoti", "Inserisci sia la latitudine che la longitudine");
            return;
        }

        try {

            double latitudine = parseCoordinate(latStr);
            double longitudine = parseCoordinate(lonStr);


            if (!isValidLatitude(latitudine) || !isValidLongitude(longitudine)) {
                showAlert(Alert.AlertType.ERROR, "Errore di input", "Coordinate non valide", "La latitudine deve essere tra -90 e 90, la longitudine tra -180 e 180.");
                return;
            }

            List<CoordinateMonitoraggio> results = service.cercaAreaGeograficaCoordinate(latitudine, longitudine);

            displayResCoordinate(results, latitudine, longitudine);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di input", "Formato non valido", "Inserisci le coordinate nel formato corretto (es: 15.03201 o 45.82832).");
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di Connessione", "Errore del Server", "Si è verificato un errore durante la ricerca: " + e.getMessage());
        }
    }

    private double parseCoordinate(String coord) {
        // gestisco i != formati
        coord = coord.replace(',', '.').trim();
        return Double.parseDouble(coord);
    }

    private boolean isValidLatitude(double lat) {
        return lat >= -90 && lat <= 90;
    }

    private boolean isValidLongitude(double lon) {
        return lon >= -180 && lon <= 180;
    }

    private void displayResCoordinate(List<CoordinateMonitoraggio> results, double searchLat, double searchLon) {
        if (results.isEmpty()) {
            coordinateResultArea.setText("Nessun risultato trovato nelle vicinanze delle coordinate specificate.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Risultati vicini a Lat: %.5f, Lon: %.5f\n\n", searchLat, searchLon));

            for (CoordinateMonitoraggio area : results) {
                double distanza = calcolaDistanzaKm(searchLat, searchLon,
                        area.getLatitudine(), area.getLongitudine());

                sb.append(String.format("Città: %s\n", area.getNomeCitta()))
                        .append(String.format("Stato: %s\n", area.getStato()))
                        .append(String.format("Paese: %s\n", area.getPaese()))
                        .append(String.format("Coordinate: %.5f, %.5f\n",
                                area.getLatitudine(), area.getLongitudine()))
                        .append(String.format("Distanza: %.1f km\n\n", distanza));
            }
            coordinateResultArea.setText(sb.toString());
        }
    }

    private double calcolaDistanzaKm(double lat1, double lon1, double lat2, double lon2) {
        // Implementazione della formula di Haversine per calcolare la distanza tra due punti sulla Terra
        final int R = 6371; // Raggio della Terra in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @FXML
    private void handleRicercaPerStato() {
        String paese = paeseField.getText().trim();

        if (paese.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Errore di ricerca", "Campo vuoto", "Inserisci il nome del paese");
            return;
        }

        try {
            List<CoordinateMonitoraggio> ris = service.cercaAreaGeograficaPerPaese(paese);
            if (ris.isEmpty()) {
                paeseResultArea.setText("Nessun risultato trovato.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (CoordinateMonitoraggio area : ris) {
                    sb.append("Città: ").append(area.getNomeCitta())
                            .append("\nStato: ").append(area.getStato())
                            .append("\nPaese: ").append(area.getPaese())
                            .append("\nLatitudine: ").append(area.getLatitudine())
                            .append("\nLongitudine: ").append(area.getLongitudine())
                            .append("\n\n");
                }
                paeseResultArea.setText(sb.toString());
            }
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di Connessione", "Errore del Server",
                    "Si è verificato un errore durante la ricerca: " + e.getMessage());
        }
    }

    @FXML
    private void handleVisualizzaDatiClim() {
        String nome = areaNameField.getText().trim();
        String stato = areaStateField.getText().trim();

        if (nome.isEmpty() || stato.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Errore di ricerca", "Campi vuoti", "Inserisci sia il nome dell'area che lo stato");
            return;
        }
        try {
            String ris = service.visualizzaAreaGeografica(nome, stato);

            if (ris.equals("Area non trovata.")) {
                climateDataResultArea.setText("Nessuna area geografica trovata con i parametri specificati");
            } else {
                climateDataResultArea.setText(ris);
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di validazione",
                    "Parametri non validi", e.getMessage());
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di connessione",
                    "Errore del server",
                    "Si è verificato un errore durante la ricerca: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreaCentroMonitoraggio() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Accesso negato", "Effettua il login come operatore");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crea centro di monitoraggio");
        dialog.setHeaderText("Inserisci i dettagli del centro di monitoraggio");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TextField nomeField = new TextField();
        TextField indirizzoField = new TextField();
        TextField capField = new TextField();
        TextField comuneField = new TextField();
        TextField provinciaField = new TextField();

        content.getChildren().addAll(
                new Label("Nome:"), nomeField,
                new Label("Indirizzo:"), indirizzoField,
                new Label("CAP:"), capField,
                new Label("Comune:"), comuneField,
                new Label("Provincia:"), provinciaField
        );

        dialog.getDialogPane().setContent(content);

        ButtonType createButton = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(createButton, cancelButton);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButton) {
                if (nomeField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Errore", "Campo obbligatorio", "Il nome è obbligatorio");
                    return null;
                }
                try {
                    boolean success = service.creaCentroMonitoraggio(
                            currentUser.getId(),
                            nomeField.getText().trim(),
                            indirizzoField.getText().trim(),
                            capField.getText().trim(),
                            comuneField.getText().trim(),
                            provinciaField.getText().trim()
                    );

                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Successo",
                                "Centro creato", "Il centro è stato creato con successo");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore",
                                "Creazione fallita", "Non è stato possibile creare il centro");
                    }
                } catch (RemoteException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore creazione centro di monitoraggio",
                            "Creazione fallita",
                            "L'utente ha già un centro di monitoraggio");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleCreaArea() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Accesso negato", "Effettua il login come operatore");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crea Area di Interesse");
        dialog.setHeaderText("Inserisci i dettagli dell'area di interesse");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TextField cittaField = new TextField();
        TextField statoField = new TextField();
        TextField latitudineField = new TextField();
        TextField longitudineField = new TextField();

        content.getChildren().addAll(
                new Label("Città:"), cittaField,
                new Label("Stato:"), statoField,
                new Label("Latitudine:"), latitudineField,
                new Label("Longitudine:"), longitudineField
        );

        dialog.getDialogPane().setContent(content);

        ButtonType createButton = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(createButton, cancelButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButton) {
                if (cittaField.getText().trim().isEmpty() || statoField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Errore",
                            "Campi obbligatori", "Città e stato sono obbligatori");
                    return null;
                }

                try {
                    double latitudine = Double.parseDouble(latitudineField.getText().trim());
                    double longitudine = Double.parseDouble(longitudineField.getText().trim());

                    boolean success = service.creaAreaInteresse(
                            currentUser.getId(),
                            cittaField.getText().trim(),
                            statoField.getText().trim(),
                            latitudine,
                            longitudine
                    );

                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Successo",
                                "Area creata", "L'area è stata creata con successo");
                        updateAreaComboBox();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore",
                                "Creazione fallita", "Non è stato possibile creare l'area");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore",
                            "Formato non valido", "Inserisci coordinate numeriche valide");
                } catch (RemoteException e) {
                    if (e.getMessage().contains("non ha un centro di monitoraggio")) {
                        showAlert(Alert.AlertType.ERROR, "Errore",
                                "Centro mancante", "Crea prima un centro di monitoraggio");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore",
                                "Errore di connessione", "Errore durante la creazione: " + e.getMessage());
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void updateAreaComboBox() {
        if (areaComboBox != null && currentUser != null) {
            try {
                List<CoordinateMonitoraggio> aree = service.getAreePerCentroMonitoraggio(currentUser.getId());
                areaComboBox.getItems().clear();
                areaComboBox.getItems().addAll(aree);
            } catch (RemoteException e) {
                showAlert(Alert.AlertType.ERROR, "Errore", "Aggiornamento fallito", "Impossibile aggiornare le aree: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleInserisciParametri() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Accesso negato", "Effettua il login come operatore");
            return;
        }


        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Inserisci Dati Climatici");
        dialog.setHeaderText("Inserisci i parametri climatici per un'area");

        ButtonType insertButtonType = new ButtonType("Inserisci", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<CoordinateMonitoraggio> areaComboBox = new ComboBox<>();
        DatePicker dataPicker = new DatePicker();
        Spinner<Integer> ventoSpinner = new Spinner<>(0, 300, 0);
        Spinner<Integer> umiditaSpinner = new Spinner<>(0, 100, 50);
        Spinner<Integer> pressioneSpinner = new Spinner<>(900, 1100, 1013);
        Spinner<Integer> temperaturaSpinner = new Spinner<>(-50, 50, 20);
        Spinner<Integer> precipitazioniSpinner = new Spinner<>(0, 500, 0);
        Spinner<Integer> altitudineSpinner = new Spinner<>(0, 8000, 0);
        Spinner<Integer> massaGhiacciaiSpinner = new Spinner<>(0, 1000000, 0);
        TextArea noteArea = new TextArea();
        noteArea.setPrefRowCount(3);

        grid.add(new Label("Area:"), 0, 0);
        grid.add(areaComboBox, 1, 0);
        grid.add(new Label("Data:"), 0, 1);
        grid.add(dataPicker, 1, 1);
        grid.add(new Label("Vento (km/h):"), 0, 2);
        grid.add(ventoSpinner, 1, 2);
        grid.add(new Label("Umidità (%):"), 0, 3);
        grid.add(umiditaSpinner, 1, 3);
        grid.add(new Label("Pressione (hPa):"), 0, 4);
        grid.add(pressioneSpinner, 1, 4);
        grid.add(new Label("Temperatura (°C):"), 0, 5);
        grid.add(temperaturaSpinner, 1, 5);
        grid.add(new Label("Precipitazioni (mm):"), 0, 6);
        grid.add(precipitazioniSpinner, 1, 6);
        grid.add(new Label("Altitudine ghiacciai (m):"), 0, 7);
        grid.add(altitudineSpinner, 1, 7);
        grid.add(new Label("Massa ghiacciai (kg):"), 0, 8);
        grid.add(massaGhiacciaiSpinner, 1, 8);
        grid.add(new Label("Note:"), 0, 9);
        grid.add(noteArea, 1, 9);

        dialog.getDialogPane().setContent(grid);

        // Popola il ComboBox con le aree disponibili
        try {
            List<CoordinateMonitoraggio> aree = service.getAreePerCentroMonitoraggio(currentUser.getId());
            areaComboBox.getItems().addAll(aree);
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di caricamento",
                    "Impossibile caricare le aree", "Si è verificato un errore nel caricamento delle aree: " + e.getMessage());
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == insertButtonType) {
                CoordinateMonitoraggio selectedArea = areaComboBox.getValue();
                LocalDate selectedDate = dataPicker.getValue();
                if (selectedArea == null || dataPicker.getValue() == null || selectedDate == null) {
                    showAlert(Alert.AlertType.ERROR, "Dati mancanti",
                            "Campi obbligatori", "Seleziona un'area e una data.");
                    return null;
                }


                if (!validaDatiClim(ventoSpinner.getValue(), umiditaSpinner.getValue(),
                        pressioneSpinner.getValue(), temperaturaSpinner.getValue(),
                        precipitazioniSpinner.getValue(), altitudineSpinner.getValue(),
                        massaGhiacciaiSpinner.getValue())) {
                    showAlert(Alert.AlertType.ERROR, "Dati non validi",
                            "I dati inseriti non sono validi", "Controlla i valori e assicurati che siano nel range corretto.");
                    return null;
                }


                try {
                    boolean success = service.inserisciParametriClimatici(
                            currentUser.getId(),
                            null,
                            selectedArea.getId(),
                            java.sql.Date.valueOf(dataPicker.getValue()),
                            ventoSpinner.getValue(),
                            umiditaSpinner.getValue(),
                            pressioneSpinner.getValue(),
                            temperaturaSpinner.getValue(),
                            precipitazioniSpinner.getValue(),
                            altitudineSpinner.getValue(),
                            massaGhiacciaiSpinner.getValue(),
                            noteArea.getText()
                    );


                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Successo",
                                "Dati inseriti", "I parametri climatici sono stati inseriti con successo.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore",
                                "Inserimento fallito", "Non è stato possibile inserire i parametri climatici.");
                    }
                } catch (RemoteException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore di connessione",
                            "Errore del server", "Si è verificato un errore durante l'inserimento dei dati: " + e.getMessage());
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Errore imprevisto",
                            "Si è verificato un errore inaspettato", "Dettagli: " + e.getMessage());
                    e.printStackTrace(); // Log dell'eccezione per il debugging
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private boolean validaDatiClim(int vento, int umidita, int pressione, int temperatura,
                                   int precipitazioni, int altitudine, int massaGhiacciai) {
        if (vento < 0 || vento > 300) return false;
        if (umidita < 0 || umidita > 100) return false;
        if (pressione < 900 || pressione > 1100) return false;
        if (temperatura < -50 || temperatura > 50) return false;
        if (precipitazioni < 0 || precipitazioni > 500) return false;
        if (altitudine < 0 || altitudine > 8000) return false;
        if (massaGhiacciai < 0 || massaGhiacciai > 1000000) return false;
        return true;
    }

    @FXML
    private void handleInserisciParametriArea() throws RemoteException {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Accesso negato", "Effettua il login come operatore");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Inserisci Dati Climatici per Area");
        dialog.setHeaderText("Inserisci i parametri climatici per un'area specifica");

        ButtonType insertButtonType = new ButtonType("Inserisci", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<CoordinateMonitoraggio> areaComboBox = new ComboBox<>();
        List<CoordinateMonitoraggio> areeInteresse;

        try {
            areeInteresse = service.getAreeInteresseOperatore(currentUser.getId());

            if (areeInteresse.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Nessuna Area Trovata",
                        "Non ci sono aree di interesse associate al tuo ID operatore.",
                        "Crea delle aree all'interno del tuo centro di monitoraggio");
                return; // Esce se non ci sono aree
            }

            areaComboBox.getItems().addAll(areeInteresse);
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di connessione",
                    "Impossibile recuperare le aree", e.getMessage());
            return;
        }

        DatePicker dataPicker = new DatePicker(LocalDate.now()); // Imposta data odierna di default
        Spinner<Integer> ventoSpinner = new Spinner<>(0, 300, 0);
        Spinner<Integer> umiditaSpinner = new Spinner<>(0, 100, 50);
        Spinner<Integer> pressioneSpinner = new Spinner<>(900, 1100, 1013);
        Spinner<Integer> temperaturaSpinner = new Spinner<>(-50, 50, 20);
        Spinner<Integer> precipitazioniSpinner = new Spinner<>(0, 500, 0);
        Spinner<Integer> altitudineSpinner = new Spinner<>(0, 8000, 0);
        Spinner<Integer> massaGhiacciaiSpinner = new Spinner<>(0, 1000000, 0);
        TextArea noteArea = new TextArea();
        noteArea.setPrefRowCount(3);



        grid.add(new Label("Area:"), 0, 0);
        grid.add(areaComboBox, 1, 0);
        grid.add(new Label("Data:"), 0, 1);
        grid.add(dataPicker, 1, 1);
        grid.add(new Label("Vento (km/h):"), 0, 2);
        grid.add(ventoSpinner, 1, 2);
        grid.add(new Label("Umidità (%):"), 0, 3);
        grid.add(umiditaSpinner, 1, 3);
        grid.add(new Label("Pressione (hPa):"), 0, 4);
        grid.add(pressioneSpinner, 1, 4);
        grid.add(new Label("Temperatura (°C):"), 0, 5);
        grid.add(temperaturaSpinner, 1, 5);
        grid.add(new Label("Precipitazioni (mm):"), 0, 6);
        grid.add(precipitazioniSpinner, 1, 6);
        grid.add(new Label("Altitudine ghiacciai (m):"), 0, 7);
        grid.add(altitudineSpinner, 1, 7);
        grid.add(new Label("Massa ghiacciai (kg):"), 0, 8);
        grid.add(massaGhiacciaiSpinner, 1, 8);
        grid.add(new Label("Note:"), 0, 9);
        grid.add(noteArea, 1, 9);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == insertButtonType) {
                CoordinateMonitoraggio selectedArea = areaComboBox.getValue();
                LocalDate selectedDate = dataPicker.getValue();

                if (selectedArea == null || selectedDate == null) {
                    showAlert(Alert.AlertType.ERROR, "Dati mancanti",
                            "Campi obbligatori", "Seleziona un'area e una data.");
                    return null;
                }

                try {
                    // Log dei valori prima dell'inserimento
                    System.out.println("Tentativo inserimento dati per area: " + selectedArea.getId());

                    boolean success = service.inserisciParametriClimaticiArea(
                            selectedArea.getCentroMonitoraggioId(),
                            selectedArea.getId(),
                            java.sql.Date.valueOf(selectedDate),
                            ventoSpinner.getValue(),
                            umiditaSpinner.getValue(),
                            pressioneSpinner.getValue(),
                            temperaturaSpinner.getValue(),
                            precipitazioniSpinner.getValue(),
                            altitudineSpinner.getValue(),
                            massaGhiacciaiSpinner.getValue(),
                            noteArea.getText().trim()
                    );

                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Successo",
                                "Dati inseriti", "I parametri climatici sono stati inseriti con successo.");
                        // Aggiorna la vista se necessario
                        // updateView();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore",
                                "Inserimento fallito", "Non è stato possibile inserire i parametri climatici.");
                    }
                } catch (RemoteException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore di connessione",
                            "Errore del server", "Si è verificato un errore durante l'inserimento dei dati: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Errore imprevisto",
                            "Si è verificato un errore inaspettato", "Dettagli: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }


    private void handleLogout() {
        mainApp.loginView();
        setCurrentUser(null);
    }


    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleVisualizzaArea() {
        String areaName = monitoringAreaNameField.getText().trim();
        String areaStatus = monitoringAreaStatusField.getText().trim();

        if (areaName.isEmpty() || areaStatus.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,
                    "Errore di ricerca",
                    "Campi vuoti",
                    "Inserisci sia il nome che lo stato dell'area di interesse.");
            return;
        }

        try {
            // Chiama il metodo del servizio per ottenere i dati dell'area
            String risultato = service.visualizzaAreaCentroMonitoraggio(areaName, areaStatus);

            if (risultato.equals("Area di interesse non trovata.")) {
                monitoringAreaResultArea.setText("Nessuna area di interesse trovata con i criteri specificati.");
            } else {
                monitoringAreaResultArea.setText(risultato);
            }
        } catch (RemoteException e) {
            handleRemoteException(e, "ricerca dell'area di interesse");
        }
    }




    private void handleRemoteException(RemoteException e, String operation) {
        showAlert(Alert.AlertType.ERROR, "Errore di connessione", "Errore del server", "Si è verificato un errore durante la " + operation + ": " + e.getMessage());
        e.printStackTrace();
    }
}
