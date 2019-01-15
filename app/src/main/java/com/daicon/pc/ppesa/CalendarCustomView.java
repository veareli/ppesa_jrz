package com.daicon.pc.ppesa;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class CalendarCustomView extends LinearLayout{
    private static final String TAG = CalendarCustomView.class.getSimpleName();
    private ImageView previousButton, nextButton;
    private TextView currentDate;
    private GridView calendarGridView;
    private Button addEventButton;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private GridAdapter mAdapter;
    private ArrayList<EventObjects> listaEventos = null;
    private VolleyS volley;
    protected RequestQueue fRequestQueue;


    public CalendarCustomView(Context context) {
        super(context);
    }
    public CalendarCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        makeRequest();
        initializeUILayout();
        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();
        setGridCellClickEvents();

    }

    private void makeRequest() {
        //progressDialog.show();
        String url =getResources().getString(R.string.url)+"/obtener_lista_factores.php";
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EventObjects evento = null;

                JSONArray json = response.optJSONArray("factores");
                try {

                    for(int i=0; i < json.length(); i++){
                        evento = new EventObjects();
                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);
                        if(!jsonObject.getString("dia").equals("vacio")){
                            evento.setId(jsonObject.optInt("id_Factor"));


                            //Date date=new SimpleDateFormat("dd/MM/yyyy").parse();
                            evento.setDate(jsonObject.optString("dia"));
                            evento.setFactorValor(jsonObject.optDouble("Factor"));

                            listaEventos.add(evento);

                            /*AdaptadorProductos adapter = new AdaptadorProductos(listaEventos);
                            recyclerViewProductos.setAdapter(adapter);*/

                            //progressDialog.hide();

                        }else{
                            //progressDialog.hide();
                            Toast.makeText(getContext(),"No hay productos en esta linea/categoria",Toast.LENGTH_SHORT).show();
                            /*fragmentLineas = new LineasFragment();
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.contenedor, fragmentLineas).commit();*/
                        }


                    }
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

    public CalendarCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initializeUILayout(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_calendario_personalizado, this);
        /*previousButton = (ImageView)view.findViewById(R.id.previous_month);
        nextButton = (ImageView)view.findViewById(R.id.next_month)*/;
        currentDate = (TextView)view.findViewById(R.id.display_current_date);
        calendarGridView = (GridView)view.findViewById(R.id.calendar_grid);
    }
    private void setPreviousButtonClickEvent(){
        previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
            }
        });
    }
    private void setNextButtonClickEvent(){
        nextButton.setOnClickListener(new OnClickListener() {
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
                Toast.makeText(context, "Clicked " + position, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setUpCalendarAdapter(){
        List<Date> dayValueInCells = new ArrayList<Date>();
        //mQuery = new DatabaseQuery(context);
        //List<com.daicon.pc.ppesa.EventObjects> listaEventos = mQuery.getAllFutureEvents();

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
        mAdapter = new GridAdapter(context, dayValueInCells, cal, listaEventos);
        calendarGridView.setAdapter(mAdapter);
    }



    public void addToQueue(Request request) {
        if (request != null) {
            request.setTag(this);
            if (fRequestQueue == null)
                fRequestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            //onPreStartConnection();
            fRequestQueue.add(request);
        }
    }
}