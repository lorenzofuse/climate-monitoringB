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


/**
 * Controller principale per l'interfaccia utente del sistema di monitoraggio climatico.
 *
 * Questa classe gestisce le interazioni dell'utente, incluse le ricerche geografiche,
 * l'inserimento di dati climatici e le operazioni di gestione delle aree di monitoraggio.
 *
 * Responsabilità principali:
 * - Gestire le ricerche di aree geografiche per nome, coordinate e paese
 * - Consentire l'inserimento di parametri climatici
 * - Gestire la creazione di centri di monitoraggio e aree di interesse
 * - Implementare funzionalità di autenticazione e logout
 *
 * @author Fusè Lorenzo 753168
 * @author Ciminella Alessandro 753369
 * @author Dragan Cosmin 754427
 */
public class MainController {

    /**
     * Campo di testo per la ricerca di aree geografiche per nome.
     */
    @FXML private TextField searchField;

    /**
     * Campo di testo per inserire lo stato/regione durante la ricerca.
     */
    @FXML private TextField stateField;

    /**
     * Campo di testo per l'inserimento della latitudine durante la ricerca per coordinate.
     */
    @FXML public TextField latitudeField;

    /**
     * Campo di testo per l'inserimento della longitudine durante la ricerca per coordinate.
     */
    @FXML private TextField longitudeField;

    /**
     * Pulsante per avviare la ricerca di aree geografiche.
     * Attiva il metodo di ricerca quando viene premuto,
     * utilizzando i criteri inseriti nei campi di testo correlati.
     */
    @FXML private Button searchButton;

    /**
     * Area di testo per visualizzare i risultati delle ricerche
     * per nome di città e stato.
     * Mostra le informazioni dettagliate delle aree geografiche
     * trovate durante la ricerca.
     */
    @FXML private TextArea resultArea;

    /**
     * Area di testo per visualizzare i risultati delle ricerche
     * per coordinate geografiche.
     * Presenta informazioni dettagliate delle aree vicine
     * alle coordinate specificate, inclusi dettagli come
     * distanza e informazioni geografiche.
     */
    @FXML private TextArea coordinateResultArea;

    /**
     * Pulsante per effettuare il logout dall'applicazione.
     * Permette all'utente di terminare la sessione corrente
     * e tornare alla schermata di login.
     */
    @FXML private Button logoutButton;

    /**
     * Tab dedicata alle operazioni riservate agli operatori.
     * Contiene funzionalità avanzate accessibili solo
     * dopo l'autenticazione come operatore.
     */
    @FXML private Tab operatorTab;

    /**
     * Campo di testo per inserire il nome del paese
     * durante la ricerca geografica.
     * Utilizzato per filtrare e trovare aree geografiche
     * appartenenti a un determinato paese.
     */
    @FXML private TextField paeseField;

    /**
     * Area di testo per visualizzare i risultati della ricerca per paese.
     * Mostra le informazioni dettagliate delle aree geografiche
     * trovate nel paese specificato.
     */
    @FXML private TextArea paeseResultArea;

    /**
     * Area di testo per visualizzare informazioni e risultati
     * relativi agli operatori.
     * Può contenere dettagli sugli operatori registrati,
     * loro attività o risultati delle operazioni.
     */
    @FXML public TextArea operatorResultArea;

    /**
     * Contenitore principale per i tab dell'interfaccia.
     * Gestisce la navigazione e l'organizzazione delle
     * diverse sezioni dell'applicazione.
     */
    @FXML private TabPane mainTabPane;

    /**
     * ComboBox per selezionare un'area di monitoraggio.
     * Permette all'utente di scegliere tra le aree geografiche
     * disponibili per ulteriori operazioni.
     */
    @FXML private ComboBox<CoordinateMonitoraggio> areaComboBox;

    /**
     * Seconda ComboBox per la selezione di aree di monitoraggio.
     * Fornisce un'ulteriore opzione di selezione area,
     * potenzialmente per confronti o operazioni multiple.
     */
    @FXML private ComboBox<CoordinateMonitoraggio> areaComboBox2 = new ComboBox<>();

    /**
     * Campo di testo per inserire il nome di un'area
     * durante le operazioni di visualizzazione dati.
     * Utilizzato per identificare specifiche aree geografiche.
     */
    @FXML private TextField areaNameField;

    /**
     * Campo di testo per inserire lo stato di un'area
     * durante le operazioni di visualizzazione dati.
     * Permette di precisare l'area geografica di interesse.
     */
    @FXML private TextField areaStateField;

    /**
     * Area di testo per mostrare i dati climatici di un'area.
     * Visualizza informazioni dettagliate sui parametri
     * climatici di un'area specifica.
     */
    @FXML private TextArea climateDataResultArea;

    /**
     * Campo di testo per inserire il nome di un'area
     * di monitoraggio durante la visualizzazione dei dettagli.
     * Utilizzato per identificare specifiche aree di monitoraggio.
     */
    @FXML private TextField monitoringAreaNameField;

    /**
     * Campo di testo per inserire lo stato di un'area
     * di monitoraggio durante la visualizzazione dei dettagli.
     * Permette di precisare l'area di monitoraggio di interesse.
     */
    @FXML private TextField monitoringAreaStatusField;

    /**
     * Area di testo per visualizzare i risultati
     * relativi alle aree di monitoraggio.
     * Mostra informazioni dettagliate sull'area
     * di monitoraggio selezionata.
     */
    @FXML private TextArea monitoringAreaResultArea;

    /**
     * Pulsante per attivare la visualizzazione dei dettagli
     * di un'area di monitoraggio.
     * Avvia il processo di recupero e presentazione
     * delle informazioni sull'area.
     */
    @FXML private Button viewMonitoringAreaButton;

    /**
     * Tab dedicato alla visualizzazione dei dettagli
     * di un centro di monitoraggio.
     * Contiene le funzionalità per esplorare
     * le informazioni sui centri di monitoraggio.
     */
    @FXML private Tab visualizzaAreaCentroTab;


    /**
     * Riferimento all'utente operatore attualmente connesso.
     * Utilizzato per verificare i permessi e associare le azioni dell'utente.
     */
    private OperatoriRegistrati currentUser;

    /**
     * Riferimento all'applicazione principale del client.
     * Utilizzato per gestire la navigazione tra diverse viste.
     */
    private ClientCM mainApp;

    /**
     * Servizio RMI per le operazioni di monitoraggio climatico.
     * Fornisce i metodi per interagire con il server remoto.
     */
    private ClimateMonitoringService service;

    /**
     * Costruttore predefinito.
     * Inizializza un'istanza vuota del controller.
     */
    public MainController() { }


    /**
     * Imposta il servizio RMI per le operazioni di monitoraggio climatico.
     *
     * @param service Servizio RMI da utilizzare per le comunicazioni con il server
     */
    public void setService(ClimateMonitoringService service) {
        this.service = service;
    }



    /**
     * Imposta il riferimento all'applicazione principale.
     *
     * @param mainApp Istanza dell'applicazione client principale
     */
    public void setMainApp(ClientCM mainApp) {
        this.mainApp = mainApp;
    }



    /**
     * Metodo di inizializzazione chiamato automaticamente dopo il caricamento del file FXML.
     * Configura i listener per i pulsanti e inizializza lo stato iniziale dell'interfaccia.
     */
    @FXML
    private void initialize() {
        searchButton.setOnAction(event -> handlecercaAreaGeograficaNome());
        logoutButton.setOnAction(event -> handleLogout());

        if (operatorTab != null) {
            operatorTab.setDisable(true);
        }
        aggiornaAree();
    }


    /**
     * Imposta l'utente corrente e aggiorna i permessi dell'interfaccia.
     *
     * @param user Utente operatore attualmente connesso
     */
    public void setCurrentUser(OperatoriRegistrati user) {
        this.currentUser = user;
        if (user != null && operatorTab != null) {
            operatorTab.setDisable(false);
            aggiornaAree();
        } else {
            operatorTab.setDisable(true);
        }
    }

    /**
     * Gestisce la ricerca di un'area geografica per nome di città e stato.
     *
     * Convalida l'input, esegue la ricerca tramite il servizio RMI
     * e visualizza i risultati nell'area di testo dedicata.
     */
    @FXML
    private void handlecercaAreaGeograficaNome() {
        String nomeCitta = searchField.getText().trim();
        String nomeStato = stateField.getText().trim();

        if (nomeCitta.isEmpty() || nomeStato.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Errore di Ricerca", "Campi vuoti", "Inserisci sia la città che lo stato.");
            return;
        }

        try {
            List<CoordinateMonitoraggio> results = service.cercaAreaGeograficaNome(nomeCitta, nomeStato);

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
            showAlert(Alert.AlertType.ERROR, "Errore di Connessione", "Errore del Server", "Si è verificato un errore durante la ricerca: " + e.getMessage());
        }
    }

    /**
     * Gestisce la ricerca di un'area geografica tramite coordinate geografiche.
     * Converte le coordinate di input, verifica la loro validità e cerca le aree geografiche vicine.
     * Visualizza i risultati con informazioni dettagliate come città, stato, coordinate e distanza.
     *
     * @throws RemoteException se si verifica un errore durante la comunicazione con il servizio remoto
     * @throws NumberFormatException se le coordinate non possono essere convertite in numeri
     */
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

    /**
     * Converte una stringa di coordinate in un valore numerico.
     * Gestisce diversi formati di input, inclusi valori con virgola o punto decimale.
     *
     * @param coord Stringa contenente la coordinata da convertire
     * @return Il valore della coordinata come numero decimale
     * @throws NumberFormatException se la stringa non può essere convertita
     */
    private double parseCoordinate(String coord) {
        // gestisco i != formati
        coord = coord.replace(',', '.').trim();
        return Double.parseDouble(coord);
    }

    /**
     * Verifica se una latitudine è compresa nell'intervallo valido (-90, 90).
     *
     * @param lat Valore della latitudine da verificare
     * @return true se la latitudine è valida, false altrimenti
     */
    private boolean isValidLatitude(double lat) {
        return lat >= -90 && lat <= 90;
    }

    /**
     * Verifica se una longitudine è compresa nell'intervallo valido (-180, 180).
     *
     * @param lon Valore della longitudine da verificare
     * @return true se la longitudine è valida, false altrimenti
     */
    private boolean isValidLongitude(double lon) {
        return lon >= -180 && lon <= 180;
    }

    /**
     * Visualizza i risultati della ricerca per coordinate geografiche.
     * Calcola e mostra la distanza tra le coordinate di ricerca e le aree trovate.
     *
     * @param results Lista delle aree geografiche trovate
     * @param searchLat Latitudine delle coordinate di ricerca
     * @param searchLon Longitudine delle coordinate di ricerca
     */
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

    /**
     * Calcola la distanza tra due punti geografici utilizzando la formula di Haversine.
     *
     * @param lat1 Latitudine del primo punto
     * @param lon1 Longitudine del primo punto
     * @param lat2 Latitudine del secondo punto
     * @param lon2 Longitudine del secondo punto
     * @return Distanza in chilometri tra i due punti
     */
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

    /**
     * Gestisce la ricerca di aree geografiche per paese.
     * Recupera e visualizza le aree geografiche appartenenti al paese specificato.
     *
     * @throws RemoteException se si verifica un errore durante la comunicazione con il servizio remoto
     */
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

    /**
     * Visualizza i dati climatici di un'area geografica specifica.
     * Richiede l'inserimento di nome e stato dell'area.
     *
     * @throws RemoteException se si verifica un errore durante la comunicazione con il servizio remoto
     * @throws IllegalArgumentException se i parametri di input non sono validi
     */
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

    /**
     * Gestisce il processo di creazione di un centro di monitoraggio per l'operatore corrente.
     *
     * Questo metodo apre una finestra di dialogo che consente all'operatore di inserire
     * i dettagli di un nuovo centro di monitoraggio, inclusi:
     * - Nome del centro
     * - Indirizzo
     * - CAP
     * - Comune
     * - Provincia
     *
     * Il metodo esegue le seguenti operazioni principali:
     * 1. Verifica che l'utente sia autenticato come operatore
     * 2. Crea un dialogo per l'inserimento dei dati
     * 3. Convalida l'input obbligatorio (nome)
     * 4. Chiama il servizio remoto per creare il centro di monitoraggio
     * 5. Mostra un messaggio di successo o di errore
     *
     * @throws RemoteException se si verifica un errore durante la comunicazione con il servizio remoto
     * @throws IllegalArgumentException se i dati inseriti non sono validi
     */
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
                        showAlert(Alert.AlertType.INFORMATION, "Successo", "Centro creato", "Il centro è stato creato con successo");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore", "Creazione fallita", "Non è stato possibile creare il centro");
                    }
                } catch (RemoteException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore creazione centro di monitoraggio", "Creazione fallita", "L'utente ha già un centro di monitoraggio");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Gestisce la creazione di una nuova area di interesse per l'operatore corrente.
     *
     * Questo metodo apre una findia di dialogo che consente all'operatore di definire
     * una nuova area di interesse geografica, richiedendo:
     * - Nome della città
     * - Stato
     * - Latitudine
     * - Longitudine
     *
     * Principali funzionalità:
     * 1. Verifica l'autenticazione dell'operatore
     * 2. Crea un dialogo per l'inserimento delle informazioni geografiche
     * 3. Convalida i campi obbligatori (città, stato)
     * 4. Converte e verifica le coordinate geografiche
     * 5. Chiama il servizio remoto per creare l'area di interesse
     * 6. Aggiorna la lista delle aree disponibili
     * 7. Mostra messaggi di esito (successo/errore)
     *
     * @throws RemoteException se si verifica un errore durante la comunicazione con il servizio remoto
     * @throws NumberFormatException se le coordinate non possono essere convertite in valori numerici
     */
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
                        showAlert(Alert.AlertType.INFORMATION, "Successo", "Area creata", "L'area è stata creata con successo");
                        aggiornaAree();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore", "Creazione fallita", "Non è stato possibile creare l'area");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore", "Formato non valido", "Inserisci coordinate numeriche valide");
                } catch (RemoteException e) {
                    if (e.getMessage().contains("non ha un centro di monitoraggio")) {
                        showAlert(Alert.AlertType.ERROR, "Errore", "Centro mancante", "Crea prima un centro di monitoraggio");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore", "Errore di connessione", "Errore durante la creazione: " + e.getMessage());
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Aggiorna la lista delle aree geografiche associate al centro di monitoraggio dell'utente corrente.
     * Utilizzato per mantenere aggiornata la ComboBox delle aree.
     *
     * @throws RemoteException se si verifica un errore durante il recupero delle aree
     */
    private void aggiornaAree() {
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

    /**
     * Gestisce l'inserimento di parametri climatici per un centro di monitoraggio.
     *
     * Questo metodo apre una finestra di dialogo interattiva che consente all'operatore
     * di inserire dati climatici dettagliati, con le seguenti caratteristiche principali:
     *
     * Dati richiesti:
     * - Selezione dell'area di monitoraggio
     * - Data del rilevamento
     * - Parametri climatici specifici:
     *   - Velocità del vento (km/h)
     *   - Umidità (%)
     *   - Pressione atmosferica (hPa)
     *   - Temperatura (°C)
     *   - Precipitazioni (mm)
     *   - Altitudine dei ghiacciai (m)
     *   - Massa dei ghiacciai (kg)
     * - Note aggiuntive
     *
     * Flusso di elaborazione:
     * 1. Verifica l'autenticazione dell'operatore
     * 2. Carica le aree associate al centro di monitoraggio
     * 3. Presenta un form interattivo per l'inserimento dei dati
     * 4. Convalida i dati inseriti (range dei valori, campi obbligatori)
     * 5. Invia i parametri al servizio remoto per la registrazione
     * 6. Fornisce feedback all'utente sull'esito dell'operazione
     *
     * Gestisce inoltre scenari di errore come:
     * - Mancata autenticazione
     * - Dati mancanti o non validi
     * - Errori di connessione al servizio remoto
     *
     * @throws RemoteException se si verificano problemi di comunicazione con il servizio remoto
     * @throws IllegalArgumentException se i dati inseriti non rispettano i criteri di validazione
     */
    @FXML
    private void handleInserisciParametri() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Accesso negato", "Effettua il login come operatore");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Inserisci Dati Climatici");
        dialog.setHeaderText("Inserisci i parametri climatici per un'area");

        ButtonType ins = new ButtonType("Inserisci", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ins, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<CoordinateMonitoraggio> areaComboBox = new ComboBox<>();
        DatePicker data = new DatePicker();
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
        grid.add(data, 1, 1);
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


        try {
            List<CoordinateMonitoraggio> aree = service.getAreePerCentroMonitoraggio(currentUser.getId());
            areaComboBox.getItems().addAll(aree);
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di caricamento", "Impossibile caricare le aree", "Si è verificato un errore nel caricamento delle aree: " + e.getMessage());
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ins) {
                CoordinateMonitoraggio selArea = areaComboBox.getValue();
                LocalDate selData = data.getValue();
                if (selArea == null || data.getValue() == null || selData == null) {
                    showAlert(Alert.AlertType.ERROR, "Dati mancanti", "Campi obbligatori", "Seleziona un'area e una data.");
                    return null;
                }


                if (!validaDatiClim(ventoSpinner.getValue(), umiditaSpinner.getValue(),
                        pressioneSpinner.getValue(), temperaturaSpinner.getValue(),
                        precipitazioniSpinner.getValue(), altitudineSpinner.getValue(),
                        massaGhiacciaiSpinner.getValue())) {
                    showAlert(Alert.AlertType.ERROR, "Dati non validi", "I dati inseriti non sono validi", "Controlla i valori e assicurati che siano nel range corretto.");
                    return null;
                }


                try {
                    boolean success = service.inserisciParametriClimatici(
                            currentUser.getId(),
                            null,
                            selArea.getId(),
                            java.sql.Date.valueOf(data.getValue()),
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
                        showAlert(Alert.AlertType.INFORMATION, "Successo", "Dati inseriti", "I parametri climatici sono stati inseriti con successo.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore", "Inserimento fallito", "Non è stato possibile inserire i parametri climatici.");
                    }
                } catch (RemoteException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore di connessione", "Errore del server", "Si è verificato un errore durante l'inserimento dei dati: " + e.getMessage());
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Errore imprevisto", "Si è verificato un errore inaspettato", "Dettagli: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Convalida i parametri climatici inseriti per garantire che siano entro intervalli accettabili.
     *
     * @return true se tutti i parametri sono validi, false altrimenti
     */
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

    /**
     * Gestisce l'inserimento di parametri climatici per un'area specifica.
     *
     * Questo metodo apre una finestra di dialogo che permette all'operatore di inserire
     * dati climatici dettagliati per un'area di interesse, compresi:
     * - Selezione dell'area
     * - Data del rilevamento
     * - Parametri climatici:
     *   - Velocità del vento
     *   - Umidità
     *   - Pressione
     *   - Temperatura
     *   - Precipitazioni
     *   - Altitudine dei ghiacciai
     *   - Massa dei ghiacciai
     * - Note aggiuntive
     *
     * Funzionalità principali:
     * 1. Verifica l'autenticazione dell'operatore
     * 2. Recupera le aree di interesse dell'operatore
     * 3. Crea un dialogo per l'inserimento dei dati climatici
     * 4. Convalida la selezione dell'area e della data
     * 5. Chiama il servizio remoto per registrare i parametri climatici
     * 6. Mostra messaggi di esito (successo/errore)
     *
     * @throws RemoteException se si verifica un errore durante la comunicazione con il servizio remoto
     */
    @FXML
    private void handleInserisciParametriArea(){
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
                showAlert(Alert.AlertType.WARNING, "Nessuna Area Trovata", "Non ci sono aree di interesse associate al tuo ID operatore.", "Crea delle aree all'interno del tuo centro di monitoraggio");
                return;
            }

            areaComboBox.getItems().addAll(areeInteresse);
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di connessione", "Impossibile recuperare le aree", e.getMessage());
            return;
        }

        DatePicker dataPicker = new DatePicker(LocalDate.now());
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
                        showAlert(Alert.AlertType.INFORMATION, "Successo", "Dati inseriti", "I parametri climatici sono stati inseriti con successo.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore", "Inserimento fallito", "Non è stato possibile inserire i parametri climatici.");
                    }
                } catch (RemoteException e) {
                    showAlert(Alert.AlertType.ERROR, "Errore di connessione", "Errore del server", "Si è verificato un errore durante l'inserimento dei dati: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Errore imprevisto", "Si è verificato un errore inaspettato", "Dettagli: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Gestisce il processo di disconnessione dell'utente dal sistema.
     *
     * Questo metodo esegue le seguenti operazioni principali:
     * 1. Richiama il metodo per tornare alla vista di login nell'applicazione principale
     * 2. Azzera l'utente corrente, rimuovendo tutti i riferimenti e i dati della sessione
     *
     * Funzionalità specifiche:
     * - Reindirizza l'utente alla schermata di login
     * - Cancella le informazioni dell'utente corrente per garantire la sicurezza
     * - Prepara l'applicazione per un nuovo accesso
     *
     * Questo metodo è tipicamente associato al pulsante di logout nell'interfaccia utente
     * e rappresenta un punto critico per la gestione della sicurezza dell'applicazione.
     */
    private void handleLogout() {
        mainApp.loginView();
        setCurrentUser(null);
    }

    /**
     * Visualizza un messaggio di avviso all'utente.
     *
     * @param alertType Tipo di avviso (INFORMATION, WARNING, ERROR, ecc.)
     * @param title Titolo della finestra di dialogo
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



    /**
     * Gestisce la visualizzazione dei dettagli di un'area di monitoraggio.
     *
     * Questo metodo permette di recuperare e visualizzare informazioni dettagliate
     * su un'area di interesse specificata tramite nome e stato.
     *
     * Funzionalità principali:
     * 1. Estrae il nome dell'area e lo stato dai campi di input
     * 2. Convalida che entrambi i campi siano stati compilati
     * 3. Chiama il servizio remoto per recuperare i dettagli dell'area
     * 4. Visualizza i risultati nell'area di testo dedicata
     * 5. Gestisce i casi in cui l'area non viene trovata
     *
     * @throws RemoteException se si verifica un errore durante la comunicazione con il servizio remoto
     */
    @FXML
    private void handleVisualizzaArea() {
        String nomeArea = monitoringAreaNameField.getText().trim();
        String nomeStato = monitoringAreaStatusField.getText().trim();

        if (nomeArea.isEmpty() || nomeStato.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Errore di ricerca", "Campi vuoti", "Inserisci sia il nome che lo stato dell'area di interesse.");
            return;
        }

        try {

            String risultato = service.visualizzaAreaCentroMonitoraggio(nomeArea, nomeStato);

            if (risultato.equals("Area di interesse non trovata.")) {
                monitoringAreaResultArea.setText("Nessuna area di interesse trovata con i criteri specificati.");
            } else {
                monitoringAreaResultArea.setText(risultato);
            }
        } catch (RemoteException e) {
            handleRemoteException(e, "ricerca dell'area di interesse");
        }
    }


    /**
     * Gestisce l'eccezione RemoteException con un messaggio di errore standard.
     *
     * @param e L'eccezione RemoteException originale
     * @param operation Descrizione dell'operazione che ha causato l'eccezione
     */
    private void handleRemoteException(RemoteException e, String operation) {
        showAlert(Alert.AlertType.ERROR, "Errore di connessione", "Errore del server", "Si è verificato un errore durante la " + operation + ": " + e.getMessage());
        e.printStackTrace();
    }
}
