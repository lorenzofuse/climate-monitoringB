package com.climatemonitoring.server.server;

import com.climatemonitoring.common.model.OperatoriRegistrati;
import com.climatemonitoring.common.service.ClimateMonitoringService;
import com.climatemonitoring.common.model.CoordinateMonitoraggio;
import com.climatemonitoring.server.util.DatabaseManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementazione del servizio di monitoraggio climatico che gestisce
 * la ricerca e la visualizzazione di aree geografiche e parametri climatici.
 *
 * Questa classe estende UnicastRemoteObject per supportare chiamate remote
 * e implementa l'interfaccia ClimateMonitoringService.
 *
 * @author
 */
public class ClimateMonitoringServiceImpl extends UnicastRemoteObject implements ClimateMonitoringService {

    /**
     * Gestore del database per le connessioni e le query.
     */
    private final DatabaseManager dbManager;

    /**
     * Costruttore della classe che inizializza il servizio RMI.
     *
     * @param dbManager Gestore del database per stabilire le connessioni
     * @throws RemoteException Se si verificano errori durante l'inizializzazione remota
     */
    public ClimateMonitoringServiceImpl(DatabaseManager dbManager) throws RemoteException {
        super();
        this.dbManager = dbManager;
    }

    /**
     * Cerca aree geografiche per nome città e stato.
     *
     * Effettua una ricerca nel database per trovare aree geografiche
     * che corrispondono parzialmente al nome della città e allo stato specificati.
     *
     * @param nome Nome della città (può essere parziale)
     * @param stato Stato in cui cercare la città
     * @return Lista di coordinate di monitoraggio corrispondenti
     * @throws RemoteException Se si verificano errori durante la ricerca nel database
     * @throws IllegalArgumentException Se nome o stato sono nulli o vuoti
     */
    @Override
    public List<CoordinateMonitoraggio> cercaAreaGeograficaNome(String nome, String stato) throws RemoteException {
        List<CoordinateMonitoraggio> aree = new ArrayList<>();

        if (nome == null || nome.trim().isEmpty() || stato == null || stato.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e stato non possono essere nulli o vuoti");
        }

        String sql = "SELECT * FROM coordinatemonitoraggio WHERE nome_citta LIKE ? AND stato = ?";

        try {

            Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, "%" + nome + "%");
            pstmt.setString(2, stato);

            try {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    CoordinateMonitoraggio area = new CoordinateMonitoraggio(
                            rs.getInt("id"),
                            rs.getString("nome_citta"),
                            rs.getString("stato"),
                            rs.getString("paese"),
                            rs.getDouble("latitudine"),
                            rs.getDouble("longitudine")
                    );
                    aree.add(area);
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            throw new RemoteException("Errore durante la ricerca delle aree geografiche", e);
        }
        return aree;
    }

    /**
     * Cerca aree geografiche per paese.
     *
     * Recupera tutte le coordinate di monitoraggio che appartengono
     * parzialmente al paese specificato.
     *
     * @param paese Nome del paese (può essere parziale)
     * @return Lista di coordinate di monitoraggio nel paese
     * @throws RemoteException Se si verificano errori durante la ricerca nel database
     * @throws IllegalArgumentException Se il paese è nullo o vuoto
     */
    @Override
    public List<CoordinateMonitoraggio> cercaAreaGeograficaPerPaese(String paese) throws RemoteException {
        List<CoordinateMonitoraggio> aree = new ArrayList<>();

        if (paese == null || paese.trim().isEmpty()) {
            throw new IllegalArgumentException("Il paese non può essere nullo");
        }

        String sql = "SELECT * FROM coordinatemonitoraggio WHERE paese LIKE ?";

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement p = conn.prepareStatement(sql);

            p.setString(1, "%" + paese + "%");

            try {
                ResultSet rs = p.executeQuery();
                while (rs.next()) {
                    CoordinateMonitoraggio area = new CoordinateMonitoraggio(
                            rs.getInt("id"),
                            rs.getString("nome_citta"),
                            rs.getString("stato"),
                            rs.getString("paese"),
                            rs.getDouble("latitudine"),
                            rs.getDouble("longitudine")
                    );
                    aree.add(area);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RemoteException("Errore durante la ricerca delle aree geografiche per paese", e);

        }
        return aree;
    }

    /**
     * Cerca aree geografiche in prossimità di coordinate geografiche specifiche.
     *
     * Questo metodo effettua una ricerca delle coordinate di monitoraggio
     * entro un raggio di tolleranza (0.5 gradi) rispetto alle coordinate inserite.
     * Le aree trovate vengono poi ordinate in base alla loro distanza dal punto di ricerca.
     *
     * Caratteristiche principali:
     * - Verifica la validità delle coordinate in input
     * - Usa una query SQL con intervallo di tolleranza per latitudine e longitudine
     * - Recupera i dettagli delle aree geografiche vicine
     * - Ordina i risultati dalla zona più vicina alla più lontana
     *
     * @param latitudine Latitudine del punto di ricerca
     * @param longitudine Longitudine del punto di ricerca
     * @return Lista di {@link CoordinateMonitoraggio} ordinate per vicinanza
     * @throws RemoteException Se si verificano errori durante la ricerca nel database
     * @throws IllegalArgumentException Se latitudine o longitudine sono nulli
     */
    @Override
    public List<CoordinateMonitoraggio> cercaAreaGeograficaCoordinate(Double latitudine, Double longitudine) throws RemoteException {

        if (latitudine == null || longitudine == null) {
            throw new IllegalArgumentException("Latitudine e longitudine non possono essere nulli");
        }

        List<CoordinateMonitoraggio> aree = new ArrayList<>();
        final double TOLLERANZA = 0.5;

        String sql = "SELECT * FROM coordinatemonitoraggio WHERE latitudine BETWEEN ? - ? AND ? + ? AND longitudine BETWEEN ? - ? AND ? + ? ";

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setDouble(1, latitudine);
            pstmt.setDouble(2, TOLLERANZA);
            pstmt.setDouble(3, latitudine);
            pstmt.setDouble(4, TOLLERANZA);
            pstmt.setDouble(5, longitudine);
            pstmt.setDouble(6, TOLLERANZA);
            pstmt.setDouble(7, longitudine);
            pstmt.setDouble(8, TOLLERANZA);

            try {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    CoordinateMonitoraggio area = new CoordinateMonitoraggio();
                    area.setId(rs.getInt("id"));
                    area.setNomeCitta(rs.getString("nome_citta"));
                    area.setStato(rs.getString("stato"));
                    area.setPaese(rs.getString("paese"));
                    area.setLatitudine(rs.getDouble("latitudine"));
                    area.setLongitudine(rs.getDouble("longitudine"));
                    aree.add(area);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Errore durante la ricerca nel database", e);
        }

        //ordino ris dopo averli presi tramite la dist + vicina
        if (!aree.isEmpty()) {
            aree.sort((a1, a2) -> {
                double dist1 = calcolaDistanzaKm(latitudine, longitudine, a1.getLatitudine(), a1.getLongitudine());
                double dist2 = calcolaDistanzaKm(latitudine, longitudine, a2.getLatitudine(), a2.getLongitudine());
                return Double.compare(dist1, dist2);
            });

        }

        return aree;
    }

    /**
     * Calcola la distanza in chilometri tra due punti geografici
     * utilizzando la formula di Haversine.
     *
     * Questo metodo privato supporta la ricerca di aree geografiche
     * vicine a coordinate specifiche.
     *
     * @param lat1 Latitudine del primo punto
     * @param lon1 Longitudine del primo punto
     * @param lat2 Latitudine del secondo punto
     * @param lon2 Longitudine del secondo punto
     * @return Distanza in chilometri tra i due punti
     */
    private double calcolaDistanzaKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Visualizza i dettagli completi di un'area geografica, inclusi
     * i parametri climatici e i commenti degli operatori.
     *
     * Recupera informazioni dettagliate su una specifica area geografica
     * identificata da nome e stato.
     *
     * @param nome Nome della città
     * @param stato Stato della città
     * @return Stringa formattata con le informazioni dell'area geografica
     * @throws RemoteException Se si verificano errori durante il recupero dei dati
     * @throws IllegalArgumentException Se nome o stato sono nulli o vuoti
     */
    @Override
    public String visualizzaAreaGeografica(String nome, String stato) throws RemoteException {
        if (nome == null || nome.trim().isEmpty() || stato == null || stato.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e stato non possono essere nulli o vuoti");
        }

        String sql = "SELECT * FROM coordinatemonitoraggio WHERE nome_citta = ? AND stato = ?";
        StringBuilder result = new StringBuilder();

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, nome);
            pstmt.setString(2, stato);


            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result.append("=== Informazioni Area Geografica ===\n\n");
                result.append("  ID: ").append(rs.getInt("id")).append("\n");
                result.append("  Nome città: ").append(rs.getString("nome_citta")).append("\n");
                result.append("  Stato: ").append(rs.getString("stato")).append("\n");
                result.append("  Paese: ").append(rs.getString("paese")).append("\n");
                result.append("  Latitudine: ").append(rs.getDouble("latitudine")).append("\n");
                result.append("  Longitudine: ").append(rs.getDouble("longitudine")).append("\n\n");

                int areaId = rs.getInt("id");
                boolean hasParametri = appendParametriClimatici(result, areaId, "coordinate_monitoraggio_id");

                if (!hasParametri) {
                    result.append("\nNessun dato climatico disponibile per questa area.\n");
                }

                appendCommentiOperatori(result, areaId, "coordinate_monitoraggio_id");
            } else {
                result.append("Area geografica non trovata per: ")
                        .append(nome)
                        .append(", ")
                        .append(stato);
            }

        } catch (SQLException e) {
            throw new RemoteException("Errore durante la visualizzazione dell'area geografica: " + e.getMessage(), e);
        }

        return result.toString();
    }


    /**
     * Recupera e aggiunge i parametri climatici ad un {@link StringBuilder}.
     *
     * Questo metodo esegue due query principali:
     * 1. Calcolo delle medie dei parametri climatici
     * 2. Recupero dei dettagli delle rilevazioni
     *
     * @param result StringBuilder su cui appendere i risultati
     * @param id Identificativo dell'area di monitoraggio
     * @param idColumnType Tipo di colonna per l'identificazione (coordinate, centro, area interesse)
     * @return {@code true} se ci sono rilevazioni, {@code false} altrimenti
     * @throws SQLException In caso di errori durante l'accesso al database
     */
    private boolean appendParametriClimatici(StringBuilder result, int id, String idColumnType) throws SQLException {
        // Query per le medie
        String sqlAvg = "SELECT COUNT(*) AS num_rilevazioni, " +
                "AVG(vento) AS avg_vento, " +
                "AVG(umidita) AS avg_umidita, " +
                "AVG(pressione) AS avg_pressione, " +
                "AVG(temperatura) AS avg_temperatura, " +
                "AVG(precipitazioni) AS avg_precipitazioni, " +
                "AVG(altitudine) AS avg_altitudine, " +
                "AVG(massa_ghiacciai) AS avg_massa_ghiacciai " +
                "FROM parametriclimatici " +
                "WHERE " + idColumnType + " = ?";

        // dettagli
        String sqlDettaglio;
        if (idColumnType.equals("coordinate_monitoraggio_id")) {
            sqlDettaglio = "SELECT p.*, p.data_rilevazione, " +
                    "p.vento, p.umidita, p.pressione, p.temperatura, " +
                    "p.precipitazioni, p.altitudine, p.massa_ghiacciai, p.note " +
                    "FROM parametriclimatici p " +
                    "WHERE p." + idColumnType + " = ? " +
                    "ORDER BY p.data_rilevazione DESC";
        } else {
            sqlDettaglio = "SELECT p.*, op.nome AS nome_operatore, op.cognome AS cognome_operatore, " +
                    "p.data_rilevazione, p.vento, p.umidita, p.pressione, p.temperatura, " +
                    "p.precipitazioni, p.altitudine, p.massa_ghiacciai, p.note " +
                    "FROM parametriclimatici p " +
                    "JOIN centrimonitoraggio cm ON p.centro_monitoraggio_id = cm.id " +
                    "JOIN operatoriregistrati op ON cm.operatore_id = op.id " +
                    "WHERE p." + idColumnType + " = ? " +
                    "ORDER BY p.data_rilevazione DESC";
        }


        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmtAvg = conn.prepareStatement(sqlAvg);

            pstmtAvg.setInt(1, id);

            ResultSet rs = pstmtAvg.executeQuery();
            if (rs.next() && rs.getInt("num_rilevazioni") > 0) {
                appendMedie(result, rs);

                // Aggiungi i dettagli
                try (PreparedStatement pstmtDettaglio = conn.prepareStatement(sqlDettaglio)) {
                    pstmtDettaglio.setInt(1, id);
                    appendDetails(result, pstmtDettaglio.executeQuery(), idColumnType.equals("centro_monitoraggio_id"));
                }
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    /**
     * Aggiunge le medie dei parametri climatici al risultato.
     *
     * Calcola e formatta le medie di diversi parametri climatici
     * come vento, umidità, temperatura, precipitazioni, ecc.
     *
     * @param result StringBuilder su cui appendere le medie
     * @param rs ResultSet contenente i dati delle medie
     * @throws SQLException In caso di errori durante l'accesso ai dati
     */
    private void appendMedie(StringBuilder result, ResultSet rs) throws SQLException {
        result.append("\n=== Riepilogo generale dei dati climatici ===\n\n");
        result.append("Numero totale di rilevazioni: ").append(rs.getInt("num_rilevazioni")).append("\n\n");
        result.append("Medie dei parametri climatici:\n");
        result.append("  Vento: ").append(String.format("%.2f", rs.getDouble("avg_vento"))).append(" m/s\n");
        result.append("  Umidità: ").append(String.format("%.2f", rs.getDouble("avg_umidita"))).append("%\n");
        result.append("  Pressione: ").append(String.format("%.2f", rs.getDouble("avg_pressione"))).append(" hPa\n");
        result.append("  Temperatura: ").append(String.format("%.2f", rs.getDouble("avg_temperatura"))).append(" °C\n");
        result.append("  Precipitazioni: ").append(String.format("%.2f", rs.getDouble("avg_precipitazioni"))).append(" mm\n");
        result.append("  Altitudine: ").append(String.format("%.2f", rs.getDouble("avg_altitudine"))).append(" m\n");
        result.append("  Massa ghiacciai: ").append(String.format("%.2f", rs.getDouble("avg_massa_ghiacciai"))).append(" kg/m³\n\n");
    }

    /**
     * Aggiunge i dettagli delle singole rilevazioni climatiche.
     *
     * Supporta due modalità di visualizzazione:
     * - Con informazioni sull'operatore
     * - Senza informazioni sull'operatore
     *
     * @param result StringBuilder su cui appendere i dettagli
     * @param rs ResultSet contenente i dati delle rilevazioni
     * @param op Flag per includere informazioni sull'operatore
     * @throws SQLException In caso di errori durante l'accesso ai dati
     */
    private void appendDetails(StringBuilder result, ResultSet rs, boolean op) throws SQLException {
        result.append("=== Dettaglio rilevazioni ===\n\n");

        while (rs.next()) {
            if (op) {
                result.append("Operatore: ").append(rs.getString("nome_operatore"))
                        .append(" ").append(rs.getString("cognome_operatore")).append("\n");
            }

            result.append("Data rilevazione: ")
                    .append(new SimpleDateFormat("dd/MM/yyyy").format(rs.getTimestamp("data_rilevazione")))
                    .append("\n");

            appendParameterDetails(result, rs);
            result.append("----------------------------------------\n");
        }
    }

    /**
     * Aggiunge i dettagli dei singoli parametri climatici di una rilevazione.
     *
     * Formatta e inserisce i valori puntuali di:
     * - Vento
     * - Umidità
     * - Pressione
     * - Temperatura
     * - Precipitazioni
     * - Altitudine
     * - Massa ghiacciai
     *
     * Include anche eventuali note aggiuntive.
     *
     * @param result StringBuilder su cui appendere i parametri
     * @param rs ResultSet contenente i dati dei parametri
     * @throws SQLException In caso di errori durante l'accesso ai dati
     */
    private void appendParameterDetails(StringBuilder result, ResultSet rs) throws SQLException {
        result.append("Parametri rilevati:\n");
        result.append("  Vento: ").append(String.format("%.2f", rs.getDouble("vento"))).append(" m/s\n");
        result.append("  Umidità: ").append(String.format("%.2f", rs.getDouble("umidita"))).append("%\n");
        result.append("  Pressione: ").append(String.format("%.2f", rs.getDouble("pressione"))).append(" hPa\n");
        result.append("  Temperatura: ").append(String.format("%.2f", rs.getDouble("temperatura"))).append(" °C\n");
        result.append("  Precipitazioni: ").append(String.format("%.2f", rs.getDouble("precipitazioni"))).append(" mm\n");
        result.append("  Altitudine: ").append(String.format("%.2f", rs.getDouble("altitudine"))).append(" m\n");
        result.append("  Massa ghiacciai: ").append(String.format("%.2f", rs.getDouble("massa_ghiacciai"))).append(" kg/m³\n");

        String note = rs.getString("note");
        if (note != null && !note.trim().isEmpty()) {
            result.append("Note: ").append(note).append("\n");
        }
    }

    /**
     * Recupera e aggiunge i commenti più recenti degli operatori.
     *
     * Estrae fino a 5 commenti più recenti associati a un'area di monitoraggio,
     * ordinati per data decrescente.
     *
     * @param result StringBuilder su cui appendere i commenti
     * @param id Identificativo dell'area di monitoraggio
     * @param idColonna Nome della colonna per l'identificazione
     * @throws SQLException In caso di errori durante l'accesso al database
     */
    private void appendCommentiOperatori(StringBuilder result, int id, String idColonna) throws SQLException {

        String sql = "SELECT note, data_rilevazione FROM parametriclimatici " +
                "WHERE " + idColonna + " = ? " +
                "AND note IS NOT NULL AND note != '' " +
                "ORDER BY data_rilevazione DESC LIMIT 5";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                result.append("\n=== Commenti recenti degli operatori ===\n");
                boolean hasComments = false;

                while (rs.next()) {
                    // Aggiungo anche la data del commento per maggiore chiarezza
                    result.append("- [")
                            .append(new SimpleDateFormat("dd/MM/yyyy").format(rs.getTimestamp("data_rilevazione")))
                            .append("] ")
                            .append(rs.getString("note"))
                            .append("\n");
                    hasComments = true;
                }

                if (!hasComments) {
                    result.append("Nessun commento disponibile.\n");
                }
                result.append("\n");
            }
        }
    }

    /**
     * Visualizza i dettagli di un'area di interesse associata a un centro di monitoraggio.
     *
     * Questo metodo recupera le informazioni complete di un'area di interesse:
     * - Dettagli geografici (nome, stato, latitudine, longitudine)
     * - Informazioni sul centro di monitoraggio associato
     * - Parametri climatici dell'area
     * - Commenti degli operatori
     *
     * Processo:
     * 1. Verifica la validità di nome e stato
     * 2. Esegue una query SQL per recuperare i dettagli dell'area
     * 3. Aggiunge parametri climatici se disponibili
     * 4. Aggiunge commenti degli operatori
     *
     * @param nome Nome dell'area di interesse
     * @param stato Stato dell'area di interesse
     * @return Stringa dettagliata con tutte le informazioni dell'area
     * @throws RemoteException Se si verificano errori durante il recupero dei dati
     * @throws IllegalArgumentException Se nome o stato sono nulli o vuoti
     */
    @Override
    public String visualizzaAreaCentroMonitoraggio(String nome, String stato) throws RemoteException {
        if (nome == null || nome.trim().isEmpty() || stato == null || stato.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e stato non possono essere nulli o vuoti");
        }

        String sql = "SELECT ai.id, ai.nome, ai.stato, ai.latitudine, ai.longitudine, " +
                "ai.centro_monitoraggio_id, cm.nome AS centro_nome " +
                "FROM areeinteresse ai " +
                "JOIN centrimonitoraggio cm ON ai.centro_monitoraggio_id = cm.id " +
                "WHERE ai.nome = ? AND ai.stato = ?";

        StringBuilder result = new StringBuilder();

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, nome);
            pstmt.setString(2, stato);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result.append("=== Informazioni Area di Interesse ===\n\n");
                    result.append("  ID: ").append(rs.getInt("id")).append("\n");
                    result.append("  Nome: ").append(rs.getString("nome")).append("\n");
                    result.append("  Centro Monitoraggio: ").append(rs.getString("centro_nome")).append("\n");
                    result.append("  Centro Monitoraggio ID: ").append(rs.getInt("centro_monitoraggio_id")).append("\n");
                    result.append("  Stato: ").append(rs.getString("stato")).append("\n");
                    result.append("  Latitudine: ").append(rs.getDouble("latitudine")).append("\n");
                    result.append("  Longitudine: ").append(rs.getDouble("longitudine")).append("\n");


                    int areaInteresseId = rs.getInt("id");
                    boolean hasParametri = appendParametriClimatici(result, areaInteresseId, "area_interesse_id");

                    if (!hasParametri) {
                        result.append("\nNessun dato climatico disponibile per questa area.\n");
                    }


                    appendCommentiOperatori(result, areaInteresseId, "area_interesse_id");
                } else {
                    result.append("Area di interesse non trovata per: ")
                            .append(nome)
                            .append(", ")
                            .append(stato);
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Errore durante la visualizzazione dell'area di interesse: " + e.getMessage(), e);
        }

        return result.toString();
    }

    /**
     * Registra un nuovo operatore nel sistema di monitoraggio climatico.
     *
     * Questo metodo inserisce i dati di un nuovo operatore nel database:
     * - Nome e cognome
     * - Codice fiscale
     * - Email
     * - Credenziali di accesso (userId e password)
     *
     * Caratteristiche:
     * - Utilizza una prepared statement per l'inserimento sicuro
     * - Restituisce un booleano che indica il successo dell'operazione
     *
     * @param nome Nome dell'operatore
     * @param cognome Cognome dell'operatore
     * @param codiceFiscale Codice fiscale dell'operatore
     * @param email Email dell'operatore
     * @param userId Identificativo utente per l'accesso
     * @param password Password di accesso
     * @return {@code true} se la registrazione ha successo, {@code false} altrimenti
     * @throws RemoteException Se si verificano errori durante la registrazione
     */
    @Override
    public boolean registrazione(String nome, String cognome, String codiceFiscale, String email, String userId, String password) throws RemoteException {
        String sql = "INSERT INTO operatoriregistrati (nome, cognome, codice_fiscale, email, userid, password) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, codiceFiscale);
            pstmt.setString(4, email);
            pstmt.setString(5, userId);
            pstmt.setString(6, password);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RemoteException("Errore durante la registrazione", e);
        }
    }


    /**
     * Recupera le informazioni di un operatore dato il suo identificativo utente.
     *
     * Questo metodo cerca nel database un operatore con lo specifico userId:
     * - Restituisce un oggetto OperatoriRegistrati se trovato
     * - Restituisce {@code null} se nessun utente corrisponde
     *
     * @param userId Identificativo utente da cercare
     * @return Oggetto OperatoriRegistrati con i dettagli dell'utente, o {@code null}
     * @throws RemoteException Se si verificano errori durante la ricerca
     */
    @Override
    public OperatoriRegistrati getUserById(String userId) throws RemoteException {
        String sql = "SELECT * FROM operatoriregistrati WHERE userid = ?";

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new OperatoriRegistrati(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("codice_fiscale"),
                            rs.getString("email"),
                            rs.getString("userid"),
                            rs.getString("password")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RemoteException("Errore durante il recupero dell'utente", e);
        }
    }

    /**
     * Crea un nuovo centro di monitoraggio per un operatore.
     *
     * Processo di creazione:
     * 1. Verifica che l'operatore non abbia già un centro
     * 2. Inserisce i dettagli del nuovo centro nel database
     * 3. Gestisce eventuali errori di inserimento
     *
     * Dettagli richiesti:
     * - ID dell'operatore
     * - Nome del centro
     * - Indirizzo completo (indirizzo, CAP, comune, provincia)
     *
     * @param operatoreId ID dell'operatore che crea il centro
     * @param nome Nome del centro di monitoraggio
     * @param indirizzo Indirizzo del centro
     * @param cap Codice di Avviamento Postale
     * @param comune Comune di ubicazione
     * @param provincia Provincia
     * @return {@code true} se il centro è stato creato con successo
     * @throws RemoteException Se l'operatore ha già un centro o se ci sono errori di inserimento
     */
    @Override
    public boolean creaCentroMonitoraggio(int operatoreId, String nome, String indirizzo, String cap, String comune, String provincia) throws RemoteException {
        try {
            String verificaQuery = "SELECT id FROM centrimonitoraggio WHERE operatore_id = ?";
            PreparedStatement verificaStmt = dbManager.getConnection().prepareStatement(verificaQuery);
            verificaStmt.setInt(1, operatoreId);
            ResultSet rs = verificaStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                throw new RemoteException("L'utente ha già registrato un centro di monitoraggio");
            }

            String query = "INSERT INTO centrimonitoraggio (operatore_id, nome, indirizzo, cap, comune, provincia) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, operatoreId);
            stmt.setString(2, nome);
            stmt.setString(3, indirizzo);
            stmt.setString(4, cap);
            stmt.setString(5, comune);
            stmt.setString(6, provincia);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Centro di monitoraggio creato con successo per operatore ID: " + operatoreId);
                return true;
            } else {
                System.out.println("Nessun centro di monitoraggio creato");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Errore SQL durante la creazione del centro: " + e.getMessage());
            throw new RemoteException("Errore durante la creazione del centro di monitoraggio", e);
        }
    }

    /**
     * Crea una nuova area di interesse per un centro di monitoraggio.
     *
     * Processo di creazione:
     * 1. Recupera l'ID del centro di monitoraggio dell'operatore
     * 2. Verifica l'esistenza del centro
     * 3. Inserisce i dettagli della nuova area nel database
     *
     * Dettagli richiesti:
     * - ID dell'operatore
     * - Nome della città
     * - Stato
     * - Coordinate geografiche (latitudine e longitudine)
     *
     * @param operatoreId ID dell'operatore che crea l'area
     * @param citta Nome della città
     * @param stato Stato della città
     * @param latitudine Latitudine geografica
     * @param longitudine Longitudine geografica
     * @return {@code true} se l'area è stata creata con successo
     * @throws RemoteException Se non esiste un centro per l'operatore o ci sono errori di inserimento
     */
    @Override
    public boolean creaAreaInteresse(int operatoreId, String citta, String stato, double latitudine, double longitudine) throws RemoteException {
        int centroId = getCentroMonitoraggio(operatoreId);

        if (centroId == -1) {
            throw new RemoteException("Centro di monitoraggio mancante");
        }

        try {
            String query = "INSERT INTO areeinteresse (nome, stato, centro_monitoraggio_id, latitudine, longitudine) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, citta);
            stmt.setString(2, stato);
            stmt.setInt(3, centroId);
            stmt.setDouble(4, latitudine);
            stmt.setDouble(5, longitudine);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RemoteException("Errore durante la creazione dell'area di interesse", e);
        }
    }


    /**
     * Recupera l'ID del centro di monitoraggio associato a un operatore.
     *
     * Questo metodo ausiliario:
     * - Cerca nel database il centro di un determinato operatore
     * - Restituisce l'ID del centro se trovato
     * - Restituisce -1 se nessun centro è associato all'operatore
     *
     * @param operatoreId ID dell'operatore
     * @return ID del centro di monitoraggio o -1 se non trovato
     * @throws RemoteException Se si verificano errori durante la ricerca
     */
    public int getCentroMonitoraggio(int operatoreId) throws RemoteException {
        try {
            String query = "SELECT id FROM centrimonitoraggio WHERE operatore_id = ?";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setInt(1, operatoreId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int centroId = rs.getInt("id");
                System.out.println("Trovato centro di monitoraggio con ID " + centroId + " per operatore ID " + operatoreId);
                return centroId;
            } else {
                System.out.println("Nessun centro di monitoraggio trovato per l'operatore con ID " + operatoreId);
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL durante la ricerca del centro: " + e.getMessage());
            throw new RemoteException("Errore durante la ricerca del centro di monitoraggio", e);
        }
    }

    /**
     * Inserisce parametri climatici completi nel sistema.
     *
     * Questo metodo permette l'inserimento dettagliato di dati climatici:
     * - Verifica l'esistenza del centro di monitoraggio
     * - Opzionalmente verifica area di interesse o coordinate
     * - Inserisce dati come vento, umidità, temperatura, ecc.
     *
     * Parametri inclusi:
     * - Centro di monitoraggio
     * - Area di interesse (opzionale)
     * - Coordinate di monitoraggio (opzionali)
     * - Data di rilevazione
     * - Parametri climatici puntuali
     * - Note aggiuntive
     *
     * @param centroMonitoraggioId ID del centro di monitoraggio
     * @param areaInteresseId ID dell'area di interesse (opzionale)
     * @param coordinateMonitoraggioId ID delle coordinate (opzionale)
     * @param dataRilevazione Data della rilevazione
     * @param vento Velocità del vento
     * @param umidita Percentuale di umidità
     * @param pressione Pressione atmosferica
     * @param temperatura Temperatura
     * @param precipitazioni Quantità di precipitazioni
     * @param altitudine Altitudine
     * @param massaGhiacciai Massa dei ghiacciai
     * @param note Note aggiuntive
     * @return {@code true} se l'inserimento ha avuto successo
     * @throws RemoteException Se si verificano errori durante l'inserimento
     */
    @Override
    public boolean inserisciParametriClimatici(int centroMonitoraggioId, Integer areaInteresseId,
                                               Integer coordinateMonitoraggioId, Date dataRilevazione,
                                               int vento, int umidita, int pressione, int temperatura,
                                               int precipitazioni, int altitudine, int massaGhiacciai,
                                               String note) throws RemoteException {
        try {
            // Verifica l'esistenza del centro monitoraggio
            String checkCentroSql = "SELECT id FROM centrimonitoraggio WHERE id = ?";
            PreparedStatement checkStmt = dbManager.getConnection().prepareStatement(checkCentroSql);
            checkStmt.setInt(1, centroMonitoraggioId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                throw new RemoteException("Il centro di monitoraggio con ID " + centroMonitoraggioId + " non esiste.");
            }

            // Verifica l'area di interesse se specificata
            if (areaInteresseId != null) {
                String checkAreaSql = "SELECT id FROM areeinteresse WHERE id = ?";
                checkStmt = dbManager.getConnection().prepareStatement(checkAreaSql);
                checkStmt.setInt(1, areaInteresseId);
                rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    throw new RemoteException("L'area di interesse con ID " + areaInteresseId + " non esiste.");
                }
            }

            // Verifica le coordinate di monitoraggio se specificate
            if (coordinateMonitoraggioId != null) {
                String checkCoordSql = "SELECT id FROM coordinatemonitoraggio WHERE id = ?";
                checkStmt = dbManager.getConnection().prepareStatement(checkCoordSql);
                checkStmt.setInt(1, coordinateMonitoraggioId);
                rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    throw new RemoteException("Le coordinate di monitoraggio con ID " + coordinateMonitoraggioId + " non esistono.");
                }
            }

            // Se tutte le verifiche passano, procedi con l'inserimento
            String sql = "INSERT INTO parametriclimatici (centro_monitoraggio_id, area_interesse_id, " +
                    "coordinate_monitoraggio_id, data_rilevazione, vento, umidita, pressione, " +
                    "temperatura, precipitazioni, altitudine, massa_ghiacciai, note) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);

            pstmt.setInt(1, centroMonitoraggioId);

            if (areaInteresseId != null) {
                pstmt.setInt(2, areaInteresseId);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (coordinateMonitoraggioId != null) {
                pstmt.setInt(3, coordinateMonitoraggioId);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setTimestamp(4, new Timestamp(dataRilevazione.getTime()));
            pstmt.setInt(5, vento);
            pstmt.setInt(6, umidita);
            pstmt.setInt(7, pressione);
            pstmt.setInt(8, temperatura);
            pstmt.setInt(9, precipitazioni);
            pstmt.setInt(10, altitudine);
            pstmt.setInt(11, massaGhiacciai);
            pstmt.setString(12, note);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Errore SQL durante l'inserimento:");
            System.err.println("Stato: " + e.getSQLState());
            System.err.println("Codice errore: " + e.getErrorCode());
            System.err.println("Messaggio: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Errore nell'inserimento dei parametri climatici: " + e.getMessage(), e);
        }
    }

    /**
     * Autentica un operatore nel sistema di monitoraggio climatico.
     *
     * Questo metodo verifica le credenziali di un operatore:
     * - Confronta userId e password con i record nel database
     * - Restituisce {@code true} se le credenziali sono corrette
     *
     * @param userId Identificativo utente
     * @param password Password di accesso
     * @return {@code true} se l'autenticazione ha successo
     * @throws RemoteException Se si verificano errori durante l'autenticazione
     */
    @Override
    public boolean autenticaOperatore(String userId, String password) throws RemoteException {
        String sql = "SELECT * FROM operatoriregistrati WHERE userid = ? AND password = ?";

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, userId);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera tutte le coordinate di monitoraggio.
     *
     * Questo metodo restituisce una lista di coordinate:
     * - Attualmente recupera tutte le coordinate, indipendentemente dal centro
     * - Ogni coordinata include dettagli come nome città, stato, paese, latitudine e longitudine
     *
     * @param centroMonitoraggioId ID del centro di monitoraggio (attualmente non utilizzato)
     * @return Lista di {@link CoordinateMonitoraggio}
     * @throws RemoteException Se si verificano errori durante il recupero
     */
    @Override
    public List<CoordinateMonitoraggio> getAreePerCentroMonitoraggio(int centroMonitoraggioId) throws RemoteException {
        List<CoordinateMonitoraggio> aree = new ArrayList<>();

//        String query = """
//        SELECT a.*
//        FROM areeinteresse a
//        WHERE a.centro_monitoraggio_id = ?
//    """;
        String queryCoordinate = "SELECT * FROM coordinatemonitoraggio";

        try {
            Connection conn = dbManager.getConnection();
//            PreparedStatement pstmtAree = conn.prepareStatement(query);
            PreparedStatement pstmtCoord = conn.prepareStatement(queryCoordinate);

//            pstmtAree.setInt(1, centroMonitoraggioId);
//            try {
//                ResultSet rsAree = pstmtAree.executeQuery();
//                while (rsAree.next()) {
//                    CoordinateMonitoraggio area = new CoordinateMonitoraggio(
//                            rsAree.getInt("id"),
//                            rsAree.getString("nome"),
//                            rsAree.getString("stato"),
//                            "", // paese non presente nelle aree di interesse
//                            rsAree.getDouble("latitudine"),
//                            rsAree.getDouble("longitudine")
//                    );
//                    aree.add(area);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            try {
                ResultSet rsCoord = pstmtCoord.executeQuery();
                while (rsCoord.next()) {
                    CoordinateMonitoraggio coord = new CoordinateMonitoraggio(
                            rsCoord.getInt("id"),
                            rsCoord.getString("nome_citta"),
                            rsCoord.getString("stato"),
                            rsCoord.getString("paese"),
                            rsCoord.getDouble("latitudine"),
                            rsCoord.getDouble("longitudine")
                    );
                    aree.add(coord);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Errore nel recupero delle aree", e);
        }

        return aree;
    }

    /**
     * Recupera le aree di interesse di un operatore.
     *
     * Questo metodo:
     * - Cerca le aree di interesse associate al centro di monitoraggio dell'operatore
     * - Restituisce una lista di {@link CoordinateMonitoraggio}
     * - Stampa a console le aree trovate per scopi di debug
     *
     * @param operatoreId ID dell'operatore
     * @return Lista delle aree di interesse dell'operatore
     * @throws RemoteException Se si verificano errori durante il recupero
     */
    @Override
    public List<CoordinateMonitoraggio> getAreeInteresseOperatore(int operatoreId) throws RemoteException {

        List<CoordinateMonitoraggio> areeInteresse = new ArrayList<>();

        String query = """
        SELECT a.* 
        FROM areeinteresse a 
        WHERE a.centro_monitoraggio_id = ?
    """;

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, operatoreId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CoordinateMonitoraggio area = new CoordinateMonitoraggio(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("centro_monitoraggio_id"),
                        rs.getString("stato"),
                        rs.getDouble("latitudine"),
                        rs.getDouble("longitudine")
                );
                areeInteresse.add(area);
            }


            if (areeInteresse.isEmpty()) {
                System.out.println("Nessuna area di interesse trovata per l'operatore ID: " + operatoreId);
            } else {
                for (CoordinateMonitoraggio area : areeInteresse) {
                    System.out.println("Area trovata: " + area.getNomeCitta());
                }
            }

            return areeInteresse;

        } catch (SQLException e) {
            String errorMsg = "Errore nel recupero delle aree di interesse per l'operatore " + operatoreId;
            System.err.println(errorMsg + ": " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException(errorMsg, e);
        }
    }


    /**
     * Inserisce parametri climatici per un'area di interesse.
     *
     * Simile a {@link #inserisciParametriClimatici}, ma semplificato:
     * - Focalizzato sull'inserimento per un'area di interesse
     * - Meno parametri rispetto al metodo più completo
     *
     * @param centroMonitoraggioId ID del centro di monitoraggio
     * @param areaInteresseId ID dell'area di interesse
     * @param dataRilevazione Data della rilevazione
     * @param vento Velocità del vento
     * @param umidita Percentuale di umidità
     * @param pressione Pressione atmosferica
     * @param temperatura Temperatura
     * @param precipitazioni Quantità di precipitazioni
     * @param altitudine Altitudine
     * @param massaGhiacciai Massa dei ghiacciai
     * @param note Note aggiuntive
     * @return {@code true} se l'inserimento ha avuto successo
     * @throws RemoteException Se si verificano errori durante l'inserimento
     */
    @Override
    public boolean inserisciParametriClimaticiArea(int centroMonitoraggioId, Integer areaInteresseId,
                                                   Date dataRilevazione, int vento, int umidita, int pressione, int temperatura,
                                                   int precipitazioni, int altitudine, int massaGhiacciai, String note) throws RemoteException {

        String sql = "INSERT INTO parametriclimatici "
                + "(centro_monitoraggio_id, area_interesse_id, data_rilevazione, "
                + "vento, umidita, pressione, temperatura, precipitazioni, "
                + "altitudine, massa_ghiacciai, note) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);


//            System.out.println("Inserimento parametri per area " + areaInteresseId
//                    + " del centro " + centroMonitoraggioId);

            pstmt.setInt(1, centroMonitoraggioId);
            if (areaInteresseId != null) {
                pstmt.setInt(2, areaInteresseId);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setTimestamp(3, new Timestamp(dataRilevazione.getTime()));
            pstmt.setInt(4, vento);
            pstmt.setInt(5, umidita);
            pstmt.setInt(6, pressione);
            pstmt.setInt(7, temperatura);
            pstmt.setInt(8, precipitazioni);
            pstmt.setInt(9, altitudine);
            pstmt.setInt(10, massaGhiacciai);
            pstmt.setString(11, note);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
            System.err.println("Stato: " + e.getSQLState());
            System.err.println("Codice errore: " + e.getErrorCode());
            e.printStackTrace();
            throw new RemoteException("Errore nell'inserimento dei parametri climatici", e);
        }
    }


}