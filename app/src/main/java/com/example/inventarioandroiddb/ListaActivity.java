package com.example.inventarioandroiddb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.inventarioandroiddb.Clases.CursorAdaptador;
import com.example.inventarioandroiddb.Clases.DataBaseHelper;

import java.util.List;

import static java.util.Collections.EMPTY_LIST;

public class ListaActivity extends AppCompatActivity {

    DataBaseHelper myDB;
    CursorAdaptador todoAdapter;
    ListView lvItems;
    Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
        setContentView(R.layout.activity_lista);
        contexto = this;
        myDB = new DataBaseHelper(this);
        Cursor cur = myDB.Lista();
/*        TextView lblRegistrosCant = findViewById(R.id.lblCantidadRegistros);
        String texto = "Registros en total: " + Integer.toString(cur.getCount())+ "  ";
        lblRegistrosCant.setText(texto);*/

        ImageButton btnEliminar = (ImageButton) findViewById(R.id.btnBorrarSeleccionados);
        ActionBtnEliminarSelected(btnEliminar);

            lvItems = (ListView) findViewById(R.id.lvLista);

            todoAdapter = new CursorAdaptador(this, cur);

            lvItems.setAdapter(todoAdapter);
        }
        catch(Exception ex)
        {
            ShowMensage("Error",ex.getMessage());
        }

    }

    private void ActionBtnEliminarSelected(ImageButton boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    final int tamaño = todoAdapter.selectedItemsPositions.size();
                    //
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

                    builder.setTitle("Eliminacion");
                    builder.setMessage("¿Se eliminarán "+Integer.toString(tamaño)+" registro/s, desea continuar?");

                    builder.setNeutralButton("Si", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String mensaje= new String();
                            for(int index = 0; index<tamaño;index++)
                            {
                                int posicion_lista = todoAdapter.selectedItemsPositions.get(index);
                                Cursor cursor = (Cursor)todoAdapter.getItem(posicion_lista);
                                String id = cursor.getString(0);
                                if(!myDB.Borrar(id))
                                {
                                    mensaje += myDB.get_mensaje() + "\n";
                                }
                            }
                            if(!isNullOrEmpty(mensaje))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                                builder.setCancelable(true);
                                builder.setTitle("Eliminacion");
                                builder.setMessage("Error: " + mensaje);
                                builder.show();
                            }
                            else
                            {
                                todoAdapter.selectedItemsPositions.clear();
                            }

                            Cursor cur = myDB.Lista();
                            todoAdapter.swapCursor(cur);
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
                    //
                }
                catch (Exception ex)
                {
                    ShowMensage("Error",ex.getMessage().toString());
                }
            }
        });
    }

    public void ShowMensage(String titulo, String mensage)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensage);
        builder.show();
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
    }

}
