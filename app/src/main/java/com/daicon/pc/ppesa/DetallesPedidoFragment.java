package com.daicon.pc.ppesa;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetallesPedidoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetallesPedidoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetallesPedidoFragment extends BaseVolleyFragment {

    private OnFragmentInteractionListener mListener;
    ProgressDialog progressDialog;
    RecyclerView recyclerViewDetallesPedido;
    ArrayList<Pedidos> listaProductosPedido;
    int idPedido;
    TextView fechaPedido, referencia, estatus, totalDetalles, sub, iva, totalx;
    String fecha, estatusDes;
    double total =0, factorValor =0;


    public DetallesPedidoFragment() {
        // Required empty public constructor
    }


    public static DetallesPedidoFragment newInstance(String param1, String param2) {
        DetallesPedidoFragment fragment = new DetallesPedidoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        idPedido = bundle != null ? bundle.getInt("idPedido") : 0;
        fecha =bundle != null ? bundle.getString("fecha") : "No disponible";
        estatusDes =bundle != null ? bundle.getString("estatus") : "No disponible";
        progressDialog = new ProgressDialog(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog.show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalles_pedido, container, false);
        estatus = view.findViewById(R.id.lblEstatusDetalle);
        referencia = view.findViewById(R.id.lblReferenciaDetalles);
        fechaPedido = view.findViewById(R.id.lblFechaDetalles);
        totalDetalles = view.findViewById(R.id.lblTotalGrande);
        listaProductosPedido = new ArrayList<>();
        recyclerViewDetallesPedido = view.findViewById(R.id.rvDetallesPedido);
        GridLayoutManager llm = new GridLayoutManager(getContext(),1 );
        recyclerViewDetallesPedido.setLayoutManager(llm);
        sub = view.findViewById(R.id.lblsubTotal);
        iva = view.findViewById(R.id.lblIVA);
        totalx = view.findViewById(R.id.lblTotalDetalles);

        referencia.setText("Referencia: "+idPedido);
        estatus.setText("Estatus: "+estatusDes);
        fechaPedido.setText("Fecha: "+fecha);

        makeRequest();
        return view;
    }

    private void makeRequest() {

        String url =getResources().getString(R.string.url)+"/consultar_detalles_pedido.php?id="+idPedido;
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Pedidos detallesPedidos = null;

                JSONArray json = response.optJSONArray("pedido");
                try {

                    for(int i=0; i < json.length(); i++){
                        detallesPedidos = new Pedidos();
                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);

                        double precio;
                        int cant;
                        factorValor = jsonObject.optDouble("Factor");
                        detallesPedidos.setID(jsonObject.optInt("id_Pedido"));
                        detallesPedidos.setPrecio(Utilerias.limitarDecimales(jsonObject.optDouble("Precio")));
                        detallesPedidos.setFecha(jsonObject.optString("FechaPedido"));
                        detallesPedidos.setEstatus(jsonObject.optString("DescEst"));
                        detallesPedidos.setIdProducto(jsonObject.optInt("_idProducto"));
                        detallesPedidos.setCantidad(jsonObject.optInt("Cantidad"));
                        detallesPedidos.setDescuento(jsonObject.optInt("Promocion"));
                        detallesPedidos.setFactor(jsonObject.optDouble("Factor"));
                        detallesPedidos.setDescripcionProducto(jsonObject.optString("DescProd"));


                        /*SELECT P.id_Pedido, P.CostoFinal,
                        P.FechaPedido,P.FechaProgramada,
                        P.FechaRealEntrega,E.Descripcion DescEst,
                        PR.Descripcion DescProd, DP.Cantidad,
                        DP.Precio,DP.Promocion,DP.Factor,DP.costoFinal
                        FROM Pedidos P
                        INNER JOIN DetallesPedidos DP ON DP.idPedido = P.id_Pedido
                        INNER JOIN Productos PR ON PR.id_Producto = DP._idProducto
                        INNER JOIN EstatusPedidos E ON E.idEstatus = P._idEstatus WHERE P.id_Pedido*/

                        total = (jsonObject.optDouble("costoFinal"));

                        listaProductosPedido.add(detallesPedidos);

                        AdaptadorDetallesPedido adapter = new AdaptadorDetallesPedido(listaProductosPedido);
                        recyclerViewDetallesPedido.setAdapter(adapter);


                    }
                    //total = factorValor*total;
                    double ivaCalculado = Utilerias.limitarDecimales(total*0.16);
                    totalDetalles.setText("$"+String.valueOf(Utilerias.limitarDecimales(total+ivaCalculado)));
                    totalx.setText(totalDetalles.getText());
                    iva.setText("$"+String.valueOf((ivaCalculado)));
                    sub.setText("$"+String.valueOf(String.valueOf((total))));
                    progressDialog.hide();
                }catch (JSONException e) {
                    progressDialog.hide();
                    Toast.makeText(getContext(), "No se pudo conectar "+response, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getContext(), "No se pudo conectar "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        //jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        //request.add(jsonObjectRequest);
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
        void onFragmentInteraction(Uri uri);
    }
}
