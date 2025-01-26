package com.climatemonitoring.common.service;

import com.climatemonitoring.common.model.CoordinateMonitoraggio;
import com.climatemonitoring.common.model.OperatoriRegistrati;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Interfaccia del servizio per il monitoraggio climatico.
 *
 * Definisce i contratti per le operazioni remote di gestione
 * di aree geografiche, operatori, centri di monitoraggio e
 * parametri climatici.
 *
 * Questa interfaccia estende Remote per consentire chiamate
 * di metodi remoti attraverso RMI (Remote Method Invocation).
 *
 * @author
 */
public interface ClimateMonitoringService extends Remote {
    /**
     * Cerca un'area geografica utilizzando nome e stato.
     *
     * Permette di trovare le coordinate di monitoraggio
     * corrispondenti a un'area specifica.
     *
     * @param nome Nome dell'area geografica
     * @param stato Stato in cui si trova l'area
     * @return Lista delle coordinate di monitoraggio corrispondenti
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    List<CoordinateMonitoraggio> cercaAreaGeograficaNome(String nome, String stato) throws RemoteException;

    /**
     * Cerca aree geografiche all'interno di un determinato paese.
     *
     * Recupera tutte le coordinate di monitoraggio
     * presenti nel paese specificato.
     *
     * @param paese Nome del paese da esplorare
     * @return Lista delle coordinate di monitoraggio nel paese
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    List<CoordinateMonitoraggio> cercaAreaGeograficaPerPaese(String paese) throws RemoteException;

    /**
     * Cerca un'area geografica tramite coordinate geografiche.
     *
     * Individua le aree di monitoraggio in prossimità
     * delle coordinate specificate.
     *
     * @param latitudine Latitudine geografica
     * @param longitudine Longitudine geografica
     * @return Lista delle coordinate di monitoraggio vicine
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    List<CoordinateMonitoraggio> cercaAreaGeograficaCoordinate(Double latitudine, Double longitudine) throws RemoteException;

    /**
     * Visualizza i dettagli di un'area geografica.
     *
     * Genera una descrizione completa dell'area
     * identificata da nome e stato.
     *
     * @param nome Nome dell'area geografica
     * @param stato Stato in cui si trova l'area
     * @return Stringa descrittiva dell'area geografica
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    String visualizzaAreaGeografica(String nome, String stato) throws RemoteException;


    /**
     * Visualizza i dettagli di un centro di monitoraggio.
     *
     * Genera una descrizione completa del centro
     * identificato da nome e stato.
     *
     * @param nome Nome del centro di monitoraggio
     * @param stato Stato in cui si trova il centro
     * @return Stringa descrittiva del centro di monitoraggio
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    String visualizzaAreaCentroMonitoraggio(String nome, String stato) throws RemoteException;


    /**
     * Registra un nuovo operatore nel sistema.
     *
     * Esegue la procedura di registrazione con verifica
     * dei dati anagrafici e delle credenziali.
     *
     * @param nome Nome dell'operatore
     * @param cognome Cognome dell'operatore
     * @param codiceFiscale Codice fiscale dell'operatore
     * @param email Indirizzo email dell'operatore
     * @param userId Identificativo utente
     * @param password Password di accesso
     * @return true se la registrazione ha successo, false altrimenti
     * @throws RemoteException In caso di errori durante la comunicazione remota
     * @throws SQLException In caso di errori durante l'interazione con il database
     */
    boolean registrazione(String nome, String cognome,String codiceFiscale, String email,String userId, String password) throws RemoteException, SQLException;


    /**
     * Recupera i dati di un operatore tramite il suo user ID.
     *
     * @param userId Identificativo utente dell'operatore
     * @return Oggetto OperatoriRegistrati con i dati dell'operatore
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    OperatoriRegistrati getUserById(String userId) throws RemoteException;

    /**
     * Crea un nuovo centro di monitoraggio.
     *
     * Registra un centro con i dettagli geografici e
     * amministrativi forniti dall'operatore.
     *
     * @param operatoreId ID dell'operatore che crea il centro
     * @param nome Nome del centro di monitoraggio
     * @param indirizzo Indirizzo del centro
     * @param cap Codice di avviamento postale
     * @param comune Comune di appartenenza
     * @param provincia Provincia
     * @return true se la creazione ha successo, false altrimenti
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    boolean creaCentroMonitoraggio(int operatoreId, String nome, String indirizzo, String cap, String comune, String provincia) throws RemoteException; //registraCentroAree()


    /**
     * Crea una nuova area di interesse per il monitoraggio.
     *
     * Registra un'area geografica con le sue coordinate
     * precise e il contesto geografico.
     *
     * @param operatoreId ID dell'operatore che crea l'area
     * @param citta Città dell'area
     * @param stato Stato di appartenenza
     * @param latitudine Coordinata latitudinale
     * @param longitudine Coordinata longitudinale
     * @return true se la creazione ha successo, false altrimenti
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    boolean creaAreaInteresse(int operatoreId, String citta, String stato, double latitudine, double longitudine) throws RemoteException; //registraCentroAree()


    /**
     * Inserisce parametri climatici dettagliati per un'area specifica.
     *
     * Registra le misurazioni dei principali indicatori climatici
     * in un determinato momento e luogo.
     *
     * @param centroMonitoraggioId ID del centro di monitoraggio
     * @param areaInteresseId ID dell'area di interesse
     * @param coordinateMonitoraggioId ID specifico delle coordinate
     * @param dataRilevazione Data della rilevazione
     * @param vento Velocità del vento
     * @param umidita Percentuale di umidità
     * @param pressione Pressione atmosferica
     * @param temperatura Temperatura
     * @param precipitazioni Livello di precipitazioni
     * @param altitudine Altitudine del rilevamento
     * @param massaGhiacciai Stato dei ghiacciai
     * @param note Note aggiuntive sulla rilevazione
     * @return true se l'inserimento ha successo, false altrimenti
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    boolean inserisciParametriClimatici(int centroMonitoraggioId, Integer areaInteresseId, Integer coordinateMonitoraggioId, Date dataRilevazione,
                                        int vento, int umidita, int pressione, int temperatura,
                                        int precipitazioni, int altitudine, int massaGhiacciai, String note) throws RemoteException;

    /**
     * Autentica un operatore nel sistema.
     *
     * Verifica le credenziali di accesso dell'operatore.
     *
     * @param userId Identificativo utente
     * @param password Password di accesso
     * @return true se l'autenticazione ha successo, false altrimenti
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    boolean autenticaOperatore(String userId, String password) throws RemoteException;


    /**
     * Recupera le aree di interesse associate a un centro di monitoraggio.
     *
     * @param centroMonitoraggioId ID del centro di monitoraggio
     * @return Lista delle coordinate di monitoraggio del centro
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    List<CoordinateMonitoraggio> getAreePerCentroMonitoraggio(int centroMonitoraggioId) throws RemoteException;

    /**
     * Recupera le aree di interesse create da un operatore.
     *
     * @param operatoreId ID dell'operatore
     * @return Lista delle coordinate di monitoraggio create dall'operatore
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    List<CoordinateMonitoraggio> getAreeInteresseOperatore(int operatoreId) throws RemoteException;


    /**
     * Inserisce parametri climatici per un'area specifica.
     *
     * Versione semplificata di inserisciParametriClimatici,
     * che non richiede l'ID specifico delle coordinate.
     *
     * @param centroMonitoraggioId ID del centro di monitoraggio
     * @param areaInteresseId ID dell'area di interesse
     * @param dataRilevazione Data della rilevazione
     * @param vento Velocità del vento
     * @param umidita Percentuale di umidità
     * @param pressione Pressione atmosferica
     * @param temperatura Temperatura
     * @param precipitazioni Livello di precipitazioni
     * @param altitudine Altitudine del rilevamento
     * @param massaGhiacciai Stato dei ghiacciai
     * @param note Note aggiuntive sulla rilevazione
     * @return true se l'inserimento ha successo, false altrimenti
     * @throws RemoteException In caso di errori durante la comunicazione remota
     */
    boolean inserisciParametriClimaticiArea(int centroMonitoraggioId, Integer areaInteresseId, Date dataRilevazione,
                                            int vento, int umidita, int pressione, int temperatura,
                                            int precipitazioni, int altitudine, int massaGhiacciai, String note) throws RemoteException;
}
