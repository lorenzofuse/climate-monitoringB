package com.climatemonitoring.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Rappresenta un insieme di parametri climatici rilevati in un determinato momento e luogo.
 *
 * Questa classe modella le misurazioni ambientali e climatiche raccolte durante un'attività
 * di monitoraggio. Implementa l'interfaccia Serializable per consentire la serializzazione
 * e deserializzazione degli oggetti, facilitando la memorizzazione e il trasferimento dei dati.
 *
 * I parametri includono diverse metriche fondamentali per comprendere le condizioni
 * climatiche e ambientali di un determinato sito di rilevazione.
 *
 * @author Fusè Lorenzo 753168
 * @author Ciminella Alessandro 753369
 * @author Dragan Cosmin 754427
 */
public class ParametriClimatici implements Serializable {
    /**
     * Identificatore di versione per la serializzazione.
     * Garantisce la compatibilità durante la deserializzazione degli oggetti.
     */
    private static final long serialVersionUID=1l;
    /**
     * Identificatore univoco della rilevazione dei parametri climatici.
     */
    private int id;
    /**
     * Data e ora in cui sono stati rilevati i parametri climatici.
     * Fornisce un contesto temporale preciso per la misurazione.
     */
    private Date data_rilevazione;
    /**
     * Velocità del vento misurata in un'unità standard (probabilmente km/h).
     */
    private int vento;
    /**
     * Percentuale di umidità relativa dell'aria.
     */
    private int umidita;
    /**
     * Pressione atmosferica misurata in un'unità standard (probabilmente millibar o hectopascal).
     */
    private int pressione;
    /**
     * Temperatura rilevata, probabilmente in gradi Celsius.
     */
    private int temperatura;
    /**
     * Quantità di precipitazioni, misurata in un'unità standard (probabilmente mm).
     */
    private int precipitazioni;
    /**
     * Altitudine del sito di rilevazione, probabilmente misurata in metri.
     */
    private int altitudine;
    /**
     * Massa dei ghiacciai, probabilmente misurata in tonnellate o chilometri cubi.
     * Questo parametro può essere cruciale per gli studi sui cambiamenti climatici.
     */
    private int massa_ghiacciai;
    /**
     * Note aggiuntive o commenti relativi alla rilevazione.
     * Può contenere informazioni contestuali o osservazioni specifiche.
     */
    private String note;

    /**
     * Costruttore completo per creare un'istanza di ParametriClimatici.
     *
     * @param id Identificatore univoco della rilevazione
     * @param data_rilevazione Data e ora della rilevazione
     * @param vento Velocità del vento
     * @param umidita Percentuale di umidità
     * @param pressione Pressione atmosferica
     * @param temperatura Temperatura rilevata
     * @param precipitazioni Quantità di precipitazioni
     * @param altitudine Altitudine del sito
     * @param massa_ghiacciai Massa dei ghiacciai
     * @param note Eventuali note aggiuntive
     */
    public ParametriClimatici(int id, Date data_rilevazione, int vento, int umidita, int pressione, int temperatura, int precipitazioni, int altitudine, int massa_ghiacciai, String note) {
        this.id = id;
        this.data_rilevazione = data_rilevazione;
        this.vento = vento;
        this.umidita = umidita;
        this.pressione = pressione;
        this.temperatura = temperatura;
        this.precipitazioni = precipitazioni;
        this.altitudine = altitudine;
        this.massa_ghiacciai = massa_ghiacciai;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getData_rilevazione() {
        return data_rilevazione;
    }

    public void setData_rilevazione(Date data_rilevazione) {
        this.data_rilevazione = data_rilevazione;
    }

    public int getVento() {
        return vento;
    }

    public void setVento(int vento) {
        this.vento = vento;
    }

    public int getUmidita() {
        return umidita;
    }

    public void setUmidita(int umidita) {
        this.umidita = umidita;
    }

    public int getPressione() {
        return pressione;
    }

    public void setPressione(int pressione) {
        this.pressione = pressione;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public int getPrecipitazioni() {
        return precipitazioni;
    }

    public void setPrecipitazioni(int precipitazioni) {
        this.precipitazioni = precipitazioni;
    }

    public int getAltitudine() {
        return altitudine;
    }

    public void setAltitudine(int altitudine) {
        this.altitudine = altitudine;
    }

    public int getMassa_ghiacciai() {
        return massa_ghiacciai;
    }

    public void setMassa_ghiacciai(int massa_ghiacciai) {
        this.massa_ghiacciai = massa_ghiacciai;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
