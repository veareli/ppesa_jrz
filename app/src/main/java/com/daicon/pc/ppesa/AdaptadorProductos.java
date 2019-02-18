package com.daicon.pc.ppesa;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ViewHolder> {
    View view;
    public AdaptadorProductos(ArrayList<Productos> listaProductos) {
        this.listaProductos = listaProductos;
    }

    ArrayList<Productos> listaProductos;

    @Override
    public AdaptadorProductos.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_producto,null,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdaptadorProductos.ViewHolder viewHolder, final int i) {
        viewHolder.producto.setText(listaProductos.get(i).getDescripcion());
//        viewHolder.disponibilidad.setText(listaProductos.get(i).getDisponibles());
  //      viewHolder.costo.setText(listaProductos.get(i).getCosto());
        viewHolder.id.setText(Integer.toString(listaProductos.get(i).getID()));

        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent(v.getContext(), AgregarProductoActivity.class);
                x.putExtra("producto",viewHolder.producto.getText());
                x.putExtra("disponibles",listaProductos.get(i).getDisponibles());
                x.putExtra("precio",listaProductos.get(i).getCosto());
                x.putExtra("idProducto",Integer.valueOf(viewHolder.id.getText().toString()));
                v.getContext().startActivity(x);
            }
        });

    }


    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView producto, costo, disponibilidad,id, linea;
        ImageButton btnShop;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);

            producto = itemView.findViewById(R.id.lblProducto);
            costo = itemView.findViewById(R.id.lblCosto);
            disponibilidad = itemView.findViewById(R.id.lblDisponibles);
            id = itemView.findViewById(R.id.lblID);
            //linea = itemView.findViewById(R.id.lblLinea);
            btnShop = itemView.findViewById(R.id.btnShop);
            card = itemView.findViewById(R.id.cardProductos);
        }
    }


}

