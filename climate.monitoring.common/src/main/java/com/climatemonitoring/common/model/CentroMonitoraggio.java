package com.climatemonitoring.common.model;

import java.io.Serializable;


/**
 * Rappresenta un Centro di Monitoraggio nel sistema di Monitoraggio Climatico.
 *
 * Questa classe contiene le informazioni relative ai dettagli di un centro di monitoraggio,
 * inclusi i suoi identificativi e le informazioni sulla posizione.
 *
 * Implementa l'interfaccia Serializable per consentire la trasmissione dell'oggetto attraverso le reti.
 */
public class CentroMonitoraggio implements Serializable {
    /** Identificatore univoco per la serializzazione. */
    private static final long serialVersionUID=1l;

    /** Identificatore univoco del centro di monitoraggio. */
    private int id;

    /** Nome del centro di monitoraggio. */
    private String nome;

    /** Indirizzo stradale del centro di monitoraggio. */
    private String indirizzo;

    /** Codice postale del centro di monitoraggio. */
    private String cap;

    /** Comune in cui è ubicato il centro di monitoraggio. */
    private String comune;

    /** Provincia in cui è ubicato il centro di monitoraggio. */
    private String provincia;

    /**
     * Costruisce un nuovo Centro di Monitoraggio con tutti i dettagli.
     *
     * @param id Identificatore univoco del centro
     * @param nome Nome del centro
     * @param indirizzo Indirizzo stradale
     * @param cap Codice postale
     * @param comune Comune
     * @param provincia Provincia
     */
    public CentroMonitoraggio(int id, String nome, String indirizzo, String cap, String comune, String provincia) {
        this.id = id;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.cap = cap;
        this.comune = comune;
        this.provincia = provincia;
    }

    /**
     * Recupera l'identificatore univoco del centro di monitoraggio.
     *
     * @return L'ID del centro di monitoraggio
     */
    public int getId() { return id; }


    /**
     * Imposta l'identificatore univoco del centro di monitoraggio.
     *
     * @param id Il nuovo ID da assegnare al centro di monitoraggio
     */
    public void setId(int id) { this.id = id; }


    /**
     * Recupera il nome del centro di monitoraggio.
     *
     * @return Il nome del centro di monitoraggio
     */
    public String getNome() { return nome; }

    /**
     * Imposta il nome del centro di monitoraggio.
     *
     * @param nome Il nuovo nome da assegnare al centro di monitoraggio
     */
    public void setNome(String nome) { this.nome = nome; }

    /**
     * Recupera l'indirizzo stradale del centro di monitoraggio.
     *
     * @return L'indirizzo del centro di monitoraggio
     */
    public String getIndirizzo() { return indirizzo; }

    /**
     * Imposta l'indirizzo stradale del centro di monitoraggio.
     *
     * @param indirizzo Il nuovo indirizzo da assegnare al centro di monitoraggio
     */
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    /**
     * Recupera il codice postale del centro di monitoraggio.
     *
     * @return Il codice postale del centro di monitoraggio
     */
    public String getCap() { return cap; }

    /**
     * Imposta il codice postale del centro di monitoraggio.
     *
     * @param cap Il nuovo codice postale da assegnare al centro di monitoraggio
     */
    public void setCap(String cap) { this.cap = cap; }

    /**
     * Recupera il comune in cui è ubicato il centro di monitoraggio.
     *
     * @return Il comune del centro di monitoraggio
     */
    public String getComune() { return comune; }

    /**
     * Imposta il comune in cui è ubicato il centro di monitoraggio.
     *
     * @param comune Il nuovo comune da assegnare al centro di monitoraggio
     */
    public void setComune(String comune) { this.comune = comune; }

    /**
     * Recupera la provincia in cui è ubicato il centro di monitoraggio.
     *
     * @return La provincia del centro di monitoraggio
     */
    public String getProvincia() { return provincia; }

    /**
     * Imposta la provincia in cui è ubicato il centro di monitoraggio.
     *
     * @param provincia La nuova provincia da assegnare al centro di monitoraggio
     */
    public void setProvincia(String provincia) { this.provincia = provincia; }
}
