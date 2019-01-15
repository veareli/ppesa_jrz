package com.daicon.pc.ppesa;

import java.io.Serializable;

public class Productos implements Serializable {

    private String Descripcion;
    private String Costo;
    private String Disponibles;
    private int ID;
    private int Linea;

    public Productos(String costo, String descripcion, String disponibles) {
        Descripcion = descripcion;
        Costo = costo;
        Disponibles = disponibles;
    }

    public Productos() {
    }


    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getCosto() {
        return Costo;
    }

    public void setCosto(String costo) {
        Costo = costo;
    }

    public String getDisponibles() {
        return Disponibles;
    }

    public void setDisponibles(String disponibles) {
        Disponibles = disponibles;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getLinea() {
        return Linea;
    }

    public void setLinea(int linea) {
        Linea = linea;
    }
}
