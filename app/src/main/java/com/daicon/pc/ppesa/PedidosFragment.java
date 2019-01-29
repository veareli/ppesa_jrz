package com.daicon.pc.ppesa;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;

public class PedidosFragment extends BaseVolleyFragment /*implements Response.Listener<JSONObject>,Response.ErrorListener */{

    private OnFragmentInteractionListener mListener;
    private TextView referencia, fecha, estatus;
    ArrayList<Pedidos> listaPedidos;
    RecyclerView recyclerViewPedidos;
    int idCliente = 0;
    ProgressDialog progressDialog;
    LineasFragment lineasFragment;

    public PedidosFragment() {
        // Required empty public constructor
    }

    public static PedidosFragment newInstance(String param1, String param2) {
        PedidosFragment fragment = new PedidosFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        lineasFragment = new LineasFragment();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);

        progressDialog.setMessage("Cargando");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Bundle bundle = getArguments();
        idCliente = bundle.getInt("IdCliente");

        estatus = (TextView)view.findViewById(R.id.lblEstatus);
        fecha = (TextView)view.findViewById(R.id.lblFecha);
        referencia = (TextView)view.findViewById(R.id.lblReferencia);

        listaPedidos = new ArrayList<>();
        recyclerViewPedidos = (RecyclerView) view.findViewById(R.id.rvPedidos);

        GridLayoutManager llm = new GridLayoutManager(getContext(),1 );
        recyclerViewPedidos.setLayoutManager(llm);

        makeRequest();

        return view;
    }

    private void makeRequest() {

        progressDialog.show();
        String url =getResources().getString(R.string.url)+"/consulta_pedidos.php?Pedidos="+ idCliente;

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Pedidos pedidos = null;

                JSONArray json = response.optJSONArray("Pedidos");
                try {

                    for(int i=0; i < json.length(); i++){
                        pedidos = new Pedidos();
                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);

                        if(!jsonObject.getString("Descripcion").equals("vacio")){
                            pedidos.setEstatus(jsonObject.optString("Descripcion"));
                            pedidos.setFecha(jsonObject.optString("FechaProgramada"));
                            pedidos.setReferencia(jsonObject.getString("id_Pedido"));

                            listaPedidos.add(pedidos);

                            AdaptadorPedidos adapter = new AdaptadorPedidos(listaPedidos);
                            recyclerViewPedidos.setAdapter(adapter);
                            progressDialog.hide();

                        }else{
                            manejarError("No hay pedidos para este usuario");
                        }

                    }
                }catch (JSONException e) {
                    manejarError(e.getMessage());

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                manejarError(error.getMessage());

            }
        });

        addToQueue(request);
    }

    public void manejarError(String mensaje){
        progressDialog.hide();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor, lineasFragment).commit();
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();


    }
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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
