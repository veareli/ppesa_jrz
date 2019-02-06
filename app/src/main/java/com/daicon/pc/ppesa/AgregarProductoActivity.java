package com.daicon.pc.ppesa;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AgregarProductoActivity extends BaseVolleyActivity implements LineasFragment.OnFragmentInteractionListener {

    TextView producto, costo, disponibles, disclaimer, terminar;
    EditText cantidad, fechaEntrega;
    Button agregar,cancelar;
    Boolean isPromo =false;
    double factorValor = 0, descuento,Costo ;
    int Linea, cant, tipoCliente;
    int idProducto = 0, cantidadParaCalculo, idCliente;
    double totalCalculado, costoCalculado;
    Productos productoExtra;
    double costofinal = 0;
    boolean insertExitoso = false;
    MyDbHelper dbHelper;
    String fechaExtra, currentDate, selectedDate;
    int responseNumber = 0;
    boolean isDisponiblesValid;


    String Producto,Disp,ID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        Bundle b =  getIntent().getBundleExtra("BundleProd");
        idCliente = getIntent().getIntExtra("IdCliente",0);
        tipoCliente= getIntent().getIntExtra("tipoCliente",0);
        final Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentDate = dateFormat.format(date);
        isDisponiblesValid = getIntent().getBooleanExtra("columna",false);


        String pantalla = getIntent().getStringExtra("pantalla");
        if(pantalla.equals("buscador")){

            productoExtra = (Productos)b.getSerializable("producto");
            Producto =productoExtra.getDescripcion();
            Disp = productoExtra.getDisponibles();
            Costo = Double.valueOf(productoExtra.getCosto());
            idProducto = productoExtra.getID();
            selectedDate = currentDate;
        }
        if(pantalla.equals("calendario")){

            productoExtra = (Productos)b.getSerializable("producto");
            Producto =productoExtra.getDescripcion();
            Disp = productoExtra.getDisponibles();
            Costo = getIntent().getDoubleExtra("costoFinal",0);//Desde calendario si debe considerarse el factor<-Revisar si ya considera el factor
            idProducto = productoExtra.getID();
            factorValor = getIntent().getDoubleExtra("factorSeleccionado",0);
            Costo =Utilerias.limitarDecimales(Costo);
            selectedDate = getIntent().getStringExtra("fechaSeleccionada");
            descuento = 0;
        }
        if(pantalla.equals("promo")){

            productoExtra = (Productos)b.getSerializable("producto");
            Promocion promocion = (Promocion) b.getSerializable("promo");
            Producto = productoExtra.getDescripcion();
            Disp = productoExtra.getDisponibles();
            Costo = Double.valueOf(promocion.getDescuento());
            idProducto =productoExtra.getID();
            //Linea = Integer.valueOf(b.getString("linea"));
            descuento = 0;
            isPromo = true;
            factorValor = 0;
            selectedDate = currentDate;
            /*double desc = 1-(descuento / 100);
            Costo = Utilerias.limitarDecimales(Costo * desc);*/

        }



        makeRequest();

        producto = findViewById(R.id.lblDescripcion);
        costo = findViewById(R.id.lblPrecio);
        disponibles = findViewById(R.id.lblDisponibilidad);
        cantidad = findViewById(R.id.txtCant);
        agregar = findViewById(R.id.btnAgregar);
        cancelar = findViewById(R.id.btnCancelar);
        disclaimer =  findViewById(R.id.lblDisclaimer);
        fechaEntrega  = findViewById(R.id.txtFechaEntrega);


        producto.setText(Producto);
        costo.setText(String.valueOf(Costo));
        disponibles.setText(String.valueOf(Disp));

        fechaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePickerDialog();
            }
        });
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cant = Integer.parseInt(cantidad.getText().toString());
                int cantDisp = Integer.valueOf(Disp);

                if(cant<=cantDisp){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AgregarProductoActivity.this);
                    builder.setMessage("Su pedido: ("+cant+") "+producto.getText().toString()+ "\n¿Esta seguro?")
                            .setTitle("Confirmar Pedido")
                            .setPositiveButton("Hacer pedido", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                    Date test = new Date(selectedDate);
                                    dateFormat.format(test);*/

                                    makeRequestInsertPedido();
                                }
                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    builder.show();

                }else{
                    cantidad.setText("");
                    Toast.makeText(getBaseContext(),"La cantidad es mayor a la disponible",Toast.LENGTH_SHORT).show();

                }

            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void showDatePickerDialog() {
        final DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                selectedDate = year + "-" +day+ "-" + (month+1);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                currentDate = dateFormat.format(date);
                //currentDate

                Calendar c = Calendar.getInstance();

                int comp = selectedDate.compareTo(currentDate);
                if(comp < 0){

                    Toast.makeText(getApplicationContext(),"Fecha no valida",Toast.LENGTH_SHORT).show();
                    //btnGuardar.setEnabled(true);

                }
                fechaEntrega.setText(selectedDate);


            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private void makeRequestInsertPedido() {
        String url =getResources().getString(R.string.url)+"/insert_pedidos.php?costoFinal="
                +Utilerias.limitarDecimales(Costo*cant)
                +"&fechaPedido="+currentDate
                +"&fechaProgramada="+selectedDate
                +"&estatus=S"
                +"&cliente="+idCliente+"";
        url = url.replace(" ","%20");

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("pedidos");

                try {

                    for(int i=0; i < json.length(); i++){
                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);

                        responseNumber = jsonObject.optInt("MAX(id_Pedido)");

                        if(responseNumber !=0)
                            insertarDetallesPedido();
                    }
                }catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "No se pudo conectar "+response, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No se puede agregar el pedido "+error.getMessage(),Toast.LENGTH_SHORT).show();


            }
        });
        addToQueue(request);

    }

    private void insertarDetallesPedido() {

        makeRequestInsertarDetallesPedido(responseNumber,
                idProducto,
                Integer.valueOf(cantidad.getText().toString()),
                Costo,
                descuento,
                factorValor,
                Utilerias.limitarDecimales(Costo*Integer.valueOf(cantidad.getText().toString()))
        );
    }

    private void makeRequestInsertarDetallesPedido(int pedido, int producto, int cant, double precio, double desc, double fac, double costoFin ) {

        String url =getResources().getString(R.string.url)+"/insert_detalles_pedidos.php?idPedido="+pedido
                +"&idProducto="+producto
                +"&cantidad="+cant
                +"&precio="+precio
                +"&promocion="+desc
                +"&factor="+fac
                +"&costoFinal="+costoFin;
        url = url.replace(" ","%20");

        JsonObjectRequest requestDetalles = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseDP) {
                JSONArray json = responseDP.optJSONArray("detallepedidos");

                try {

                    for(int i=0; i < json.length(); i++){
                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);

                        insertExitoso = jsonObject.optBoolean("detallepedidos");

                    }
                }catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "No se pudo conectar "+e.getMessage(), Toast.LENGTH_LONG).show();
                    //e.printStackTrace();
                }

                if(insertExitoso){
                    modificarDisponibles();

                    //agregar update disponibles

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No se pudo realizar el pedido "+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        addToQueue(requestDetalles);

    }

    private void makeRequestInsertarComentarioPedido(int pedido, String comentario ) {

        String url =getResources().getString(R.string.url)+"/guardar_comentario.php?pedido="+pedido
                +"&comentario="+comentario;
        url = url.replace(" ","%20");

        JsonObjectRequest requestDetalles = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseDP) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No se pudo guardar el comentario "+error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        addToQueue(requestDetalles);

    }


    private void modificarDisponibles() {
        String columna ="Disponibilidad";
        if(!isDisponiblesValid){
            columna = "Minimo";
        }
        String url =getResources().getString(R.string.url)+"/update_disponibles.php?idProducto="
                +idProducto
                +"&cantidad="+cant
                +"&columna="+columna+"";
        url = url.replace(" ","%20");

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("isInsertSuccesful");

                try {

                    for(int i=0; i < json.length(); i++){
                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);

                        boolean updateSuccess = jsonObject.optBoolean("disponibles");

                        if(updateSuccess)
                            createCustomDialog().show();
                    }
                }catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "No se pudo conectar "+response, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No se puede agregar el pedido "+error.getMessage(),Toast.LENGTH_SHORT).show();


            }
        });
        addToQueue(request);

    }


    public android.support.v7.app.AlertDialog createCustomDialog() {
        final android.support.v7.app.AlertDialog alertDialog;
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_pedido_agregado, null);
        Button continuar = v.findViewById(R.id.btnContinuar);
        final EditText comentario = v.findViewById(R.id.txtComentario);


        builder.setView(v);
        alertDialog = builder.create();

        continuar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!comentario.getText().equals("")){
                            String textoComentario = comentario.getText().toString();
                            makeRequestInsertarComentarioPedido(responseNumber,textoComentario);
                        }

                        //finish();
                        Intent x = new Intent(v.getContext(), MainActivity.class);
                        x.putExtra("tipoCliente",tipoCliente);
                        x.putExtra("IdCliente",idCliente);
                        v.getContext().startActivity(x);


                    }
                }
        );

        return alertDialog;
    }

    private void makeRequest() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");


        String url =getResources().getString(R.string.url)+"/obtener_factor.php?fecha="+fechaExtra;//dateFormat.format(date);
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("factor");
                try {
                    for(int i=0; i <json.length(); i++){

                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);
                        //factorValor = jsonObject.optDouble("Factor");

                    }
                    /*if(factorValor>0)

                        costoCalculado = Costo * factorValor;
*/

                    if(descuento>0){
                        disclaimer.setVisibility(View.VISIBLE);
//                        Costo = costoCalculado * (1-descuento);
                    }else{
                        disclaimer.setVisibility(View.INVISIBLE);
                    }


                    //  costo.setText(String.valueOf(Math.floor(costoCalculado*100)/100d));

                }catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        addToQueue(request);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
