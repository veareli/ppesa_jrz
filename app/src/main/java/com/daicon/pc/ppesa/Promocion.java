package com.daicon.pc.ppesa;

import java.io.Serializable;

public class Promocion implements Serializable {

    private double Descuento;
    private String Producto;
    private String Fecha;
    private Productos ProductoObj;
    private String Texto;

    public Promocion() {

    }

    public double getDescuento() {
        return Descuento;
    }

    public void setDescuento(double descuento) {
        Descuento = descuento;
    }

    public String getProducto() {
        return Producto;
    }

    public void setProducto(String producto) {
        Producto = producto;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public Productos getProductoObj() {
        return ProductoObj;
    }

    public void setProductoObj(Productos productoObj) {
        ProductoObj = productoObj;
    }

    public String getTexto() {
        return Texto;
    }

    public void setTexto(String texto) {
        Texto = texto;
    }
}
