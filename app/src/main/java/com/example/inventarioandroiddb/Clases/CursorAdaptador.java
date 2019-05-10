package com.example.inventarioandroiddb.Clases;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventarioandroiddb.ListaActivity;
import com.example.inventarioandroiddb.LoginActivity;
import com.example.inventarioandroiddb.ModificarItemListaActivity;
import com.example.inventarioandroiddb.R;
import com.example.inventarioandroiddb.UbicacionActivity;

public class CursorAdaptador extends CursorAdapter {

    private Context contexto;
    private Cursor cursorf;
    public CursorAdaptador(Context context, Cursor c) {
        super(context, c, 0);
        this.contexto = context;
        cursorf = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_item_lista, parent,false);
    }

    @Override
    public void bindView(View view, Context context,Cursor cursor) {
        final View vw = view;

        TextView tvID = (TextView) view.findViewById(R.id.tvID);
        TextView tvUsuario = (TextView) view.findViewById(R.id.tvUsuario);
        TextView tvUbicacion = (TextView) view.findViewById(R.id.tvUbicacion);
        TextView tvCodigo = (TextView) view.findViewById(R.id.tvCodigo);
        TextView tvCantidad = (TextView) view.findViewById(R.id.tvCantidad);

        // Extract properties from cursor
        final String id = cursor.getString(0);
        final String usuario = cursor.getString(1);
        final String ubicacion = cursor.getString(2);
        final String codigo = cursor.getString(3);
        final int cantidad = cursor.getInt(4);
        // Populate fields with extracted properties
        tvID.setText("ID: "+id);
        tvUsuario.setText("Usuario: "+usuario);
        tvUbicacion.setText("Ubicacion: "+ubicacion);
        tvCodigo.setText("Codigo: "+codigo);
        tvCantidad.setText("Cantidad: "+String.valueOf(cantidad));

        //Button
        ImageView btnModificar = (ImageView) view.findViewById(R.id.btnModificarItem);
        ImageView btnEliminar = (ImageView) view.findViewById(R.id.btnEliminarItem);
        //Listener Button
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                final DataBaseHelper db = new DataBaseHelper(contexto);

                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

                builder.setTitle("Eliminacion");
                builder.setMessage("¿Esta seguro que desea eliminar el registro?");

                builder.setNeutralButton("Si", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        if(db.Borrar(id))
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                            builder.setCancelable(true);
                            builder.setTitle("Eliminacion");
                            builder.setMessage("Registro de ID " + id + " ha sido borrado");
                            builder.create();
                            builder.show();
                            swapCursor(db.Lista());
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                            builder.setCancelable(true);
                            builder.setTitle("Eliminacion");
                            builder.setMessage("Error: " + db.get_mensaje());
                            builder.show();
                        }
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                } catch (Exception ex)
                {
                    ShowMensage("Error",ex.getMessage().toString());
                }
            }

        });
        btnEliminar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }

                return false;
            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent intent=new Intent(contexto, ModificarItemListaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.putExtra("id_item",id);
                    contexto.startActivity(intent);

                }
                catch (Exception ex)
                {
                    ShowMensage("Error",ex.getMessage().toString());
                }
            }
        });

        btnModificar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000,PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }

                return false;
            }
        });
    }

    public void ShowMensage(String titulo, String mensage)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensage);
        builder.show();
    }
}
