package com.daicon.pc.ppesa;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorDetallesPedido extends RecyclerView.Adapter<AdaptadorDetallesPedido.ViewHolder> {
    ArrayList<Pedidos> listaDetallesPedidos;



    public AdaptadorDetallesPedido(ArrayList<Pedidos> listaDetallesPedidos) {
        this.listaDetallesPedidos = listaDetallesPedidos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_detalles_pedido, null, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.producto.setText(listaDetallesPedidos.get(i).getDescripcionProducto());
        viewHolder.cantidad.setText(" x "+String.valueOf(listaDetallesPedidos.get(i).getCantidad()));
        viewHolder.precio.setText("$"+String.valueOf(listaDetallesPedidos.get(i).getPrecio()));

        double descuento= listaDetallesPedidos.get(i).getPrecio();
        double costoFinal = listaDetallesPedidos.get(i).getCantidad() * listaDetallesPedidos.get(i).getPrecio();
        //costoFinal = descuento >0? ((100-descuento)/100) * costoFinal: costoFinal;
        viewHolder.costo.setText("$"+String.valueOf(Utilerias.limitarDecimales(costoFinal)));




    }

    @Override
    public int getItemCount() {
        return listaDetallesPedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView producto, cantidad, costo, precio;

        public ViewHolder(View itemView) {
            super(itemView);

            producto = itemView.findViewById(R.id.lblProductoDetalles);
            cantidad = itemView.findViewById(R.id.lblCantProductoDetalles);
            costo = itemView.findViewById(R.id.lblCostoProductoDetalles);
            precio = itemView.findViewById(R.id.lblPrecioProductoDetalles);


        }
    }
}
