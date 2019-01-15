package com.daicon.pc.ppesa;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorProductosCalendario extends RecyclerView.Adapter<AdaptadorProductosCalendario.ViewHolder> implements View.OnClickListener{

    ArrayList<Productos> listaProductosCal;
    private  View.OnClickListener listener;

    public AdaptadorProductosCalendario(ArrayList<Productos> listaProductosCal) {
        this.listaProductosCal = listaProductosCal;
    }
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_productos_calendario,null,false);
        view.setOnClickListener(this);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptadorProductosCalendario.ViewHolder viewHolder, int i) {
        viewHolder.disponibles.setText(listaProductosCal.get(i).getDisponibles());
       // viewHolder.precio.setText(listaProductosCal.get(i).getCosto());
        viewHolder.producto.setText(listaProductosCal.get(i).getDescripcion());
        viewHolder.id.setText(String.valueOf(listaProductosCal.get(i).getID()));
       /* viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                *//*Intent x = new Intent(v.getContext(), AgregarProductoActivity.class);
                x.putExtra("producto",viewHolder.producto.getText());
                x.putExtra("disp",viewHolder.disponibles.getText());
                x.putExtra("precio",viewHolder.precio.getText());
                x.putExtra("idProducto",Integer.valueOf(viewHolder.id.getText().toString()));
                //  x.putExtra("linea",viewHolder.linea.getText());
                v.getContext().startActivity(x);*//*
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return listaProductosCal.size();
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView producto, precio, disponibles,id;
        Button obtener;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);

            producto = itemView.findViewById(R.id.lblProductoCalendario);
            id = itemView.findViewById(R.id.lblIdCal);
            //precio = itemView.findViewById(R.id.lblPrecioCalendario);
            disponibles = itemView.findViewById(R.id.lblDisponiblesCalendario);
            cv = itemView.findViewById(R.id.cardProdCalendario);
        }
    }
}
