package com.daicon.pc.ppesa;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PerfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends BaseVolleyFragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    EditPerfilFragment editPerfilFragment;
    Button btnEditarInfo;
    int id,tCliente;
    RequestQueue request;
    //JsonObjectRequest jsonObjectRequest;
    TextView nombre,tipoCliente,contacto,direccion,whatsapp;
    ProgressDialog progressDialog;



    private OnFragmentInteractionListener mListener;

    public PerfilFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance() {
        PerfilFragment fragment = new PerfilFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        id = bundle.getInt("IdCliente");
        tCliente = bundle.getInt("tipoCliente");
        request = Volley.newRequestQueue(getContext());

        progressDialog = new ProgressDialog( getActivity());
        makeRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        progressDialog.setMessage("Cargando");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //id = getArguments() != null ? getArguments().getInt("IdUser") : 0;

        nombre = (TextView)view.findViewById(R.id.lblName);
        tipoCliente = (TextView)view.findViewById(R.id.lblTipoCliente);
        contacto = (TextView)view.findViewById(R.id.lblContacto);
        direccion = (TextView)view.findViewById(R.id.lblDir);
        whatsapp = (TextView)view.findViewById(R.id.lblWhatsapp);


        btnEditarInfo = (Button)view.findViewById(R.id.btnEditarInfo);
        editPerfilFragment = new EditPerfilFragment();
        btnEditarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putInt("IdCliente",  id);
                args.putInt("tipoCliente",  tCliente);
                args.putString("nombre",nombre.getText().toString());
                args.putString("tipocliente",tipoCliente.getText().toString());
                args.putString("telefono",contacto.getText().toString());
                args.putString("direccion",direccion.getText().toString());
                args.putString("whatsapp",whatsapp.getText().toString());
                editPerfilFragment.setArguments(args);
                transaction.replace(R.id.contenedor, editPerfilFragment).commit();
            }
        });



        return view;
    }

    private void makeRequest() {
        progressDialog.show();
        String url = getResources().getString(R.string.url)+"/consulta_cliente.php?id="+id;
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cliente cliente = null;

                JSONArray json = response.optJSONArray("cliente");

                try{
                    cliente = new Cliente();
                    JSONObject jsonObject = null;


                    for (int i = 0; i < json.length(); i++) {
                        jsonObject = json.getJSONObject(i);
                        cliente.setID(jsonObject.optInt("idCliente"));
                        cliente.setNombre(jsonObject.optString("Nombre"));
                        cliente.setDireccionEntrega(jsonObject.optString("DireccionEntrega"));
                        cliente.setTelefono(jsonObject.optString("Telefono"));
                        cliente.setWhatsapp(jsonObject.optString("Whatsapp"));
                        cliente.setTipoCliente(jsonObject.optString("Descripcion"));
                        cliente.set_idUsuario(jsonObject.optInt("idCliente"));

                        if(cliente!=null){
                            agregarDatos(cliente);
                        }

                    }


                }catch (JSONException e){
                    progressDialog.hide();
                    Toast.makeText(getContext(), "No se pudo conectar " + response, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getContext(), "No se pudo consultar "+error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });

        //jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        //request.add(jsonObjectRequest);
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

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se pudo consultar", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onResponse(JSONObject response) {
        Cliente cliente = null;

        JSONArray json = response.optJSONArray("cliente");

        try{
            cliente = new Cliente();
            JSONObject jsonObject = null;


            for (int i = 0; i < json.length(); i++) {
                jsonObject = json.getJSONObject(i);
                cliente.setID(jsonObject.optInt("idCliente"));
                cliente.setNombre(jsonObject.optString("Nombre"));
                cliente.setDireccionEntrega(jsonObject.optString("DireccionEntrega"));
                cliente.setTelefono(jsonObject.optString("Telefono"));
                cliente.setWhatsapp(jsonObject.optString("Whatsapp"));
                cliente.setTipoCliente(jsonObject.optString("Descripcion"));
                cliente.set_idUsuario(jsonObject.optInt("idCliente"));

                if(cliente!=null){
                    agregarDatos(cliente);
                }

            }


        }catch (JSONException e){
            Toast.makeText(getContext(), "No se pudo conectar " + response, Toast.LENGTH_LONG).show();
        }



    }
    public void agregarDatos(Cliente c){
        nombre.setText(c.getNombre().toString());
        contacto.setText(c.getTelefono().toString());
        whatsapp.setText(c.getWhatsapp().toString());
        tipoCliente.setText(c.getTipoCliente());
        direccion.setText(c.getDireccionEntrega().toString());
        progressDialog.hide();
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
