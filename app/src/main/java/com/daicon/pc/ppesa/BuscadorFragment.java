package com.daicon.pc.ppesa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
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
import java.util.List;

public class BuscadorFragment extends BaseVolleyFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    boolean disponibles = false;
    RecyclerView recyclerViewBuscador;
    ArrayList<Productos> listaProductos;
    Productos producto;
    BuscadorFragment buscadorFragment;
    int idCliente,tipoCliente;


    AdaptadorBuscador adapter;
    private OnFragmentInteractionListener mListener;

    public BuscadorFragment() {
    }

    public static BuscadorFragment newInstance(String param1, String param2) {
        BuscadorFragment fragment = new BuscadorFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buscadorFragment = new BuscadorFragment();
        Bundle args = getArguments();
        idCliente = args.getInt("IdCliente");
        tipoCliente= args.getInt("tipoCliente",0);
        disponiblesModificados();
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscador, container, false);

        listaProductos = new ArrayList<>();
        recyclerViewBuscador = view.findViewById(R.id.rvBuscador);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerViewBuscador.setLayoutManager(llm);
        recyclerViewBuscador.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        makeRequest();


        return view;
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

    private void makeRequest() {
        String url =getResources().getString(R.string.url)+"/consulta_todos_prodductos.php";
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                JSONArray json = response.optJSONArray("linea");
                try {

                    for(int i=0; i < json.length(); i++){
                        producto = new Productos();
                        JSONObject jsonObject = null;

                        jsonObject = json.getJSONObject(i);

                        producto.setDescripcion(jsonObject.optString("Descripcion"));
                        producto.setCosto(jsonObject.optString("Precio"));
                        if(disponibles)
                            producto.setDisponibles(jsonObject.getString("Disponibilidad"));
                        if(!disponibles)
                            producto.setDisponibles(jsonObject.getString("Minimo"));
                        producto.setID(jsonObject.getInt("id_Producto"));
                        producto.setLinea(jsonObject.getInt("_idLinea"));

                        listaProductos.add(producto);

                        adapter = new AdaptadorBuscador(listaProductos);

                        recyclerViewBuscador.setAdapter(adapter);

                        adapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent x = new Intent(getContext(), AgregarProductoActivity.class);

                                Bundle b = new Bundle();
                                b.putSerializable("producto", (Serializable) adapter.listaProductos.get((recyclerViewBuscador.getChildAdapterPosition(v))));
                                x.putExtra("BundleProd",b);
                                x.putExtra("IdCliente", idCliente);
                                x.putExtra("tipoCliente",tipoCliente);
                                x.putExtra("pantalla", "buscador");
                                x.putExtra("columna", disponibles);

                                startActivity(x);
                            }
                        });


                    }
                }catch (JSONException e) {
                    Toast.makeText(getContext(), "No se pudo conectar "+response, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se pudo conectar "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addToQueue(request);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater ) {
        inflater.inflate(R.menu.action_bar_menu2, menu);
        MenuItem searchItem = menu.findItem(R.id.buscador);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");

        super.onCreateOptionsMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle args = new Bundle();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.buscador:

                transaction.replace(R.id.contenedor, buscadorFragment).commit();

                return true;
        }
        return false;
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
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    public void resetSearch() {
        adapter.listaProductos= listaProductos;
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        ArrayList<Productos> filteredValues = new ArrayList<>(listaProductos);
        for (Productos value : listaProductos) {
            if (!value.getDescripcion().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }
        adapter.listaProductos = filteredValues;
        adapter.notifyDataSetChanged();

        return false;
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
