package com.daicon.pc.ppesa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;


public class LineasFragment extends BaseVolleyFragment implements
        ProductosFragment.OnFragmentInteractionListener,
        ProductosCalendarioFragment.OnFragmentInteractionListener{

    ArrayList<LineasProductos> listaLineas;
    RecyclerView recyclerViewLineas;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ProductosFragment productosFragment;
    ProductosCalendarioFragment productosCalendarioFragment;
    CalendarioFragment calendarioFragment;
    int idCliente,tipoCliente;

    private OnFragmentInteractionListener mListener;

    public LineasFragment() {
        // Required empty public constructor
    }

    public static LineasFragment newInstance(/*String param1, String param2*/) {
        LineasFragment fragment = new LineasFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //calendarioFragment = new CalendarioFragment();
        productosCalendarioFragment = new ProductosCalendarioFragment();
        idCliente = getArguments()!=null? getArguments().getInt("IdCliente"):0;
        tipoCliente = getArguments()!=null? getArguments().getInt("tipoCliente"):0;


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        listaLineas = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_lineas, container, false);
        recyclerViewLineas = (RecyclerView) view.findViewById(R.id.rvLineas);

        productosFragment = new ProductosFragment();
        calendarioFragment = new CalendarioFragment();

        GridLayoutManager llm = new GridLayoutManager(getContext(), 2);
        recyclerViewLineas.setLayoutManager(llm);

        makeRequest();
        return view;
    }


    private void makeRequest(){
        String url = getResources().getString(R.string.url)+"/ConsultaListaLineas.php";
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LineasProductos lineasProductos = null;

                JSONArray json = response.optJSONArray("linea");
                try {
                for(int i =0; i<json.length(); i++){
                    lineasProductos = new LineasProductos();
                    JSONObject jsonObject = null;


                        jsonObject = json.getJSONObject(i);


                    lineasProductos.setId(jsonObject.optInt("id_Linea"));
                    lineasProductos.setDescripcion(jsonObject.optString("Descripcion"));


                    listaLineas.add(lineasProductos);

                    AdaptadorLineasProd adapter = new AdaptadorLineasProd(listaLineas);
                    recyclerViewLineas.setAdapter(adapter);
                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Bundle args = new Bundle();
                            args.putInt("IdCliente",idCliente);
                            args.putInt("tipoCliente",tipoCliente);

                            args.putInt("IDLinea",  listaLineas.get(recyclerViewLineas.getChildAdapterPosition(v)).getId());
                            args.putString("linea",  listaLineas.get(recyclerViewLineas.getChildAdapterPosition(v)).getDescripcion());

                            productosCalendarioFragment.setArguments(args);

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.contenedor, productosCalendarioFragment).commit();


                        }
                    });

                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se pudo consultar "+error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
        addToQueue(request);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }


}
