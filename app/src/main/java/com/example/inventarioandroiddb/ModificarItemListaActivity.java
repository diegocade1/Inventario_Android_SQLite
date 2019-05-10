package com.example.inventarioandroiddb;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.inventarioandroiddb.Clases.CursorAdaptador;
import com.example.inventarioandroiddb.Clases.DataBaseHelper;

public class ModificarItemListaActivity extends AppCompatActivity {

    private DataBaseHelper db;
    private String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_item_lista);
        db = new DataBaseHelper(this);
// Get id
        String id = (String)getIntent().getSerializableExtra("id_item");
        ID = id;
        // Text view label de Modificar
        TextView lbl = (TextView) findViewById(R.id.lblModificarItem);
        String titulo = lbl.getText().toString();
        lbl.setText(titulo+id);

        Button btnAceptar =findViewById(R.id.btnAceptarModificar);
        Button btnCancelar =findViewById(R.id.btnCancelarModificar);
    //Accion Bottons
        ActionBtnCancelar(btnCancelar);
        ActionBtnAceptar(btnAceptar);
    }

    private void ActionBtnCancelar(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ActionBtnAceptar(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    EditText txt = (EditText) findViewById(R.id.txtModificarItem);
                    if(!db.Modificar(Integer.parseInt(ID),Integer.parseInt(txt.getText().toString())))
                    {
                        ShowMensage("Error",db.get_mensaje());
                    }
                    else
                    {
                        Intent intent=new Intent(getApplicationContext(), ListaActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
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
}
