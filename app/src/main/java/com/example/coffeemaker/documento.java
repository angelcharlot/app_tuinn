package com.example.coffeemaker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class documento extends AppCompatActivity {

    TextView t1;
    GridView Table;
    TableLayout area_detalle;
    Button btn;
    Array[] arrays;

    //vaiables de recuperacion activiti pasado
    String name_area;
    String name_mesa;
    String host;
    String id_area;
    String id_mesa;
    String cant_comesales;
    String negocio_id;
    String user_id;
    String bn_doc;

    String Comentario,categorias;

    JSONArray datos = new JSONArray();
    JSONObject fila;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documento);
        t1=findViewById(R.id.tv_ubi);
        btn=findViewById(R.id.button2);
        Table=findViewById(R.id.tabla);
        Comentario="";
        area_detalle=findViewById(R.id.detalle_comanda);
        arrays = new Array[10];
        //recuperando valores necerarios
        name_area=getIntent().getStringExtra("area_name");
        name_mesa=getIntent().getStringExtra("mesa_name");
        id_mesa=getIntent().getStringExtra("id_mesa");
        id_area=getIntent().getStringExtra("id_area");
        cant_comesales=getIntent().getStringExtra("nro_comensales");
        user_id=getIntent().getStringExtra("user_id");
        negocio_id=getIntent().getStringExtra("negocio_id");
        categorias=getIntent().getStringExtra("categorias");
        host=getIntent().getStringExtra("host");
        bn_doc=getIntent().getStringExtra("bn_doc");
        t1.setText("pedido en "+name_area+" "+name_mesa);
        //boton de fonfirmacion de pedido
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    btn();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //lRP=(LinearLayout)findViewById(R.id.lineP);
        area_detalle.removeAllViews();

        rellenar_vista_productos(getIntent().getStringExtra("datos_productos"));


    }

    public void btn() throws JSONException {

        if (area_detalle.getChildCount() <= 0) {
            Toast.makeText(this, "Ningun cambio realizado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        for (int i = 0; i < area_detalle.getChildCount(); i++) {
            TableRow row = (TableRow) area_detalle.getChildAt(i);

            TextView cantidad = (TextView) row.getChildAt(0);
            TextView name = (TextView) row.getChildAt(1);
            TextView tipo = (TextView) row.getChildAt(2);
            TextView precio = (TextView) row.getChildAt(3);
            TextView id_p = (TextView) row.getChildAt(4);
            TextView id_impre = (TextView) row.getChildAt(5);


            String cant = cantidad.getText().toString();
            String nombre = name.getText().toString();
            String presentacion = tipo.getText().toString();
            String precio_venta = precio.getText().toString();
            String preoducto_id = id_p.getText().toString();
            String impresora_id = id_impre.getText().toString();


            fila = new JSONObject();
            try {
                fila.put("cantidad", cant);
                fila.put("name", nombre);
                fila.put("producto_id", preoducto_id);
                fila.put("tipo", presentacion);
                fila.put("precio", precio_venta);
                fila.put("impresora", impresora_id);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            datos.put(i, fila);
            Log.i("dato",datos.toString());
        }//fin de ciclo for

       // t1.setText(datos.toString());

        JsonArrayRequest arrayRequest= new JsonArrayRequest(Request.Method.POST, host+"/api/comandar?id_area="+id_area+"&id_mesa="+id_mesa+"&user_id="+user_id+"&nro_comensales="+cant_comesales+"&bn_doc="+bn_doc+"&comentario="+Comentario,datos, new  Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {

                finish();
                Toast.makeText(documento.this,"Comanda enviada", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(documento.this,"Error de envio", Toast.LENGTH_SHORT).show();


            }
        });

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(arrayRequest);



    }//fin de del metodo oncreate

    public void btn_coment(View view){

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Nota");
        View selector=getLayoutInflater().inflate(R.layout.layoutcomentario,null);
        builder.setView(selector);
        EditText Et_comentario=(EditText)selector.findViewById(R.id.comentario);
        Et_comentario.setText(Comentario);
        builder.setPositiveButton("confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int r) {
              Comentario=Et_comentario.getText().toString();
                Toast.makeText(documento.this, "comentario agregado", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();

    }

    public  void  cancelar(View view){
        Toast.makeText(this, "pedido cancelado", Toast.LENGTH_SHORT).show();
        finish();
        return;
    }

    public void rellenar_vista_productos(String data_productos){


        try {
            JSONArray arrayproductos=new JSONArray(data_productos);
            adactador ada= new adactador(this,arrayproductos,R.layout.producto,area_detalle);
            Table.setAdapter(ada);



        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void mostrarDialogoDeFiltro(View view) {
        // Crea el array con las opciones del filtro


        String[] opcionesFiltro;
        try {
            JSONArray cate = new JSONArray(categorias);
            opcionesFiltro = new String[cate.length()];

            for (int i = 0; i < cate.length(); i++) {

                opcionesFiltro[i] = cate.getJSONObject(i).getString("name");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Seleccione una opción");

            builder.setItems(opcionesFiltro, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Aquí puedes manejar la opción seleccionada
                    try {
                        rellenar_vista_productos(cate.getJSONObject(which).getString("productos").toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            builder.show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Crea el diálogo de alerta


        // Muestra el diálogo de alerta

    }


}//fin de la clase