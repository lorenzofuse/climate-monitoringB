package com.climatemonitoring.common.model;

import java.io.Serializable;

/**
 * Rappresenta un operatore registrato nel sistema di monitoraggio climatico.
 *
 * Questa classe modella le informazioni di un operatore che ha completato la registrazione,
 * contenendo i suoi dati personali e di accesso. Implementa l'interfaccia Serializable
 * per consentire la serializzazione e deserializzazione degli oggetti.
 *
 * @author [Nome dell'autore]
 * @version 1.0
 * @since [Data di implementazione]
 */
public class OperatoriRegistrati implements Serializable {

    /**
     * Identificatore di versione per la serializzazione.
     * Garantisce la compatibilità durante la deserializzazione degli oggetti.
     */
    private static final long serialVersionUID=1l;

    /**
     * Identificatore univoco dell'operatore registrato.
     */
    private int id;

    /**
     * Nome dell'operatore registrato.
     */
    private String nome;

    /**
     * Cognome dell'operatore registrato.
     */
    private String cognome;

    /**
     * Codice fiscale dell'operatore registrato.
     */
    private String codice_fiscale;

    /**
     * Indirizzo email dell'operatore registrato.
     */
    private String email;

    /**
     * Nome utente (userid) utilizzato per l'accesso al sistema.
     */
    private String userid;

    /**
     * Password di accesso dell'operatore.
     *
     * @note È importante gestire questa informazione con procedure di sicurezza adeguate.
     */
    private String password;

    /**
     * Costruttore completo per creare un'istanza di OperatoriRegistrati.
     *
     * @param id Identificatore univoco dell'operatore
     * @param nome Nome dell'operatore
     * @param cognome Cognome dell'operatore
     * @param codice_fiscale Codice fiscale dell'operatore
     * @param email Indirizzo email dell'operatore
     * @param userid Nome utente per l'accesso
     * @param password Password di accesso
     */
    public OperatoriRegistrati(int id, String nome, String cognome, String codice_fiscale, String email, String userid, String password) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.codice_fiscale = codice_fiscale;
        this.email = email;
        this.userid = userid;
        this.password = password;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodice_fiscale() {
        return codice_fiscale;
    }

    public void setCodice_fiscale(String codice_fiscale) {
        this.codice_fiscale = codice_fiscale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

