package com.climatemonitoring.common.service;

import com.climatemonitoring.common.model.CoordinateMonitoraggio;
import com.climatemonitoring.common.model.OperatoriRegistrati;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface ClimateMonitoringService extends Remote {
    List<CoordinateMonitoraggio> cercaAreaGeograficaNome(String nome, String stato) throws RemoteException;

    List<CoordinateMonitoraggio> cercaAreaGeograficaPerPaese(String paese) throws RemoteException;
    List<CoordinateMonitoraggio> cercaAreaGeograficaCoordinate(Double latitudine, Double longitudine) throws RemoteException;

    String visualizzaAreaGeografica(String nome, String stato) throws RemoteException; //display tab coordinatemonitoraggio (cerca i paesi dentro il file fornito dal prof)

    String visualizzaAreaCentroMonitoraggio(String nome, String stato) throws RemoteException; //display area dentro centromonitoraggio (cerca dalle aree che l'operatore crea)

    boolean registrazione(String nome, String cognome,String codiceFiscale, String email,String userId, String password) throws RemoteException, SQLException;

    boolean verificaUser(String userId, String password) throws RemoteException;

    OperatoriRegistrati getUserById(String userId) throws RemoteException;

    boolean creaCentroMonitoraggio(int operatoreId, String nome, String indirizzo, String cap, String comune, String provincia) throws RemoteException; //registraCentroAree()

    boolean creaAreaInteresse(int operatoreId, String citta, String stato, double latitudine, double longitudine) throws RemoteException; //registraCentroAree()

    boolean inserisciParametriClimatici(int centroMonitoraggioId, Integer areaInteresseId, Integer coordinateMonitoraggioId, Date dataRilevazione,
                                        int vento, int umidita, int pressione, int temperatura,
                                        int precipitazioni, int altitudine, int massaGhiacciai, String note) throws RemoteException;

    boolean autenticaOperatore(String userId, String password) throws RemoteException;

    List<CoordinateMonitoraggio> getAreePerCentroMonitoraggio(int centroMonitoraggioId) throws RemoteException;

    Integer getAreaInteresseId(String nomeArea) throws RemoteException;

    List<CoordinateMonitoraggio> getAreeInteresseOperatore(int operatoreId) throws RemoteException;


    boolean insertClimateDataForArea(int centroMonitoraggioId, Integer areaInteresseId, Date dataRilevazione,
                                            int vento, int umidita, int pressione, int temperatura,
                                            int precipitazioni, int altitudine, int massaGhiacciai, String note) throws RemoteException;
    int getCentroMonitoraggioId(OperatoriRegistrati currentUser) throws RemoteException;
}
