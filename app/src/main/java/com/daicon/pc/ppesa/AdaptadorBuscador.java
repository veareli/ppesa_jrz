package com.daicon.pc.ppesa;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdaptadorBuscador extends RecyclerView.Adapter<AdaptadorBuscador.ViewHolder> implements View.OnClickListener {

    View view;
    private  View.OnClickListener listener;

    public AdaptadorBuscador(ArrayList<Productos> listaProductos) {
        this.listaProductos = listaProductos;
    }

    ArrayList<Productos> listaProductos;

    @Override
    public AdaptadorBuscador.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_buscador,null,false);
        view.setOnClickListener(this);

        return new AdaptadorBuscador.ViewHolder(view);
    }

    public void onBindViewHolder(final AdaptadorBuscador.ViewHolder viewHolder, int i) {
        viewHolder.producto.setText(listaProductos.get(i).getDescripcion());

    }


    public int getItemCount() {
        return listaProductos.size();
    }

    public void setOnClickListener(View.OnClickListener listener){

        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView producto;


        public ViewHolder(View itemView) {
            super(itemView);

            producto = itemView.findViewById(R.id.lblDescripcionProd);

        }
    }



}
