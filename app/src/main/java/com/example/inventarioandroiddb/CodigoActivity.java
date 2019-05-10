package com.example.inventarioandroiddb;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventarioandroiddb.Clases.DataBaseHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodigoActivity extends AppCompatActivity {

    private int cantidad = 0;
    private String code = "";
    DataBaseHelper myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo);
        myDB = new DataBaseHelper(getApplicationContext());
        esconderKeyboard();
//-------------------Botones-----------------------------------
        Button btnCod =findViewById(R.id.btnTipoCodigo);
        Button btnRegresar = findViewById(R.id.btnRegresar);
        Button btnLista = findViewById(R.id.btnLista);
//-------------------Accion Botones----------------------------
        ActionBtnCod(btnCod);
        ActionBtnRegresar(btnRegresar);
        ActionBtnListar(btnLista);
//----------------------------------------------------------------------------
        TextView lblUsuario = findViewById(R.id.lblTextUsuario);
        String usuario = (String)getIntent().getSerializableExtra("usuario");
        lblUsuario.setText(usuario);

        TextView lblUbicacion = findViewById(R.id.lblTextUbicacion);
        String ubicacion = (String)getIntent().getSerializableExtra("ubicacion");
        lblUbicacion.setText(ubicacion);

        TextView lblCantidad = findViewById(R.id.lblTextCantidad);
        lblCantidad.setText(Integer.toString(myDB.CantidadTotalDeRegistros()));
        cantidad = myDB.CantidadTotalDeRegistros();

        TextView lblCodigo = findViewById(R.id.lblTextUltimoCodigo);
        String ultimocodigo = myDB.UltimoCodigoIngresado();
        if(ultimocodigo!="")
        {
            lblCodigo.setText(myDB.UltimoCodigoIngresado());
        }
        else
        {
            lblCodigo.setText("-");
        }

//-----------------------------------------------------------------
        EditText text = findViewById(R.id.txtCodigo);
        ActionKeyPressTextCodigo(text);

        EditText textCant = findViewById(R.id.txtCantidad);
        ActionKeyPressTextCantidad(textCant);
    }

    private void ActionBtnCod(final Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = boton.getText().toString();
                if(!texto.toUpperCase().equals("COD/COD"))
                {
                    boton.setText("COD/COD");
                    TextView view = findViewById(R.id.lblTituloCantidad);
                    EditText cant = findViewById(R.id.txtCantidad);
                    view.setVisibility(View.GONE);
                    cant.setVisibility(View.GONE);
                }
                else
                {
                    boton.setText("COD/CANT");
                    TextView view = findViewById(R.id.lblTituloCantidad);
                    EditText cant = findViewById(R.id.txtCantidad);
                    view.setVisibility(View.VISIBLE);
                    cant.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void ActionBtnListar(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                Cursor cur = myDB.Lista();
                if(cur.getCount()==0)
                {
                    ShowMensage("Error","No se han encontrado registros");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while(cur.moveToNext())
                {
                    buffer.append("ID :"+ cur.getString(0) + "\n");
                    buffer.append("USUARIO :"+ cur.getString(1) + "\n");
                    buffer.append("UBICACION :"+ cur.getString(2) + "\n");
                    buffer.append("CODIGO :"+ cur.getString(3) + "\n");
                    buffer.append("CANTIDAD :"+ cur.getInt(4) + "\n\n");
                }

                ShowMensage("Lista",buffer.toString());
*/

            try{
                startActivityForResult(new Intent(CodigoActivity.this, ListaActivity.class),1);

            }
            catch (Exception ex)
            {
                ShowMensage("Error",ex.getMessage().toString());
            }
            }
        });
    }

    private boolean ActionKeyPressTextCodigo(EditText text)
    {
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    EditText text = findViewById(R.id.txtCodigo);

                    if(!text.getText().toString().trim().equals(""))
                    {
                        Button btnCod =findViewById(R.id.btnTipoCodigo);
                        String tipos = btnCod.getText().toString();

                        TextView lblUsuario = findViewById(R.id.lblTextUsuario);
                        TextView lblUbicacion = findViewById(R.id.lblTextUbicacion);

                        if(!tipos.toUpperCase().equals("COD/COD"))
                        {
                            EditText cant = findViewById(R.id.txtCantidad);
                            cant.requestFocus();
                            return true;
                        }
                        else
                        {
                            COD_COD(lblUsuario.getText().toString(),lblUbicacion.getText().toString(),text.getText().toString());
                            text.setText("");
                            text.requestFocus();
                            return true;
                        }
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    //return CodigoActivity.super.onKeyDown(keyCode, keyevent);
                    return false;
                }
            }
        });
        return false;
    }

    private boolean ActionKeyPressTextCantidad(EditText text)
    {
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    EditText cant = findViewById(R.id.txtCantidad);

                    if(!cant.getText().toString().trim().equals(""))
                    {

                        TextView lblUsuario = findViewById(R.id.lblTextUsuario);
                        TextView lblUbicacion = findViewById(R.id.lblTextUbicacion);

                        EditText text = findViewById(R.id.txtCodigo);

                        COD_CANT(lblUsuario.getText().toString(),lblUbicacion.getText().toString(),text.getText().toString(),cant.getText().toString());
                        text.setText("");
                        cant.setText("");
                        text.requestFocus();
                        return true;
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    //return CodigoActivity.super.onKeyDown(keyCode, keyevent);
                    return false;
                }
            }
        });
        return false;
    }


    private void COD_CANT(String usuario, String ubicacion, String codigo, String cant)
    {

        cantidad += Integer.parseInt(cant);
        code = codigo;

        try
        {
            myDB.InsertarData(usuario,ubicacion,codigo,Integer.parseInt(cant));
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }


        TextView txtCodigo = findViewById(R.id.lblTextUltimoCodigo);
        txtCodigo.setText(code);

        TextView txtCantidad = findViewById(R.id.lblTextCantidad);
        txtCantidad.setText(Integer.toString(cantidad));
    }

    private void COD_COD(String usuario, String ubicacion, String codigo)
    {
        cantidad += 1;
        code = codigo;

        try
        {
            Boolean correcto = myDB.InsertarData(usuario,ubicacion,codigo,1);
            if(!correcto)
            {
                Toast.makeText(getApplicationContext(),myDB.get_mensaje(), Toast.LENGTH_SHORT).show();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        TextView txtCodigo = findViewById(R.id.lblTextUltimoCodigo);
        txtCodigo.setText(code);

        TextView txtCantidad = findViewById(R.id.lblTextCantidad);
        txtCantidad.setText(Integer.toString(cantidad));

    }

    private void ActionBtnRegresar(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String Padding(char relleno,int largo, String palabra)
    {
        String padded = new String(new char[largo - palabra.length()]).replace('\0', relleno) + palabra;
        return padded;
    }

    public void esconderKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void ShowMensage(String titulo, String mensage)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(titulo);
        builder.setMessage(mensage);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TextView lblCantidad = findViewById(R.id.lblTextCantidad);
        lblCantidad.setText(Integer.toString(myDB.CantidadTotalDeRegistros()));
        cantidad = myDB.CantidadTotalDeRegistros();

        TextView lblCodigo = findViewById(R.id.lblTextUltimoCodigo);
        String ultimocodigo = myDB.UltimoCodigoIngresado();
        if(ultimocodigo!="")
        {
            lblCodigo.setText(myDB.UltimoCodigoIngresado());
        }
        else
        {
            lblCodigo.setText("-");
        }
    }
}
