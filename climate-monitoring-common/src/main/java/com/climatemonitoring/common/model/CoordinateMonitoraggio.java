package com.climatemonitoring.common.model;

import java.io.Serializable;

public class CoordinateMonitoraggio implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nomeCitta;
    private String stato;
    private String paese;
    private double latitudine;
    private double longitudine;
    boolean isAreaInteresse;
    private Integer centroMonitoraggioId;
    private String tipo;

    public CoordinateMonitoraggio() {}

    public CoordinateMonitoraggio(int id, String nomeCitta, String stato, String paese,
                                  double latitudine, double longitudine) {
        this.id = id;
        this.nomeCitta = nomeCitta;
        this.stato = stato;
        this.paese = paese;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }


    public CoordinateMonitoraggio(int id, String nome, int centroMonitoraggioId, String stato, double latitudine, double longitudine, String tipo) {
        this.id=id;
        this.nomeCitta=nome;
        this.centroMonitoraggioId=centroMonitoraggioId;
        this.stato = stato;
         this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.tipo=tipo;
    }


    public Integer getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCitta() {
        return nomeCitta;
    }

    public void setNomeCitta(String nomeCitta) {
        this.nomeCitta = nomeCitta;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getPaese() {
        return paese;
    }

    public void setPaese(String paese) {
        this.paese = paese;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

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

    public Integer getCentroMonitoraggioId() {
        return centroMonitoraggioId;
    }

    public void setCentroMonitoraggioId(Integer centroMonitoraggioId) {
        this.centroMonitoraggioId = centroMonitoraggioId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isAreaInteresse() {
        return isAreaInteresse;
    }

    public void setAreaInteresse(boolean areaInteresse) {
        isAreaInteresse = areaInteresse;
    }
}