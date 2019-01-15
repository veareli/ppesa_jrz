package com.daicon.pc.ppesa;

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

public class AdaptadorPedidos extends RecyclerView.Adapter<AdaptadorPedidos.ViewHolder> {
    public AdaptadorPedidos(ArrayList<Pedidos> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    ArrayList<Pedidos> listaPedidos;
    DetallesPedidoFragment detallesPedidoFragment;
    AppCompatActivity activity;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_pedidos,null,false);

        detallesPedidoFragment = new DetallesPedidoFragment();
        activity = (AppCompatActivity) view.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.fecha.setText("Fecha: " +listaPedidos.get(i).getFecha());
        viewHolder.referencia.setText("Referencia: " +listaPedidos.get(i).getReferencia());
        viewHolder.estatus.setText("Estatus:" + listaPedidos.get(i).getEstatus());
        viewHolder.vermas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction =  activity.getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putInt("idPedido", Integer.parseInt(listaPedidos.get(i).getReferencia()));
                args.putString("estatus", listaPedidos.get(i).getEstatus());
                args.putString("fecha", listaPedidos.get(i).getFecha());
                detallesPedidoFragment.setArguments(args);
                transaction.replace(R.id.contenedor, detallesPedidoFragment).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fecha, estatus, referencia;
        TextView vermas;

        public ViewHolder(View itemView) {
            super(itemView);

            fecha = itemView.findViewById(R.id.lblFecha);
            estatus = itemView.findViewById(R.id.lblEstatus);
            referencia = itemView.findViewById(R.id.lblReferencia);
            vermas = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}
