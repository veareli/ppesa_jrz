package com.daicon.pc.ppesa;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ProductosCalendarioFragment extends BaseVolleyFragment implements Serializable {

    ArrayList<Productos> listaProductosCal;
    RecyclerView recyclerViewProdCal;
    Productos finalProducto;
    double factor = 0;
    int idLinea = 0;
    int idCliente,tipoCliente;
    String fecha="", linea;
    ProgressDialog progressDialog;
    private OnFragmentInteractionListener mListener;
    CalendarioPersonalizadoFragment calendarioPersonalizadoFragment;
    TextView titulo;
    LineasFragment fragmentLineas = new LineasFragment();
    ListaTemporalFragment listaTemporalFragment;
    boolean isFactorNeed = false, disponibles=false;

    AdaptadorProductosCalendario adapter;
    public ProductosCalendarioFragment() {
    }

    public static ProductosCalendarioFragment newInstance(String param1, String param2) {
        ProductosCalendarioFragment fragment = new ProductosCalendarioFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        idCliente = getArguments()!=null? getArguments().getInt("IdCliente"):0;
        tipoCliente = getArguments().getInt("tipoCliente");
        idLinea= getArguments()!=null? getArguments().getInt("IDLinea"):0;

        isFactorNeed = (idLinea==1|| idLinea==2||idLinea==4)?true:false;

        listaTemporalFragment = new ListaTemporalFragment();

        disponiblesModificados();

    }

    private void disponiblesModificados() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        String dateString = format.format(new Date());
        String url =getResources().getString(R.string.url)+"/disponiblesModificados.php?fecha="+dateString;
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                JSONArray json = response.optJSONArray("Disponibles");
                try {


                    for(int i=0; i < json.length(); i++){
                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);
                        if(!jsonObject.optString("Fecha").equals("vacio")){
                            disponibles = true;


                        }else {
                           disponibles = false;
                        }
                    }
                }catch (JSONException e) {
                    disponibles = false;
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error response: Productos Calendario "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        addToQueue(request);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_productos_calendario, container, false);


        progressDialog.setMessage("Cargando");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Bundle b = getArguments();
        idLinea = b.getInt("IDLinea");
        linea = b.getString("linea");
        factor = b.getDouble("factor");
        fecha = b.getString("fecha");

        titulo = view.findViewById(R.id.lblTitulo);
        titulo.setText(linea);



        listaProductosCal = new ArrayList<>();
        recyclerViewProdCal = (RecyclerView) view.findViewById(R.id.rvProductosCalendario);

        GridLayoutManager llm = new GridLayoutManager(getContext(),1);
        recyclerViewProdCal.setLayoutManager(llm);

        makeRequest();

        return view;
    }

    private void makeRequest() {
        progressDialog.show();
        /*String url;
        if(disponibles){

        }else{*/
           String url =getResources().getString(R.string.url)+"/ConsultaListaProductos.php?producto="+idLinea;
        //}

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
                            if(!jsonObject.optString("Descripcion").equals("vacio")){
                                final double costo = jsonObject.optDouble("Precio");
                                double costoFinal = Utilerias.limitarDecimales(costo);

                                producto.setDescripcion(jsonObject.optString("Descripcion"));
                                producto.setCosto(String.valueOf(costoFinal));
                                if(disponibles)
                                    producto.setDisponibles(jsonObject.getString("Disponibilidad"));
                                if(!disponibles)
                                    producto.setDisponibles(jsonObject.getString("Minimo"));
                                producto.setID(jsonObject.getInt("id_Producto"));
                                producto.setLinea(jsonObject.getInt("_idLinea"));

                                listaProductosCal.add(producto);

                                adapter= new AdaptadorProductosCalendario(listaProductosCal);
                                recyclerViewProdCal.setAdapter(adapter);

                                finalProducto = producto;
                                adapter.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Bundle args = new Bundle();
                                        args.putDouble("precio",costo);
                                        args.putString("linea",linea);
                                        args.putBoolean("isFactorNeed",isFactorNeed);
                                        args.putInt("IdCliente",idCliente);
                                        args.putInt("tipoCliente",tipoCliente);
                                        args.putBoolean("columna", disponibles);

                                        args.putSerializable("producto", (Serializable) listaProductosCal.get(recyclerViewProdCal.getChildAdapterPosition(v)));

                                        listaTemporalFragment.setArguments(args);

                                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.contenedor, listaTemporalFragment).commit();

                                    }
                                });

                            }else {
                                manejarError("No hay productos para esta linea/categoria");
                            }
                            progressDialog.hide();
                        }
                    }catch (JSONException e) {
                        manejarError("No se pudo conectar  "+e.getMessage());
                        }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error response: Productos Calendario "+error.getMessage(), Toast.LENGTH_LONG).show();
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

    public void manejarError(String msj){
        progressDialog.hide();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor, fragmentLineas).commit();

        Toast.makeText(getContext(), msj, Toast.LENGTH_LONG).show();
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
