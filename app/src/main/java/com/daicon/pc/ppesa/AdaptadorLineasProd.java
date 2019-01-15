package com.daicon.pc.ppesa;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorLineasProd extends RecyclerView.Adapter<AdaptadorLineasProd.ViewHolder> implements View.OnClickListener {
    public AdaptadorLineasProd(ArrayList<LineasProductos> listaLineasProd) {
        this.listaLineasProd = listaLineasProd;
    }

    ArrayList<LineasProductos> listaLineasProd;
    private  View.OnClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_lineas,null,false);

        view.setOnClickListener(this);
        return new AdaptadorLineasProd.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.descripcion.setText(listaLineasProd.get(i).getDescripcion());

    }


    @Override
    public int getItemCount() {
        return listaLineasProd.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView descripcion;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            descripcion = itemView.findViewById(R.id.lblDescripcion);

        }
    }
}
