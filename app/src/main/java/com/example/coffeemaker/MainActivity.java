package com.example.coffeemaker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText Edtemail,Edtpass,host_server;
    Button Btnlogin;
    String usuario,pass,host;

    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Edtemail=findViewById(R.id.editTextTextPersonName);
        Edtpass=findViewById(R.id.editTextTextPassword);
        Btnlogin=findViewById(R.id.button);
        spinner=findViewById(R.id.progressBar);
        host_server=findViewById(R.id.host);
        recupererPreferencias();
        Btnlogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                usuario=Edtemail.getText().toString();
                pass=Edtpass.getText().toString();
                host=host_server.getText().toString();
                if (!usuario.isEmpty() && !pass.isEmpty() && !host.isEmpty()){
                    Btnlogin.setEnabled(false);
                    validar(host+"/api/users");
                }else{
                    Toast.makeText(MainActivity.this, "No se permiten campos vacios", Toast.LENGTH_SHORT).show();
                    Btnlogin.setEnabled(true);
                }

            }
        });
    }

    public void validar(String URL){
        spinner.setVisibility(View.VISIBLE);
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                    if (!response.isEmpty()){
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        String mensaje = jsonObject.optString("mensaje");
                        switch (mensaje) {
                            case "1":
                                Toast.makeText(MainActivity.this, "email invalido", Toast.LENGTH_SHORT).show();
                                Btnlogin.setEnabled(true);
                                spinner.setVisibility(View.GONE);
                                break;
                            case "2":
                                Toast.makeText(MainActivity.this, "password invalido", Toast.LENGTH_SHORT).show();
                                Btnlogin.setEnabled(true);
                                spinner.setVisibility(View.GONE);
                                break;
                            case "3":
                                Toast.makeText(MainActivity.this,"logueo completado", Toast.LENGTH_SHORT).show();

                                guardarPreferencias();
                                Intent intent=new Intent(getApplicationContext(),principal.class);
                                intent.putExtra("datos",response);
                                startActivity(intent);
                                finish();

                                break;
                            // ...
                            default:
                                // código a ejecutar si la variable no es igual a ninguno de los valores anteriores
                                break;
                        }

                   }else{
                        Btnlogin.setEnabled(true);
                        spinner.setVisibility(View.GONE);
                       Toast.makeText(MainActivity.this, "usuario o clave erronea", Toast.LENGTH_SHORT).show();
                   }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.setVisibility(View.GONE);
                Btnlogin.setEnabled(true);
                if (error != null && error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                switch (statusCode) {
                    case 400:
                        Toast.makeText(MainActivity.this, "Solicitud incorrecta", Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        Toast.makeText(MainActivity.this, "No autorizado", Toast.LENGTH_SHORT).show();
                        break;
                    case 403:
                        Toast.makeText(MainActivity.this, "Prohibido", Toast.LENGTH_SHORT).show();
                        break;
                    case 404:
                        Toast.makeText(MainActivity.this, "No encontrado", Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(MainActivity.this, "Error interno del servidor", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Error desconocido", Toast.LENGTH_SHORT).show();
                        break;
                }
                }else {
                    Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("email",usuario);
                parametros.put("pass",pass);
                return parametros;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void guardarPreferencias(){
        SharedPreferences preferences=getSharedPreferences("preferenciaslogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("usuario",usuario);
        editor.putString("password", pass);
        editor.putString("host", host);
        editor.putBoolean("sesion",true);
        editor.commit();
    }

    private void recupererPreferencias(){
        SharedPreferences preferences=getSharedPreferences("preferenciaslogin", Context.MODE_PRIVATE);
        Edtemail.setText(preferences.getString("usuario","micorreo@gmail.com"));
        Edtpass.setText(preferences.getString("password","123456"));
        host_server.setText(preferences.getString("host","http://192.168.0.185:8000"));
    }

}