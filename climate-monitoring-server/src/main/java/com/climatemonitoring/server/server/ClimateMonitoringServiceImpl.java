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

public class ClimateMonitoringServiceImpl extends UnicastRemoteObject implements ClimateMonitoringService {

    private final DatabaseManager dbManager;

    public ClimateMonitoringServiceImpl(DatabaseManager dbManager) throws RemoteException {
        super();
        this.dbManager = dbManager;
    }

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


    @Override
    public String visualizzaAreaCentroMonitoraggio(String nome, String stato) throws RemoteException {
        if (nome == null || nome.trim().isEmpty() || stato == null || stato.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e stato non possono essere nulli o vuoti");
        }

        String sql = "SELECT ai.id, ai.nome, ai.stato, ai.latitudine, ai.longitudine, " +
                "ai.tipo, ai.centro_monitoraggio_id, cm.nome AS centro_nome " +
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
                    result.append("  Tipo: ").append(rs.getString("tipo")).append("\n\n");


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


    @Override
    public List<CoordinateMonitoraggio> getAreePerCentroMonitoraggio(int centroMonitoraggioId) throws RemoteException {
        List<CoordinateMonitoraggio> aree = new ArrayList<>();

        String queryAreeInteresse = "SELECT a.* FROM areeinteresse a " +
                "WHERE a.centro_monitoraggio_id = ?";

        String queryCoordinate = "SELECT * FROM coordinatemonitoraggio";

        try {
            Connection conn = dbManager.getConnection();
            PreparedStatement pstmtAree = conn.prepareStatement(queryAreeInteresse);
            PreparedStatement pstmtCoord = conn.prepareStatement(queryCoordinate);

            pstmtAree.setInt(1, centroMonitoraggioId);
            try {
                ResultSet rsAree = pstmtAree.executeQuery();
                while (rsAree.next()) {
                    CoordinateMonitoraggio area = new CoordinateMonitoraggio(
                            rsAree.getInt("id"),
                            rsAree.getString("nome"),
                            rsAree.getString("stato"),
                            "", // paese non presente nelle aree di interesse
                            rsAree.getDouble("latitudine"),
                            rsAree.getDouble("longitudine")
                    );
                    aree.add(area);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    @Override
    public List<CoordinateMonitoraggio> getAreeInteresseOperatore(int operatoreId) throws RemoteException {

        List<CoordinateMonitoraggio> areeInteresse = new ArrayList<>();

        String query = """
                SELECT a.*
                FROM areeinteresse a
                JOIN operatoriregistrati o ON a.centro_monitoraggio_id = o.centro_monitoraggio_id
                WHERE o.id = ?
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
                        rs.getDouble("longitudine"),
                        rs.getString("tipo")
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
