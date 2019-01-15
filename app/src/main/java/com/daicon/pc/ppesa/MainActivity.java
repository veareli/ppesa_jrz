package com.daicon.pc.ppesa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseVolleyActivity implements
        LineasFragment.OnFragmentInteractionListener,
        EditPerfilFragment.OnFragmentInteractionListener,
        PerfilFragment.OnFragmentInteractionListener,
        ProductosFragment.OnFragmentInteractionListener,
        PedidosFragment.OnFragmentInteractionListener,
        /*Response.Listener<JSONObject>,
        Response.ErrorListener,*/
        NavigationView.OnNavigationItemSelectedListener,
        CalendarioFragment.OnFragmentInteractionListener,
        ProductosCalendarioFragment.OnFragmentInteractionListener,
        DetallesPedidoFragment.OnFragmentInteractionListener,
        BuscadorFragment.OnFragmentInteractionListener,
        CalendarioPersonalizadoFragment.OnFragmentInteractionListener,
        ListaTemporalFragment.OnFragmentInteractionListener
{



    Promocion promocion = null;
    LineasFragment fragmentLineas;
    PerfilFragment fragmentPerfil;
    PedidosFragment pedidosFragment;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    TextView descuento, descripcion, fecha;
    int tipoCliente, ID;
    BuscadorFragment buscadorFragment;
    Boolean disponibles = false;
    RecyclerView recyclerView;
    private ActionBar toolbar;
    private DrawerLayout drawer;
    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            switch (item.getItemId()) {
                case R.id.home:
                    //toolbar.setTitle("Inicio");
                    args.putInt("IdCliente", ID);
                    args.putInt("tipoCliente", tipoCliente);

                    fragmentLineas.setArguments(args);
                    transaction.replace(R.id.contenedor, fragmentLineas).commit();
                    return true;
                case R.id.search:
                    args.putInt("tipoCliente", tipoCliente);
                    args.putInt("IdCliente", ID);
                    buscadorFragment.setArguments(args);
                    transaction.replace(R.id.contenedor, buscadorFragment).commit();
                    return true;
                case R.id.packageB:
                    //toolbar.setTitle("Pedidos");

                    args.putInt("IdCliente", ID);
                    args.putInt("tipoCliente", tipoCliente);
                    pedidosFragment.setArguments(args);
                    transaction.replace(R.id.contenedor, pedidosFragment).commit();


                    return true;
                case R.id.promo:
                    buscarPromocion();

                    return true;

            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volley = VolleyS.getInstance(this);
        fRequestQueue = volley.getRequestQueue();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();


        Intent intent = getIntent();
        ID = intent.getIntExtra("IdCliente", 0);
        tipoCliente = intent.getIntExtra("tipoCliente", 0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentLineas = new LineasFragment();
        fragmentPerfil = new PerfilFragment();
        pedidosFragment = new PedidosFragment();
        buscadorFragment = new BuscadorFragment();

        Bundle args = new Bundle();
        args.putInt("tipoCliente", tipoCliente);
        args.putInt("IdCliente",ID);
        fragmentLineas.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.contenedor, fragmentLineas).commit();

        disponiblesModificados();

        buscarNombreApp();
        buscarPromocion();
        //abrirPromocion(promocion);


    }

    private void buscarNombreApp() {

        String url = getResources().getString(R.string.url)+"/consultar_nombre_app.php";
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("nombre");
                try{
                    for(int i=0; i < json.length(); i++){
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        if(!jsonObject.getString("Nombre").equals("PPESA")){
                            getSupportActionBar().setTitle(jsonObject.getString("Nombre"));
                        }

                    }
                }
                catch(JSONException e){

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        addToQueue(request);

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

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
                Toast.makeText(getApplicationContext(), "Error response: Productos Calendario "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        addToQueue(request);
    }


    private void buscarPromocion() {
        String url = getResources().getString(R.string.url)+"/obtener_promocion.php?id_tipoCliente=" + tipoCliente;
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("promocion");
                try {

                    for (int i = 0; i < json.length(); i++) {
                        promocion = new Promocion();
                        Productos producto = new Productos();

                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);

                        if(!jsonObject.getString("Descripcion").equals("vacio")){
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date date = new Date();

                            String currentDate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());

                            promocion.setFecha(currentDate);
                            promocion.setDescuento(jsonObject.optInt("NuevoPrecio"));
                            promocion.setProducto(jsonObject.optString("Descripcion"));
                            promocion.setTexto(jsonObject.optString("texto"));

                            if(disponibles)
                                producto.setDisponibles(jsonObject.getString("Disponibilidad"));
                            if(!disponibles)
                                producto.setDisponibles(jsonObject.getString("Minimo"));
                            producto.setDescripcion(jsonObject.optString("Descripcion"));
                            producto.setCosto(jsonObject.optString("Precio"));
                            producto.setID(jsonObject.optInt("id_Producto"));
                            producto.setLinea(jsonObject.optInt("_idLinea"));
                            promocion.setProductoObj(producto);

                            abrirPromocion(promocion);
                        }else{
                            Toast.makeText(getApplicationContext(),"No hay promociones para ti el dia de hoy :)", Toast.LENGTH_SHORT).show();

                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext() , "No se pudo conectar " + e.getMessage(), Toast.LENGTH_LONG).show();
                    //e.printStackTrace();
                }



            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No se pudo consultar. Main Activity", Toast.LENGTH_SHORT).show();

                    }
                });
        //request = Volley.newRequestQueue(this);

        //cargarWebService();

        addToQueue(request);

    }

    private void abrirPromocion(Promocion p) {
        if (p != null) {
            LayoutInflater inflater = getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_promocion, null);

            descripcion = dialoglayout.findViewById(R.id.lblProducto);
            descuento = dialoglayout.findViewById(R.id.lblDescuento);
            fecha = dialoglayout.findViewById(R.id.lblCaducidad);

            descripcion.setText(promocion.getProducto());
            descuento.setText(promocion.getTexto());
            fecha.setText(promocion.getFecha());


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialoglayout).
                    setPositiveButton("Obtener ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent x = new Intent(getApplicationContext(), AgregarProductoActivity.class);

                            Bundle b = new Bundle();
                            b.putSerializable("producto", (Serializable) promocion.getProductoObj());
                            b.putSerializable("promo", (Serializable) promocion);
                            x.putExtra("BundleProd",b);
                            x.putExtra("IdCliente", ID);
                            x.putExtra("pantalla", "promo");
                            x.putExtra("columna", disponibles);


                            x.putExtra("producto", promocion.getProducto());
                            x.putExtra("disp", promocion.getProductoObj().getDisponibles());
                            x.putExtra("precio", promocion.getProductoObj().getCosto());
                            x.putExtra("idProducto", promocion.getProductoObj().getID());
                            //x.putExtra("linea",promocion.getProductoObj().getLinea());
                            x.putExtra("descuento", promocion.getDescuento());
                            x.putExtra("tipoCliente", tipoCliente);
                            x.putExtra("pantalla", "promo");
                            startActivity(x);


                        }
                    }).setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.show();
        }

    }

    public android.support.v7.app.AlertDialog createCustomDialog() {
        final android.support.v7.app.AlertDialog alertDialog;
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_agregar_pedido, null);

        Button btnAgregar = (Button) v.findViewById(R.id.btnAgregar);
        TextView desc = (TextView) v.findViewById(R.id.lblDescripcion);
        TextView disp = (TextView) v.findViewById(R.id.lblDisponibilidad);
        TextView prec = (TextView) v.findViewById(R.id.lblPrecio);


        builder.setView(v);
        alertDialog = builder.create();
        // Add action buttons
        desc.setText(promocion.getProducto());
        disp.setText(promocion.getProductoObj().getDisponibles().toString());
        prec.setText(promocion.getProductoObj().getCosto().toString());
        btnAgregar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent x = new Intent(v.getContext(), AgregarProductoActivity.class);
                        x.putExtra("producto", promocion.getProducto());
                        x.putExtra("disp", promocion.getProductoObj().getDisponibles());
                        x.putExtra("precio", promocion.getProductoObj().getCosto());
                        x.putExtra("id", promocion.getProductoObj().getID());
                        x.putExtra("linea", promocion.getProductoObj().getLinea());
                        x.putExtra("descuento", promocion.getDescuento());
                        x.putExtra("IdCliente", ID);
                        x.putExtra("tipoCliente",tipoCliente);
                        v.getContext().startActivity(x);
                        // Aceptar
                        //alertDialog.dismiss();
                    }
                }
        );

        return alertDialog;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Bundle args = new Bundle();
        switch (menuItem.getItemId()) {
            case R.id.nav_perfil:
                args.putInt("IdCliente", ID);
                args.putInt("tipoCliente", tipoCliente);
                fragmentPerfil.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, fragmentPerfil).commit();
                break;
            case R.id.nav_sesion:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("¿Seguro que quieres cerrar sesion?");
                builder.
                        setPositiveButton("Cerrar sesion", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences settings = getSharedPreferences("login_shared", Context.MODE_PRIVATE);
                                settings.edit().clear().commit();
                                abrirLogin();

                            }
                        }).setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.show();

                //
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void abrirLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
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