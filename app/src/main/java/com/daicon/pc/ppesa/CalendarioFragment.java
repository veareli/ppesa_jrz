package com.daicon.pc.ppesa;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarioFragment extends BaseVolleyFragment {
    CalendarView calendario;
    Button verProductos;
    ProductosFragment productosFragment;
    int idLinea = 0;
    String fecha, nombre;
    Date fechaActual;
    Boolean seguir = false;
    TextView linea;

    ProductosCalendarioFragment productosCalendarioFragment = new ProductosCalendarioFragment();
    double factorValor=0;
    private OnFragmentInteractionListener mListener;
    public CalendarioFragment() {
        // Required empty public constructor
    }

    public static CalendarioFragment newInstance(String param1, String param2) {
        CalendarioFragment fragment = new CalendarioFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Date date = new Date();
        fechaActual = date;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.calendar_layout, container, false);
        idLinea = getArguments() != null ? getArguments().getInt("IDLinea") : 0;
        nombre = getArguments()!= null? getArguments().getString("nombre"):"";
        productosFragment = new ProductosFragment();
        /*calendario = view.findViewById(R.id.calendario);
        verProductos = view.findViewById(R.id.btnVerProductos);
        linea = view.findViewById(R.id.lblLinea)*/;

        /*linea.setText(nombre);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-1);
        calendario.setMinDate(c.getTimeInMillis());

        Calendar d = Calendar.getInstance();
        d.add(Calendar.DAY_OF_MONTH,45);
        calendario.setMaxDate(d.getTimeInMillis());

        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                fecha = dayOfMonth+"/"+(month+1)+"/"+year;
                makeRequest();
                if(seguir)
                abrirPromocionesOtroDia();

            }
        });
        verProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putInt("IDLinea",  idLinea);
                productosFragment.setArguments(args);
                transaction.replace(R.id.contenedor, productosFragment).commit();
            }
        });*/
        return view;
    }

    private void abrirPromocionesOtroDia() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle argsE = new Bundle();
        argsE.putInt("idLinea",  idLinea);
        argsE.putDouble("factor",factorValor);
        argsE.putString("fecha",fecha);

        productosCalendarioFragment.setArguments(argsE);
        transaction.replace(R.id.contenedor, productosCalendarioFragment).commit();
    }

    private void makeRequest() {

        String url =getResources().getString(R.string.url)+"/obtener_factor.php?fecha="+fecha;
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("factor");
                try {
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(0);
                        factorValor = jsonObject.optDouble("Factor");
                    if(factorValor!=0.0){
                        seguir = true;
                    }else{
                        seguir = false;
                        Toast.makeText(getContext(),"No se encontraron precios para este dia",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    Toast.makeText(getContext(), "Calendario exception: "+ e.getMessage(),Toast.LENGTH_SHORT ).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "onErrorResponse: "+ error.getMessage(),Toast.LENGTH_SHORT ).show();
            }
        });

        addToQueue(request);

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
