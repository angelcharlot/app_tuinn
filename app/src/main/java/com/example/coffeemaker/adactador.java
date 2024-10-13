package com.example.coffeemaker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class adactador extends BaseAdapter {

    public Context context;
    private int layout;
    public JSONArray producto;
    public TableLayout detalle;
    View detalle_comanda_adac;




    public adactador(Context context, JSONArray producto, int layout,TableLayout detalle){
        this.context= (Context) context;
        this.layout=layout;
        this.producto=producto;
        this.detalle=detalle;

    }


    @Override
    public int getCount() {
        return this.producto.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View V;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        V = layoutInflater.inflate(R.layout.producto,null);

        try {
            String name= producto.getJSONObject(i).getString("name");
            String id=producto.getJSONObject(i).getString("id");
            String id_impresora=producto.getJSONObject(i).getString("impresora_id");
            String baseUrl = "http://185.141.222.250:8000/";  // Define la base de la URL
            String imageUrl = baseUrl + producto.getJSONObject(i).getString("img");  // Concatenar base de la URL con img
            JSONArray array_presentaciones=new JSONArray(producto.getJSONObject(i).getString("presentaciones"));

            TextView tx1 = V.findViewById(R.id.producto_id);
            TextView tx2 = V.findViewById(R.id.producto_name);
            tx1.setText(name);
            tx2.setText(id);
            // Configurar la ImageView
            ImageView productImage = V.findViewById(R.id.imageP);
            Picasso.get()
                    .load(imageUrl) // URL de la imagen
                    .placeholder(R.drawable.birra_logo) // Imagen de placeholder mientras carga
                    .error(R.drawable.birra_logo) // Imagen de error si falla la carga
                    .into(productImage); // Cargar la imagen en la ImageView



            V.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (array_presentaciones.length()==1) {
                                int bn = 0;
                                for (int i = 0; i < detalle.getChildCount(); i++) {
                                    TableRow row = (TableRow) detalle.getChildAt(i);
                                    TextView nameTv = (TextView) row.getChildAt(1);
                                    TextView cantTv = (TextView) row.getChildAt(0);
                                    TextView tipotv =(TextView) row.getChildAt(2);
                                    String cantS = cantTv.getText().toString();
                                    int cant = Integer.parseInt(cantS);
                                    try {
                                        if (nameTv.getText().toString() == name && tipotv.getText().toString() == array_presentaciones.getJSONObject(0).getString("name")) {
                                            bn = 1;
                                            cant += 1;
                                            cantTv.setText(String.valueOf(cant));
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                if (bn == 0) {
                                    detalle_comanda_adac = LayoutInflater.from(context).inflate(R.layout.detalle_row, null, false);
                                    TextView tv1 = detalle_comanda_adac.findViewById(R.id.cantidad);
                                    TextView tv2 = detalle_comanda_adac.findViewById(R.id.name);
                                    TextView tv3 = detalle_comanda_adac.findViewById(R.id.tipo);
                                    TextView tv4 = detalle_comanda_adac.findViewById(R.id.id_p);
                                    TextView tv5 = detalle_comanda_adac.findViewById(R.id.precio_venta);
                                    TextView tv6 = detalle_comanda_adac.findViewById(R.id.impresora);
                                    Button plus= detalle_comanda_adac.findViewById(R.id.button3_plus);
                                    Button subtract=detalle_comanda_adac.findViewById(R.id.button4_subtract);
                                    tv1.setText("1");
                                    tv2.setText(name);
                                    tv4.setText(id);
                                    tv6.setText(id_impresora);
                                    plus.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            TableRow row_sumar=(TableRow) view.getParent();
                                            TextView cantidad_cambiar=(TextView) row_sumar.getChildAt(0);
                                            String sumar = cantidad_cambiar.getText().toString();
                                            int nro_existente=  Integer.parseInt(sumar);
                                            nro_existente+=1;
                                            cantidad_cambiar.setText(String.valueOf(nro_existente));

                                        }
                                    });
                                    subtract.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            TableRow row_sumar=(TableRow) view.getParent();
                                            TextView cantidad_cambiar=(TextView) row_sumar.getChildAt(0);
                                            String sumar = cantidad_cambiar.getText().toString();
                                            int nro_existente=  Integer.parseInt(sumar);
                                            nro_existente-=1;
                                            if (nro_existente==0){
                                                detalle.removeView(row_sumar);
                                            }
                                            cantidad_cambiar.setText(String.valueOf(nro_existente));
                                        }
                                    });
                                    try {
                                        tv3.setText(array_presentaciones.getJSONObject(0).getString("name"));
                                        tv5.setText(array_presentaciones.getJSONObject(0).getString("precio_venta"));

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                    detalle.addView(detalle_comanda_adac);
                                }
                            }else{

                                String[] tipos;
                                tipos =new String[array_presentaciones.length()];
                                for (int x=0; x < array_presentaciones.length();x++ ){
                                    try {
                                        tipos[x]=array_presentaciones.getJSONObject(x).getString("name");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                                builder.setTitle("Elige una presentacion");
                                builder.setItems(tipos, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int r) {

                                        int bn = 0;
                                        for (int i = 0; i < detalle.getChildCount(); i++) {
                                            TableRow row = (TableRow) detalle.getChildAt(i);
                                            TextView nameTv = (TextView) row.getChildAt(1);
                                            TextView cantTv = (TextView) row.getChildAt(0);
                                            TextView tipotv =(TextView) row.getChildAt(2);
                                            String cantS = cantTv.getText().toString();
                                            int cant = Integer.parseInt(cantS);
                                            try {
                                                if ((nameTv.getText().toString() == name) && (tipotv.getText().toString() == array_presentaciones.getJSONObject(r).getString("name"))) {
                                                    bn = 1;
                                                    cant += 1;
                                                    cantTv.setText(String.valueOf(cant));
                                                }
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        if (bn == 0) {
                                            detalle_comanda_adac = LayoutInflater.from(context).inflate(R.layout.detalle_row, null, false);
                                            TextView tv1 = detalle_comanda_adac.findViewById(R.id.cantidad);
                                            TextView tv2 = detalle_comanda_adac.findViewById(R.id.name);
                                            TextView tv3 = detalle_comanda_adac.findViewById(R.id.tipo);
                                            TextView tv4 = detalle_comanda_adac.findViewById(R.id.id_p);
                                            TextView tv5 = detalle_comanda_adac.findViewById(R.id.precio_venta);
                                            TextView tv6 = detalle_comanda_adac.findViewById(R.id.impresora);
                                            Button plus= detalle_comanda_adac.findViewById(R.id.button3_plus);
                                            Button subtract=detalle_comanda_adac.findViewById(R.id.button4_subtract);

                                            tv1.setText("1");
                                            tv2.setText(name);
                                            tv4.setText(id);
                                            tv6.setText(id_impresora);
                                            plus.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    TableRow row_sumar=(TableRow) view.getParent();
                                                    TextView cantidad_cambiar=(TextView) row_sumar.getChildAt(0);
                                                    String sumar = cantidad_cambiar.getText().toString();
                                                    int nro_existente=  Integer.parseInt(sumar);
                                                    nro_existente+=1;
                                                    cantidad_cambiar.setText(String.valueOf(nro_existente));
                                                }
                                            });
                                            subtract.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    TableRow row_sumar=(TableRow) view.getParent();
                                                    TextView cantidad_cambiar=(TextView) row_sumar.getChildAt(0);
                                                    String sumar = cantidad_cambiar.getText().toString();
                                                    int nro_existente=  Integer.parseInt(sumar);
                                                    nro_existente-=1;
                                                    if (nro_existente==0){
                                                        detalle.removeView(row_sumar);
                                                    }
                                                    cantidad_cambiar.setText(String.valueOf(nro_existente));
                                                }
                                            });
                                            try {
                                                tv3.setText(array_presentaciones.getJSONObject(r).getString("name"));
                                                tv5.setText(array_presentaciones.getJSONObject(r).getString("precio_venta"));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }

                                            detalle.addView(detalle_comanda_adac);
                                        }
                                    }
                                }).show();



                            }



                        }
                    }
            );


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        return V;
    }
}
