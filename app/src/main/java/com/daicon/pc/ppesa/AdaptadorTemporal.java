package com.daicon.pc.ppesa;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EventObject;

public class AdaptadorTemporal extends RecyclerView.Adapter<AdaptadorTemporal.ViewHolder> implements View.OnClickListener {
    View view;
    private View.OnClickListener listener;

    public AdaptadorTemporal(ArrayList<EventObjects> listaTemporal) {
        this.listaTemporal = listaTemporal;
    }

    ArrayList<EventObjects> listaTemporal;

    @Override
    public AdaptadorTemporal.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_temporal, null, false);
        view.setOnClickListener(this);

        return new AdaptadorTemporal.ViewHolder(view);
    }

    public void onBindViewHolder(final AdaptadorTemporal.ViewHolder viewHolder, int i) {
        viewHolder.precio.setText(String.valueOf(listaTemporal.get(i).getCosto()));
        viewHolder.factor.setText(String.valueOf(listaTemporal.get(i).getFactorValor()));
        viewHolder.fecha.setText(listaTemporal.get(i).getDate());
        viewHolder.disp.setText(String.valueOf(listaTemporal.get(i).getDisponibilidad()));

    }


    public int getItemCount() {
        return listaTemporal.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {

        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fecha, factor, precio, disp;


        public ViewHolder(View itemView) {
            super(itemView);

            fecha = itemView.findViewById(R.id.lblFechaTemp);
            factor = itemView.findViewById(R.id.lblFactorTemp);
            precio = itemView.findViewById(R.id.lblPrecioTemp);
            disp = itemView.findViewById(R.id.lblDisponibles);

        }
    }
}