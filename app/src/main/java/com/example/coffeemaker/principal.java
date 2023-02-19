    package com.example.coffeemaker;

    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;

    import android.annotation.SuppressLint;
    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.graphics.Color;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.view.Gravity;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TableLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.android.volley.AuthFailureError;
    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import com.squareup.picasso.Picasso;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.text.MessageFormat;
    import java.util.HashMap;
    import java.util.Map;


    public class principal extends AppCompatActivity {

        //variables
         TextView welcome,welcome2;
         ImageView imgnegocio;
         ImageView imgqr;
         String id;
         String name,datos_string;
         String negocio_id;
         String user_id;
         TableLayout tabla;
         View registro_mesas;
         Context contesto;
        String bn_doc;
         LinearLayout layout1;
         //host de conexion
        String host;

        @SuppressLint({"WrongViewCast", "MissingInflatedId", "ResourceAsColor", "SetTextI18n"})
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_principal);
            contesto=this;
            SharedPreferences preferences=getSharedPreferences("preferenciaslogin", Context.MODE_PRIVATE);
            host=(preferences.getString("host",null));
            time time=new time();
            time.execute();
            datos_string=getIntent().getStringExtra("datos");
            //vinculacion con texview de la vista
            welcome= findViewById(R.id.textmostrar);
            welcome2=findViewById(R.id.textmostrar_id);
            imgnegocio=findViewById(R.id.imagenegociovm);
            imgqr=findViewById(R.id.imagenqr);
            tabla=(TableLayout)findViewById(R.id.table_) ;
            tabla.removeAllViews();
            //try para el procesamiento de json
            try {
                //json con datos usuario y negocio
                JSONObject datos=new JSONObject(datos_string);
                name = datos.getJSONObject("usuario").getString("name");
                user_id=datos.getJSONObject("usuario").getString("id");
                negocio_id=datos.getJSONObject("negocio").getString("id");
                Picasso.get().load(datos.getJSONObject("negocio").getString("img")).into(imgnegocio);
                Picasso.get().load(datos.getJSONObject("negocio").getString("img_qr")).into(imgqr);
                welcome2.setText(MessageFormat.format("Bienvenido {0}", name));
                welcome.setText(datos.getJSONObject("usuario").getString("email"));
                //array de json con areas array de mesas.
                JSONArray areas=new JSONArray(datos.getString("areas"));
                for (int i=0 ; i < areas.length();i++){
                    JSONObject area = areas.getJSONObject(i);

                    TextView registro_areas= new TextView(this);
                    registro_areas.setText(area.getString("name"));
                    registro_areas.setGravity(Gravity.CENTER);
                    registro_areas.setBackgroundColor(Color.rgb(0,100,250));
                    registro_areas.setTextColor(Color.WHITE);
                    registro_areas.setTextSize(20);
                    registro_areas.setHeight(80);
                    tabla.addView(registro_areas);
                    for (int y=0 ; y < areas.getJSONObject(i).getJSONArray("mesas").length();y++){
                        JSONObject mesas =  areas.getJSONObject(i).getJSONArray("mesas").getJSONObject(y);
                        registro_mesas = LayoutInflater.from(this).inflate(R.layout.mesas_row,null,false);
                        TextView mesa=(TextView)registro_mesas.findViewById(R.id.mesa_nro);
                        TextView total=registro_mesas.findViewById(R.id.total);
                        Button btn_add=registro_mesas.findViewById(R.id.btn_add);
                        Button btn_remove=registro_mesas.findViewById(R.id.btn_remove);
                        Button btn_detalle=registro_mesas.findViewById(R.id.btn_detalle);
                        total.setText("-----");
                        mesa.setText(mesas.getString("name"));
                        bn_doc=mesas.getString("doc_activo");
                        if(mesas.getString("doc_activo").equals("1")){
                            total.setText(mesas.getString("total_doc")+"€");
                        }

                        //eventos--- cuando ya hay una comanda activa-------------------------------------------------------
                        if (mesas.getString("doc_activo").equals("1")){
                            registro_mesas.setBackgroundColor(Color.rgb(230,255,230));
                            btn_remove.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Intent intentajuste=new Intent(getApplicationContext(),ajuste.class);
                                        intentajuste.putExtra("id_mesa",mesas.getString("id"));
                                        startActivity(intentajuste);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                            btn_detalle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                        Intent intentdetalle=new Intent(getApplicationContext(),detalle.class);
                                    try {
                                        intentdetalle.putExtra("id_mesa",mesas.getString("id"));
                                        intentdetalle.putExtra("user_id",user_id);
                                        intentdetalle.putExtra("host",host);

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    startActivity(intentdetalle);


                                }
                            });
                            int finalI1 = i;
                            int finalY1 = y;
                            btn_add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    try {
                                        Intent intent=new Intent(getApplicationContext(),documento.class);
                                        intent.putExtra("datos_productos",datos.getString("productos"));
                                        intent.putExtra("categorias",datos.getString("categorias"));
                                        intent.putExtra("nro_comensales","0");
                                        intent.putExtra("mesa_name",areas.getJSONObject(finalI1).getJSONArray("mesas").getJSONObject(finalY1).getString("name"));
                                        intent.putExtra("id_mesa",areas.getJSONObject(finalI1).getJSONArray("mesas").getJSONObject(finalY1).getString("id"));
                                        intent.putExtra("id_area",areas.getJSONObject(finalI1).getString("id"));
                                        intent.putExtra("area_name",areas.getJSONObject(finalI1).getString("name"));
                                        intent.putExtra("negocio_id",negocio_id);
                                        intent.putExtra("user_id",user_id);
                                        intent.putExtra("bn_doc",bn_doc);
                                        intent.putExtra("host",host);
                                        startActivity(intent);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }else{
                            //cuando hay que abrir la mesa-----------------------------------------------------------------
                            int finalY = y;
                            int finalI = i;
                            btn_add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    AlertDialog.Builder builder=new AlertDialog.Builder(contesto);
                                    builder.setMessage("Seleccione el numero de comensales");
                                    View selector=getLayoutInflater().inflate(R.layout.layoutnrocomensales,null);
                                    builder.setView(selector);
                                    EditText nro_comensales_tx=(EditText)selector.findViewById(R.id.nro_comensales);

                                    builder.setPositiveButton("confirmar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int rr) {
                                            try {
                                                Intent intent=new Intent(getApplicationContext(),documento.class);
                                                intent.putExtra("datos_productos",datos.getString("productos"));
                                                intent.putExtra("categorias",datos.getString("categorias"));
                                                intent.putExtra("nro_comensales",nro_comensales_tx.getText().toString());
                                                intent.putExtra("mesa_name",areas.getJSONObject(finalI).getJSONArray("mesas").getJSONObject(finalY).getString("id"));
                                                intent.putExtra("id_mesa",areas.getJSONObject(finalI).getJSONArray("mesas").getJSONObject(finalY).getString("id"));
                                                intent.putExtra("id_area",areas.getJSONObject(finalI).getString("id"));
                                                intent.putExtra("area_name",areas.getJSONObject(finalI).getString("name"));
                                                intent.putExtra("negocio_id",negocio_id);
                                                intent.putExtra("user_id",user_id);
                                                intent.putExtra("bn_doc",bn_doc);
                                                intent.putExtra("host",host);
                                                 startActivity(intent);
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                    builder.create().show();



                                }
                            });
                        }
                        tabla.addView(registro_mesas);
                    }

                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }



        public void hilo() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        public void ejecutar(){
            time time=new time();
            time.execute();
        }
        public class time extends AsyncTask<Void,Integer,Boolean>
        {

            @Override
            protected Boolean doInBackground(Void... voids) {
                for (int i=0; i<2;i++){
                    hilo();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                StringRequest stringRequest= new StringRequest(Request.Method.POST, host+"/api/areas", new Response.Listener<String>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(String response) {
                            tabla.removeAllViews();
                        try {
                            JSONArray areas = new JSONArray(response);
                            JSONObject datos=new JSONObject(datos_string);
                            for (int i=0 ; i < areas.length();i++){
                                JSONObject area = areas.getJSONObject(i);

                                TextView registro_areas= new TextView(contesto);
                                registro_areas.setText(area.getString("name"));
                                registro_areas.setGravity(Gravity.CENTER);
                                registro_areas.setBackgroundColor(Color.rgb(0,100,250));
                                registro_areas.setTextColor(Color.WHITE);
                                registro_areas.setTextSize(20);
                                registro_areas.setHeight(80);


                                tabla.addView(registro_areas);


                                for (int y=0 ; y < areas.getJSONObject(i).getJSONArray("mesas").length();y++){
                                    JSONObject mesas =  areas.getJSONObject(i).getJSONArray("mesas").getJSONObject(y);

                                    registro_mesas = LayoutInflater.from(contesto).inflate(R.layout.mesas_row,null,false);
                                    TextView mesa=(TextView)registro_mesas.findViewById(R.id.mesa_nro);
                                    TextView total=registro_mesas.findViewById(R.id.total);
                                    Button btn_add=registro_mesas.findViewById(R.id.btn_add);
                                    Button btn_remove=registro_mesas.findViewById(R.id.btn_remove);
                                    Button btn_detalle=registro_mesas.findViewById(R.id.btn_detalle);
                                    //eventos
                                    total.setText("-----");
                                    mesa.setText(mesas.getString("name"));
                                    bn_doc=mesas.getString("doc_activo");
                                    if(mesas.getString("doc_activo").equals("1")){
                                        total.setText(mesas.getString("total_doc")+"€");
                                    }

                                    //eventos--- cuando ya hay una comanda activa-------------------------------------------------------
                                    if (mesas.getString("doc_activo").equals("1")){
                                        registro_mesas.setBackgroundColor(Color.rgb(230,255,230));
                                        btn_remove.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    Intent intentajuste=new Intent(getApplicationContext(),ajuste.class);
                                                    intentajuste.putExtra("id_mesa",mesas.getString("id"));
                                                    intentajuste.putExtra("user_id",user_id);
                                                    intentajuste.putExtra("host",host);
                                                    startActivity(intentajuste);
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        });
                                        btn_detalle.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intentdetalle=new Intent(getApplicationContext(),detalle.class);
                                                try {
                                                    intentdetalle.putExtra("id_mesa",mesas.getString("id"));
                                                    intentdetalle.putExtra("user_id",user_id);
                                                    intentdetalle.putExtra("host",host);
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                startActivity(intentdetalle);
                                            }
                                        });
                                        int finalI1 = i;
                                        int finalY1 = y;
                                        btn_add.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    Intent intent=new Intent(getApplicationContext(),documento.class);
                                                    intent.putExtra("datos_productos",datos.getString("productos"));
                                                    intent.putExtra("categorias",datos.getString("categorias"));
                                                    intent.putExtra("nro_comensales","0");
                                                    intent.putExtra("mesa_name",areas.getJSONObject(finalI1).getJSONArray("mesas").getJSONObject(finalY1).getString("name"));
                                                    intent.putExtra("id_mesa",areas.getJSONObject(finalI1).getJSONArray("mesas").getJSONObject(finalY1).getString("id"));
                                                    intent.putExtra("id_area",areas.getJSONObject(finalI1).getString("id"));
                                                    intent.putExtra("area_name",areas.getJSONObject(finalI1).getString("name"));
                                                    intent.putExtra("negocio_id",negocio_id);
                                                    intent.putExtra("user_id",user_id);
                                                    intent.putExtra("bn_doc",bn_doc);
                                                    intent.putExtra("host",host);
                                                    startActivity(intent);
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        });

                                    }else{
                                        //cuando hay que abrir la mesa
                                        int finalY = y;
                                        int finalI = i;
                                        btn_add.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                AlertDialog.Builder builder=new AlertDialog.Builder(contesto);
                                                builder.setMessage("Seleccione el numero de comensales");
                                                View selector=getLayoutInflater().inflate(R.layout.layoutnrocomensales,null);
                                                builder.setView(selector);
                                                EditText nro_comensales_tx=(EditText)selector.findViewById(R.id.nro_comensales);

                                                builder.setPositiveButton("confirmar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int rr) {
                                                        try {
                                                            Intent intent=new Intent(getApplicationContext(),documento.class);
                                                            intent.putExtra("datos_productos",datos.getString("productos"));
                                                            intent.putExtra("categorias",datos.getString("categorias"));
                                                            intent.putExtra("nro_comensales",nro_comensales_tx.getText().toString());
                                                            intent.putExtra("mesa_name",areas.getJSONObject(finalI).getJSONArray("mesas").getJSONObject(finalY).getString("id"));
                                                            intent.putExtra("id_mesa",areas.getJSONObject(finalI).getJSONArray("mesas").getJSONObject(finalY).getString("id"));
                                                            intent.putExtra("id_area",areas.getJSONObject(finalI).getString("id"));
                                                            intent.putExtra("area_name",areas.getJSONObject(finalI).getString("name"));
                                                            intent.putExtra("negocio_id",negocio_id);
                                                            intent.putExtra("user_id",user_id);
                                                            intent.putExtra("bn_doc",bn_doc);
                                                            intent.putExtra("host",host);
                                                            startActivity(intent);
                                                        } catch (JSONException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                                });
                                                builder.create().show();



                                            }
                                        });

                                    }





                                    tabla.addView(registro_mesas);
                                }

                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(principal.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> parametros=new HashMap<String,String>();
                        parametros.put("id",negocio_id);
                        return parametros;
                    }
                };
                RequestQueue requestQueue= Volley.newRequestQueue(contesto);
                requestQueue.add(stringRequest);
                ejecutar();

            }
        }
    }


