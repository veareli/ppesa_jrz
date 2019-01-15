package com.daicon.pc.ppesa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarioPersonalizadoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarioPersonalizadoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarioPersonalizadoFragment extends BaseVolleyFragment {


    CalendarCustomView mView;
    private static final String TAG = CalendarCustomView.class.getSimpleName();
    private Button previousButton, nextButton;
    private TextView currentDate, productoLabel;
    private GridView calendarGridView;
    private Button addEventButton;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private double precio = 0;
    LineasFragment fragmentLineas;
    ProgressDialog progressDialog;
    Productos producto;
    int idCliente;



    private GridAdapter mAdapter;
    private ArrayList<EventObjects> listaEventos;
    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    private OnFragmentInteractionListener mListener;
    View view;

    public CalendarioPersonalizadoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarioPersonalizadoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarioPersonalizadoFragment newInstance(String param1, String param2) {
        CalendarioPersonalizadoFragment fragment = new CalendarioPersonalizadoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idCliente = getArguments()!=null? getArguments().getInt("IdCliente"):0;
        precio =  getArguments().getDouble("precio");

        producto = (Productos)getArguments().getSerializable("producto");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getContext());

        makeRequest();
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.calendar_layout, container, false);//inflater.inflate(R.layout.fragment_calendario_personalizado, this);
        previousButton = view.findViewById(R.id.previous_month);
        nextButton = view.findViewById(R.id.next_month);
        productoLabel = view.findViewById(R.id.lblProductoNombre);
        currentDate = view.findViewById(R.id.display_current_date);
        /*addEventButton = view.findViewById(R.id.add_calendar_event);*/
        calendarGridView = view.findViewById(R.id.calendar_grid);
        //mView = view.findViewById(R.id.custom_calendar);
        productoLabel.setText(producto.getDescripcion());



        setPreviousButtonClickEvent();
        setNextButtonClickEvent();


        //progressDialog.hide();

        return view;
    }

    private void makeRequest() {
        progressDialog.show();
        String url =getResources().getString(R.string.url)+"/obtener_lista_factores.php";
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


                            double factor = jsonObject.optDouble("Factor");
                            //Date date=new SimpleDateFormat("dd/MM/yyyy").parse(jsonObject.optString("dia"));
                            evento.setDate(jsonObject.optString("dia"));
                            evento.setFactorValor(factor);
                            double costo = (precio*factor);
                            DecimalFormat df = new DecimalFormat("#.00");

                            evento.setCosto(Double.valueOf(df.format(costo)));

                            listaEventos.add(evento);

                            //progressDialog.hide();

                        }else{
                            progressDialog.hide();
                            Toast.makeText(getContext(),"No hay productos en esta linea/categoria",Toast.LENGTH_SHORT).show();
                            fragmentLineas = new LineasFragment();
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.contenedor, fragmentLineas).commit();
                        }


                    }
                    if(!listaEventos.isEmpty()){
                        setUpCalendarAdapter();
                        progressDialog.hide();
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


    private void setPreviousButtonClickEvent(){
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
            }
        });
    }
    private void setNextButtonClickEvent(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
            }
        });
    }
    private void setGridCellClickEvents(){
        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView celda, fecha, factor;
                celda = view.findViewById(R.id.event_id);
                fecha = view.findViewById(R.id.event_fecha);
                factor = view.findViewById(R.id.lblfactor);
                Intent agregar = new Intent(getContext(), AgregarProductoActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("producto", (Serializable) producto);
                agregar.putExtra("fechaSeleccionada",fecha.getText().toString());
                agregar.putExtra("factorSeleccionado",factor.getText().toString());
                agregar.putExtra("costoFinal",Double.valueOf(celda.getText().toString().replace("$","")));
                agregar.putExtra("BundleProd",b);
                agregar.putExtra("pantalla", "calendario");
                agregar.putExtra("IdCliente",idCliente);

                startActivity(agregar);
                //Toast.makeText(getContext(), "Clicked " + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpCalendarAdapter(){
        List<Date> dayValueInCells = new ArrayList<Date>();
        //mQuery = new DatabaseQuery(context);
        //listaEventos = ;

        Calendar mCal = (Calendar)cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while(dayValueInCells.size() < MAX_CALENDAR_COLUMN){
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        String sDate = formatter.format(cal.getTime());
        currentDate.setText(sDate);
        mAdapter = new GridAdapter(getContext(), dayValueInCells, cal, listaEventos);
        calendarGridView.setAdapter(mAdapter);


        //progressDialog.hide();
        setGridCellClickEvents();
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
