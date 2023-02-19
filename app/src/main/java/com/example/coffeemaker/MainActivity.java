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

                        guardarPreferencias();
                        Intent intent=new Intent(getApplicationContext(),principal.class);
                        intent.putExtra("datos",response);
                        startActivity(intent);
                        finish();
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
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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