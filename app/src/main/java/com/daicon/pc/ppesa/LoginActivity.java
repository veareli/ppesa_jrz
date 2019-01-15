package com.daicon.pc.ppesa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener  {

    Button entrar;
    EditText usuarioT, passT;
    TextView sinUsuario;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    Usuario miUsuario = new Usuario();
    int ID_Cliente, tipoCliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getSupportActionBar().hide();

        if(!getFromSharedPreferences("Usuario").equals("")){
            Intent main = new Intent(this, MainActivity.class);
            main.putExtra("IdCliente", getFromSharedPreferencesInt("idCliente"));
            main.putExtra("tipoCliente",getFromSharedPreferencesInt("tipoCliente"));
            startActivity(main);

        }

        entrar = (Button) findViewById(R.id.btnEntrar);
        usuarioT = (EditText) findViewById(R.id.txtUser);
        passT = (EditText) findViewById(R.id.txtPass);
        sinUsuario = findViewById(R.id.lblSinUsuario);


        request = Volley.newRequestQueue(getBaseContext());

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarWebService(usuarioT.getText().toString(),passT.getText().toString());
            }
        });
        sinUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Contacta a alguien del personal para crear tu usuario y contraseña.",Toast.LENGTH_LONG).show();
            }
        });


    }//fin Oncreate

    private void cargarWebService(String usuario, String Passwrd) {

        String url = getResources().getString(R.string.url)+"/login_app.php?Usuario="+usuario;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        request.add(jsonObjectRequest);

    }

    private int getFromSharedPreferencesInt(String key){
        SharedPreferences sharedPref = getSharedPreferences("login_shared",Context.MODE_PRIVATE);
        return sharedPref.getInt(key,0);
    }

    private String getFromSharedPreferences(String key){
        SharedPreferences sharedPref = getSharedPreferences("login_shared",Context.MODE_PRIVATE);
        return sharedPref.getString(key,"");
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        //progreso.hide();;
        Toast.makeText(this, "No se pudo consultar. Login", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onResponse(JSONObject response) {
        // progreso.hide();


        JSONArray json = response.optJSONArray("usuario");
        JSONObject jsonObject = null;

        try {
            jsonObject = json.getJSONObject(0);
            miUsuario.setUser(jsonObject.optString("Usuario"));//Respeta uppercase
            miUsuario.setPass(jsonObject.optString("Password"));
            ID_Cliente = jsonObject.optInt("idCliente");
            tipoCliente = jsonObject.optInt("_idTipoCliente");
            validacionLogin(usuarioT.getText().toString(),passT.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void validacionLogin(String userField, String passField) {
        if(userField.equals(miUsuario.getUser())){
            if(passField.equals(miUsuario.getPass())){

                saveLoginShared(miUsuario.getUser(),miUsuario.getPass());

                Intent valido = new Intent(getApplicationContext(), MainActivity.class );
                valido.putExtra("IdCliente", ID_Cliente);
                valido.putExtra("tipoCliente", tipoCliente);
                //Intent valido = new Intent(getApplicationContext(), LineasActivity.class );
                startActivity(valido);
                finish();


            }else{
                Toast.makeText(this,"Password incorrecto", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,"Usuario invalido", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveLoginShared(String user, String pass) {
        SharedPreferences sharedPref = getSharedPreferences("login_shared",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Usuario",user);
        editor.putString("Password",pass);
        editor.putInt("idCliente",ID_Cliente);
        editor.putInt("tipoCliente",tipoCliente);
        editor.apply();

    }



}
