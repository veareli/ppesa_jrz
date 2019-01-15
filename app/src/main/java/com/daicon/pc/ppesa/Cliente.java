package com.daicon.pc.ppesa;

public class Cliente {

    private int ID;

    public Cliente() {
    }

    private String Nombre;
    private String Telefono;
    private String Whatsapp;
    private String tipoCliente;
    private int _idUsuario;
    private String DireccionEntrega;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getWhatsapp() {
        return Whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        Whatsapp = whatsapp;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public int get_idUsuario() {
        return _idUsuario;
    }

    public void set_idUsuario(int _idUsuario) {
        this._idUsuario = _idUsuario;
    }

    public String getDireccionEntrega() {
        return DireccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        DireccionEntrega = direccionEntrega;
    }
}
