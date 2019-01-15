package com.daicon.pc.ppesa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.security.AccessController.getContext;

public class ProductosFragment extends BaseVolleyFragment{

    ArrayList<Productos> listaProductos;
    RecyclerView recyclerViewProductos;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    int idLinea = 0;
    ImageView btnAgregar;
    private int idproducto,  cantidad;
    private double  factorValor;
    String precio, descripcion;
    ProgressDialog progressDialog;
    LineasFragment fragmentLineas;



    TextView lblIdProd, lblPrecio, lblDescripcion;
    EditText txtCantidad;


    private OnFragmentInteractionListener mListener;

    public ProductosFragment() {
        // Required empty public constructor
    }

    public static ProductosFragment newInstance() {
        ProductosFragment fragment = new ProductosFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        makeRequestFactor();


    }

    private void makeRequestFactor() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        String url =getResources().getString(R.string.url)+"/obtener_factor.php?fecha="+dateFormat.format(date);
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("factor");
                try {
                    for(int i=0; i <json.length(); i++){

                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);
                        factorValor = jsonObject.optDouble("Factor");

                    }

                }catch (JSONException e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        addToQueue(request);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_productos, container, false);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Cargando");


        idLinea = getArguments() != null ? getArguments().getInt("IDLinea") : 0;

        listaProductos = new ArrayList<>();
        recyclerViewProductos = view.findViewById(R.id.rvProductos);


        GridLayoutManager llm = new GridLayoutManager(getContext(),1 );
        recyclerViewProductos.setLayoutManager(llm);

        makeRequest();

        return view;
    }


    private void makeRequest() {

        progressDialog.show();
        String url =getResources().getString(R.string.url)+"/ConsultaListaProductos.php?producto="+idLinea;
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Productos producto = null;

                    JSONArray json = response.optJSONArray("producto");
                    try {

                        for(int i=0; i < json.length(); i++){
                            producto = new Productos();
                            JSONObject jsonObject = null;

                            jsonObject = json.getJSONObject(i);
                            if(!jsonObject.getString("Descripcion").equals("vacio")){
                                producto.setDescripcion(jsonObject.optString("Descripcion"));
                                producto.setCosto(String.valueOf(Utilerias.limitarDecimales(jsonObject.optDouble("Precio")*factorValor)));
                                producto.setDisponibles(jsonObject.getString("Disponibilidad"));
                                producto.setID(jsonObject.getInt("id_Producto"));
                                producto.setLinea(jsonObject.getInt("_idLinea"));


                                listaProductos.add(producto);

                                AdaptadorProductos adapter = new AdaptadorProductos(listaProductos);
                                recyclerViewProductos.setAdapter(adapter);

                                progressDialog.hide();

                            }else{
                                progressDialog.hide();
                                Toast.makeText(getContext(),"No hay productos en esta linea/categoria",Toast.LENGTH_SHORT).show();
                                fragmentLineas = new LineasFragment();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.contenedor, fragmentLineas).commit();
                            }


                        }
                    }catch (JSONException e) {
                        progressDialog.hide();
                        Toast.makeText(getContext(), "No se pudo conectar "+response, Toast.LENGTH_LONG).show();
                        fragmentLineas = new LineasFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.contenedor, fragmentLineas).commit();

                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getContext(), "No se pudo conectar "+error.getMessage(), Toast.LENGTH_LONG).show();
                fragmentLineas = new LineasFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contenedor, fragmentLineas).commit();
            }
        });
        addToQueue(request);

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
