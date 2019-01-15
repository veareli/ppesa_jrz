package com.daicon.pc.ppesa;

import java.util.Date;
public class EventObjects {
    private int id;
    private double factorValor;
    private String date;
    private double costo;

    public EventObjects(){

    }
    public EventObjects(double factorValor, String date) {
        this.setFactorValor(factorValor);
        this.setDate(date);
    }
    public EventObjects(int id, double factorValor, String date) {
        this.setDate(date);
        this.setFactorValor(factorValor);
        this.setId(id);
    }
    public int getId() {
        return id;
    }
    public double getFactorValor() {
        return factorValor;
    }
    public String getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFactorValor(double factorValor) {
        this.factorValor = factorValor;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
}