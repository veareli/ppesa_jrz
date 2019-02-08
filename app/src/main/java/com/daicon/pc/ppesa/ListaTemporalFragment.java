package com.daicon.pc.ppesa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListaTemporalFragment extends BaseVolleyFragment {
    ArrayList<EventObjects> listaEventos;
    ArrayList<String> listaFechas;
    ArrayList<String> listaFechasConFormato;
    RecyclerView recyclerViewTemporal;
    int idCliente,tipoCliente;
    double precio;
    Productos producto;
    double costoTemp;
    String linea;
    TextView fecha,factor,precioTemp,categoria;
    Boolean isFactorNeed,isDisponiblesValid = false;


    private OnFragmentInteractionListener mListener;

    public ListaTemporalFragment() {
        // Required empty public constructor
    }


    public static ListaTemporalFragment newInstance(String param1, String param2) {
        ListaTemporalFragment fragment = new ListaTemporalFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idCliente = getArguments()!=null? getArguments().getInt("IdCliente"):0;
        tipoCliente= getArguments().getInt("tipoCliente",0);
        precio =  getArguments().getDouble("precio");
        isFactorNeed = getArguments().getBoolean("isFactorNeed");
        producto = (Productos)getArguments().getSerializable("producto");
        linea =getArguments().getString("linea");
        isDisponiblesValid = getArguments().getBoolean("columna",false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_temporal, container, false);
        categoria = view.findViewById(R.id.lblTituloCategoria);
        //precioTemp = view.findViewById(R.id.lblPrecioTemp);
        categoria.setText("Precios para: "+producto.getDescripcion());
        listaEventos = new ArrayList<>();
        recyclerViewTemporal = view.findViewById(R.id.rvProductosTemporal);


        if(!isFactorNeed){
            categoria.setText("Dias de entrega:");

        }


        GridLayoutManager llm = new GridLayoutManager(getContext(),3 );
        recyclerViewTemporal.setLayoutManager(llm);

        llenarDias();
        makeRequest();

        return view;
    }

    private void llenarDias(){
        listaFechas = new ArrayList<>();
        listaFechasConFormato = new ArrayList<>();
        int diasExtra = 2;


        for(int i= 0;i<45;i++){
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, diasExtra+i);
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.format(calendar.getTime());
            dateFormat2.format(calendar.getTime());
            listaFechasConFormato.add(dateFormat2.format(calendar.getTime()));
            listaFechas.add(dateFormat.format(calendar.getTime()));


        }
    }

    private void makeRequest() {
        String url =getResources().getString(R.string.url)+"/obtener_lista_factores.php?";
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EventObjects evento = null;

                listaEventos = new ArrayList<>();

                JSONArray json = response.optJSONArray("factores");
                try {

                    for(int i=0; i < json.length(); i++){
                        evento = new EventObjects();
                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);
                        if(!jsonObject.getString("dia").equals("vacio")){
                            evento.setId(jsonObject.optInt("id_Factor"));


                            double factorInterno = jsonObject.optDouble("Factor");
                            //Date date=new SimpleDateFormat("dd/MM/yyyy").parse(jsonObject.optString("dia"));
                            evento.setDate(listaFechas.get(i).toString());
                            evento.setDateConFormato(listaFechasConFormato.get(i).toString());
                            evento.setDisponibilidad(jsonObject.optInt("disponible"));

                            if(isFactorNeed){
                                evento.setFactorValor(factorInterno);
                                costoTemp = Utilerias.limitarDecimales(Double.valueOf(producto.getCosto())*factorInterno);
                            }else{
                                costoTemp = Double.valueOf(producto.getCosto());
                                evento.setFactorValor(0);
                            }


                            evento.setCosto(Double.valueOf(costoTemp));

                            listaEventos.add(evento);

                            AdaptadorTemporal adapter = new AdaptadorTemporal(listaEventos);
                            recyclerViewTemporal.setAdapter(adapter);
                            adapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    Intent agregar = new Intent(getContext(), AgregarProductoActivity.class);
                                    Bundle b = new Bundle();
                                    b.putSerializable("producto", (Serializable) producto);
                                    agregar.putExtra("fechaSeleccionada",listaEventos.get(recyclerViewTemporal.getChildAdapterPosition(v)).getDateConFormato());
                                    agregar.putExtra("factorSeleccionado",listaEventos.get(recyclerViewTemporal.getChildAdapterPosition(v)).getFactorValor());
                                    agregar.putExtra("costoFinal",listaEventos.get(recyclerViewTemporal.getChildAdapterPosition(v)).getCosto());//Lleva factor
                                    agregar.putExtra("BundleProd",b);
                                    agregar.putExtra("pantalla", "calendario");
                                    agregar.putExtra("IdCliente",idCliente);
                                    agregar.putExtra("tipoCliente",tipoCliente);
                                    agregar.putExtra("columna", isDisponiblesValid);


                                    startActivity(agregar);


                                }
                            });

                            //progressDialog.hide();

                        }else{
                            //progressDialog.hide();
                            Toast.makeText(getContext(),"No hay productos en esta linea/categoria",Toast.LENGTH_SHORT).show();
                            /*//fragmentLineas = new LineasFragment();
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.contenedor, fragmentLineas).commit();*/
                        }


                    }
                    /*if(!listaEventos.isEmpty()){
                        setUpCalendarAdapter();
                        progressDialog.hide();
                    }*/
                }catch (JSONException e) {
                    //progressDialog.hide();
                    Toast.makeText(getContext(), "No se pudo conectar "+response, Toast.LENGTH_LONG).show();
                    /*fragmentLineas = new LineasFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contenedor, fragmentLineas).commit();*/

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.hide();
                Toast.makeText(getContext(), "No se pudo conectar "+error.getMessage(), Toast.LENGTH_LONG).show();
                /*fragmentLineas = new LineasFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contenedor, fragmentLineas).commit();*/
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
