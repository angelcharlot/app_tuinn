package com.example.coffeemaker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

public class detalle extends AppCompatActivity {

    //id de la mesa
    String id_mesa,host,user_id,tipo_pago;
    TableLayout table_coblo;
    View row_detalle;
    TextView total_cobro,tv_area,tv_mesa,tv_pex,tv_nro_doc;
    JSONObject datos;
    ProgressBar gitdecarga;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        table_coblo=(TableLayout) findViewById(R.id.table_cobro);
        total_cobro=findViewById(R.id.tv_total_total);
        tv_area=findViewById(R.id.tv_area);
        tv_mesa=findViewById(R.id.tv_mesa);
        tv_pex=findViewById(R.id.tv_pex);
        tv_nro_doc=findViewById(R.id.tv_nro_doc);
        table_coblo.removeAllViews();
        gitdecarga=findViewById(R.id.progressBardetalle);
        id_mesa = getIntent().getStringExtra("id_mesa");
        host = getIntent().getStringExtra("host");
        user_id = getIntent().getStringExtra("user_id");
        StringRequest stringRequest= new StringRequest(Request.Method.POST,host+"/api/detalle", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                try {
                    datos= new JSONObject(response);
                    tv_area.setText(datos.getJSONObject("mesa").getJSONObject("area").getString("name"));
                    tv_mesa.setText("Mesa:"+datos.getJSONObject("mesa").getString("nro"));
                    tv_pex.setText("Comensales:"+datos.getString("cam1"));
                    tv_nro_doc.setText(datos.getString("nro_documento"));
                    Float t=Float.parseFloat(datos.getString("total"));

                   total_cobro.setText(String.format( "%.2f",t));

                    for (int i=0;i<datos.getJSONArray("detalles").length();i++) {
                        row_detalle = LayoutInflater.from(detalle.this).inflate(R.layout.row_tiket, null, false);
                        TextView name = row_detalle.findViewById(R.id.tv_name);
                        TextView cantidad = row_detalle.findViewById(R.id.tv_cantidad);

                        TextView precio_u = row_detalle.findViewById(R.id.tv_precio_u);
                        TextView tv_total = row_detalle.findViewById(R.id.tv_total);

                        Float pu=Float.parseFloat(datos.getJSONArray("detalles").getJSONObject(i).getString("precio_venta"));
                        Float ccantidad=Float.parseFloat(datos.getJSONArray("detalles").getJSONObject(i).getString("cantidad"));

                        Float cant_total=  pu *  ccantidad;


                        name.setText(datos.getJSONArray("detalles").getJSONObject(i).getString("name")+datos.getJSONArray("detalles").getJSONObject(i).getString("tipo_presentacion"));
                        cantidad.setText(datos.getJSONArray("detalles").getJSONObject(i).getString("cantidad"));

                        precio_u.setText(datos.getJSONArray("detalles").getJSONObject(i).getString("precio_venta"));
                        tv_total.setText(String.format( "%.2f",(cant_total)));
                        table_coblo.addView(row_detalle);


                    }
                        gitdecarga.setVisibility(View.GONE);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }







            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(detalle.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("user_id",user_id);
                parametros.put("id_mesa",id_mesa);
                return parametros;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);




      }

    public void btn_atras(View view){

        finish();
        return;

    }
    public void btn_imprimir(View view){

        StringRequest stringRequest= new StringRequest(Request.Method.POST,host+"/api/imprimir_tiket", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(detalle.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(detalle.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("user_id",user_id);
                parametros.put("id_mesa",id_mesa);
                return parametros;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }//fin de btn imprimir

    public  void btn_cobrar(View view){
        String[] tipos;
        tipos =new String[2];

        tipos[0]="Efectivo";
        tipos[1]="Targeta";
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Metodo de pago");
        builder.setItems(tipos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int r) {

                if (r==0){
                    tipo_pago="Efectivo";
                }else{
                    tipo_pago="Targeta";
                }

                StringRequest stringRequest= new StringRequest(Request.Method.POST,host+"/api/cobrar", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(detalle.this, response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(detalle.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> parametros=new HashMap<String,String>();
                        parametros.put("user_id",user_id);
                        parametros.put("id_mesa",id_mesa);
                        parametros.put("tipo_pago",tipo_pago);
                        return parametros;
                    }
                };

                RequestQueue requestQueue= Volley.newRequestQueue(detalle.this);
                requestQueue.add(stringRequest);

                finish();

                //
            }
        }).show();


    }


}//fin de la clase