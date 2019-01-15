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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditPerfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditPerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPerfilFragment extends BaseVolleyFragment {

    EditText txtContacto, txtDireccion, txtWhatsapp;
    Button btnGuardar;
    String telefono, direccion, whatsapp, nombre,tipoCliente;
    int id = 0;
    PerfilFragment fragmentPerfil;
    TextView lblId, lblNombre, lblTipocliente;
    StringRequest stringRequest;
    ProgressDialog progressDialog;


    private OnFragmentInteractionListener mListener;

    public EditPerfilFragment() {
        // Required empty public constructor
    }

    public static EditPerfilFragment newInstance() {
        EditPerfilFragment fragment = new EditPerfilFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        id = getArguments() != null ? getArguments().getInt("IdCliente") : id;
        telefono = getArguments() != null ? getArguments().getString("telefono") : "";
        nombre= getArguments() != null ? getArguments().getString("nombre") : "";
        direccion= getArguments() != null ? getArguments().getString("direccion") : "";
        whatsapp= getArguments() != null ? getArguments().getString("whatsapp") : "";
        tipoCliente= getArguments() != null ? getArguments().getString("tipocliente") : "";
        progressDialog= new ProgressDialog(getActivity());



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_perfil, container, false);
        txtContacto =  view.findViewById(R.id.txtContacto);
        txtWhatsapp =  view.findViewById(R.id.txtWhatsapp);
        txtDireccion =  view.findViewById(R.id.txtDireccion);
        lblId = view.findViewById(R.id.lblId);
        lblNombre= view.findViewById(R.id.lblName);
        lblTipocliente = view.findViewById(R.id.lblTipoCliente);

        txtContacto.setText(telefono);
        txtWhatsapp.setText(whatsapp);
        txtDireccion.setText(direccion);
        lblId.setText(String.valueOf(id));
        lblNombre.setText(nombre);
        lblTipocliente.setText(tipoCliente);

        progressDialog.setMessage("Cargando");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        fragmentPerfil = new PerfilFragment();
        btnGuardar =  (Button)view.findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telefono = txtContacto.getText().toString();
                direccion = txtDireccion.getText().toString();
                whatsapp = txtWhatsapp.getText().toString();
                //id = lblId.getText().toString();

                //Hacer update

                //cargarWebService();
               // webServiceActualizar();

                makeRequest();


            }
        });
        return view;
    }

    private void cargarWebService() {
    }

    private void makeRequest(){
        progressDialog.show();
        String url =getResources().getString(R.string.url)+"/update_cliente.php?";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equalsIgnoreCase("actualiza")){
                    progressDialog.hide();
                    Toast.makeText(getContext(),"Cambios guardados exitosamente", Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(getContext(),"Nose pudieron guardar los cambios", Toast.LENGTH_LONG).show();
                }

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putInt("IdUser",  id);
                fragmentPerfil.setArguments(args);
                transaction.replace(R.id.contenedor, fragmentPerfil).commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(),"Fallo en conexion: No se pudieron guardar los cambios" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String,String> parametros = new HashMap<>();
                parametros.put("direccion", direccion);
                parametros.put("telefono", telefono);
                parametros.put("whatsapp", whatsapp);
                parametros.put("idCliente", String.valueOf(id));

                return parametros;

            }
        };
        addToQueue(request);
        //VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
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
