package com.daicon.pc.ppesa;

import java.util.Date;
public class EventObjects {
    private int id;
    private double factorValor;
    private String date;
    private String dateConFormato;
    private double costo;
    private int disponibilidad;

    public EventObjects(){

    }
    public EventObjects(double factorValor, String date) {
        this.setFactorValor(factorValor);
        this.setDate(date);
    }
    public EventObjects(int id, double factorValor, String date, int disponibilidad) {
        this.setDate(date);
        this.setFactorValor(factorValor);
        this.setId(id);
        this.setDisponibilidad(disponibilidad);
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

    public String getDateConFormato() {
        return dateConFormato;
    }

    public void setDateConFormato(String dateConFormato) {
        this.dateConFormato = dateConFormato;
    }

    public int getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(int disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}