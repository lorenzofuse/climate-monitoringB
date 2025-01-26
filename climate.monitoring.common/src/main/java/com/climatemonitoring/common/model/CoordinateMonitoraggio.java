package com.climatemonitoring.common.model;

import java.io.Serializable;

/**
 * Rappresenta le coordinate geografiche di un sito di monitoraggio nel sistema di Monitoraggio Climatico.
 *
 * Questa classe incapsula le informazioni dettagliate relative alla posizione geografica,
 * inclusi attributi come città, stato, paese, latitudine, longitudine e altri metadati.
 *
 * La classe supporta la serializzazione per consentire la trasmissione di oggetti
 * CoordinateMonitoraggio attraverso reti o per la persistenza dei dati.
 *
 * Gli oggetti di questa classe possono rappresentare sia aree di interesse specifiche
 * per il monitoraggio climatico che posizioni geografiche generiche.
 */
public class CoordinateMonitoraggio implements Serializable {
    /** Identificatore univoco per la serializzazione delle istanze di questa classe. */
    private static final long serialVersionUID = 1L;

    /** Identificatore univoco delle coordinate di monitoraggio. */
    private int id;

    /** Nome della città in cui sono ubicate le coordinate. */
    private String nomeCitta;

    /** Stato o regione in cui sono ubicate le coordinate. */
    private String stato;

    /** Paese in cui sono ubicate le coordinate. */
    private String paese;

    /** Latitudine delle coordinate geografiche. */
    private double latitudine;

    /** Longitudine delle coordinate geografiche. */
    private double longitudine;

    /**
     * Flag che indica se questa posizione è un'area di interesse specifico
     * per il monitoraggio climatico.
     */
    boolean isAreaInteresse;

    /**
     * Identificatore del centro di monitoraggio associato a queste coordinate.
     * Può essere nullo se non è associato a un centro specifico.
     */
    private Integer centroMonitoraggioId;

    /**
     * Tipo di area o punto di interesse (se applicabile).
     * Fornisce ulteriori dettagli sul contesto delle coordinate.
     */
    private String tipo;

    /**
     * Costruttore predefinito senza parametri.
     * Crea un'istanza di CoordinateMonitoraggio con valori predefiniti.
     */
    public CoordinateMonitoraggio() {}

    /**
     * Costruisce un oggetto CoordinateMonitoraggio con informazioni geografiche di base.
     *
     * @param id Identificatore univoco delle coordinate
     * @param nomeCitta Nome della città
     * @param stato Stato o regione
     * @param paese Paese
     * @param latitudine Latitudine geografica
     * @param longitudine Longitudine geografica
     */
    public CoordinateMonitoraggio(int id, String nomeCitta, String stato, String paese,
                                  double latitudine, double longitudine) {
        this.id = id;
        this.nomeCitta = nomeCitta;
        this.stato = stato;
        this.paese = paese;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }


    /**
     * Costruisce un oggetto CoordinateMonitoraggio con informazioni geografiche
     * e un collegamento a un centro di monitoraggio.
     *
     * @param id Identificatore univoco delle coordinate
     * @param nome Nome della città
     * @param centroMonitoraggioId Identificatore del centro di monitoraggio associato
     * @param stato Stato o regione
     * @param latitudine Latitudine geografica
     * @param longitudine Longitudine geografica
     */
    public CoordinateMonitoraggio(int id, String nome, int centroMonitoraggioId, String stato, double latitudine, double longitudine) {
        this.id=id;
        this.nomeCitta=nome;
        this.centroMonitoraggioId=centroMonitoraggioId;
        this.stato = stato;
         this.latitudine = latitudine;
        this.longitudine = longitudine;

    }

    /**
     * Recupera l'identificatore univoco delle coordinate di monitoraggio.
     *
     * @return L'ID delle coordinate
     */
    public Integer getId(){
        return id;
    }

    /**
     * Imposta l'identificatore univoco delle coordinate di monitoraggio.
     *
     * @param id Il nuovo ID da assegnare alle coordinate
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Recupera il nome della città associata a queste coordinate geografiche.
     *
     * Fornisce l'identificativo urbano del punto di monitoraggio,
     * permettendo una localizzazione più precisa.
     *
     * @return Il nome della città
     */
    public String getNomeCitta() {
        return nomeCitta;
    }

    /**
     * Imposta il nome della città per queste coordinate geografiche.
     *
     * Permette di aggiornare l'identificativo urbano del punto di monitoraggio,
     * garantendo la precisione della localizzazione.
     *
     * @param nomeCitta Il nome della città da associare alle coordinate
     */
    public void setNomeCitta(String nomeCitta) {
        this.nomeCitta = nomeCitta;
    }

    /**
     * Recupera lo stato o la regione associata a queste coordinate geografiche.
     *
     * Fornisce un livello di dettaglio geografico superiore al nome della città,
     * contestualizzando ulteriormente la posizione.
     *
     * @return Il nome dello stato o della regione
     */
    public String getStato() {
        return stato;
    }

    /**
     * Imposta lo stato o la regione per queste coordinate geografiche.
     *
     * Consente di aggiornare l'informazione geografica di livello superiore,
     * migliorando la precisione della localizzazione.
     *
     * @param stato Il nome dello stato o della regione da associare
     */
    public void setStato(String stato) {
        this.stato = stato;
    }

    /**
     * Recupera il nome del paese associato a queste coordinate geografiche.
     *
     * Fornisce il contesto nazionale più ampio per la localizzazione,
     * completando le informazioni geografiche.
     *
     * @return Il nome del paese
     */
    public String getPaese() {
        return paese;
    }

    /**
     * Imposta il nome del paese per queste coordinate geografiche.
     *
     * Permette di definire o aggiornare il contesto nazionale
     * delle coordinate di monitoraggio.
     *
     * @param paese Il nome del paese da associare
     */
    public void setPaese(String paese) {
        this.paese = paese;
    }

    /**
     * Recupera la latitudine delle coordinate geografiche.
     *
     * Restituisce la coordinata nord-sud che definisce la posizione
     * verticale del punto di monitoraggio sulla superficie terrestre.
     *
     * @return Il valore della latitudine in gradi decimali
     */
    public double getLatitudine() {
        return latitudine;
    }

    /**
     * Imposta la latitudine per queste coordinate geografiche.
     *
     * Consente di definire o modificare la posizione verticale
     * del punto di monitoraggio sulla superficie terrestre.
     *
     * @param latitudine Il valore della latitudine in gradi decimali
     */
    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    /**
     * Recupera la longitudine delle coordinate geografiche.
     *
     * Restituisce la coordinata est-ovest che definisce la posizione
     * orizzontale del punto di monitoraggio sulla superficie terrestre.
     *
     * @return Il valore della longitudine in gradi decimali
     */
    public double getLongitudine() {
        return longitudine;
    }

    /**
     * Imposta la longitudine per queste coordinate geografiche.
     *
     * Permette di definire o modificare la posizione orizzontale
     * del punto di monitoraggio sulla superficie terrestre.
     *
     * @param longitudine Il valore della longitudine in gradi decimali
     */
    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }


    /**
     * Genera una rappresentazione testuale delle coordinate di monitoraggio.
     *
     * Se le coordinate sono un'area di interesse, include anche il tipo di area.
     * Formatta latitudine e longitudine con due cifre decimali.
     *
     * @return Una stringa che descrive le coordinate geografiche
     */
    @Override
    public String toString() {
        if (isAreaInteresse) {
            return String.format("%s, %s - Tipo: %s (Lat: %.2f, Long: %.2f)",
                    nomeCitta, stato, tipo, latitudine, longitudine);
        } else {
            return String.format("%s, %s (Lat: %.2f, Long: %.2f)",
                    nomeCitta, stato, latitudine, longitudine);
        }
    }

    /**
     * Recupera l'identificatore del centro di monitoraggio associato a queste coordinate.
     *
     * Questo metodo restituisce l'ID del centro di monitoraggio che gestisce
     * o è responsabile di queste coordinate geografiche.
     *
     * @return L'identificatore del centro di monitoraggio, o null se non è associato
     */
    public Integer getCentroMonitoraggioId() {
        return centroMonitoraggioId;
    }

    /**
     * Imposta l'identificatore del centro di monitoraggio per queste coordinate.
     *
     * Questo metodo consente di stabilire una relazione tra le coordinate
     * geografiche e un centro di monitoraggio specifico.
     *
     * @param centroMonitoraggioId L'identificatore del centro di monitoraggio da associare
     */
    public void setCentroMonitoraggioId(Integer centroMonitoraggioId) {
        this.centroMonitoraggioId = centroMonitoraggioId;
    }


    /**
     * Recupera il tipo di area o punto di interesse per queste coordinate.
     *
     * Questo metodo restituisce una descrizione o classificazione specifica
     * del sito geografico, fornendo ulteriori dettagli sul contesto delle coordinate.
     *
     * @return Una stringa che descrive il tipo di area o punto di interesse
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Imposta il tipo di area o punto di interesse per queste coordinate.
     *
     * Permette di specificare una classificazione o descrizione dettagliata
     * del sito geografico, arricchendo le informazioni contestuali.
     *
     * @param tipo La descrizione del tipo di area o punto di interesse
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Verifica se le coordinate rappresentano un'area di interesse specifico.
     *
     * Un'area di interesse può indicare un sito particolarmente rilevante
     * per il monitoraggio climatico, che richiede un'attenzione speciale.
     *
     * @return true se le coordinate rappresentano un'area di interesse, false altrimenti
     */
    public boolean isAreaInteresse() {
        return isAreaInteresse;
    }

    /**
     * Imposta lo stato di area di interesse per le coordinate.
     *
     * Questo metodo consente di contrassegnare le coordinate come un'area
     * di particolare importanza per il monitoraggio climatico.
     *
     * @param areaInteresse Indica se le coordinate rappresentano un'area di interesse
     */
    public void setAreaInteresse(boolean areaInteresse) {
        isAreaInteresse = areaInteresse;
    }
}