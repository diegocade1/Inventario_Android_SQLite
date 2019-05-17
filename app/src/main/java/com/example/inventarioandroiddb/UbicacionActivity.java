package com.example.inventarioandroiddb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UbicacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        Boolean validacion = (Boolean) getIntent().getSerializableExtra("validacion");
//-------------------Botones--------------------------------------------------------
        Button btnAceptar =findViewById(R.id.btnAceptarUbicacion);
        Button btnCancelar =findViewById(R.id.btnCancelarUbicacion);
//-------------------Accion Botones-------------------------------------------------
        ActionBtnAceptar(btnAceptar,validacion);
        ActionBtnCancelar(btnCancelar);
//----------------------------------------------------------------------------------
        //String usuario = getIntent().getSerializableExtra("usuario").toString();
        TextView label = findViewById(R.id.lblUser);
        String usuario = (String)getIntent().getSerializableExtra("usuario");
        label.setText(label.getText() + usuario);
//-------------------Edit Text----------------------------------
        EditText txtUbicacion = findViewById(R.id.txtUbicacion);
//------------------Acciones Text Edit------------------------------
        ActionKeyPressTextUbicacion(txtUbicacion);
    }

    private void ActionBtnAceptar(Button boton,final Boolean validacion)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText txt = findViewById(R.id.txtUbicacion);
                String ubicacion = txt.getText().toString();
                if(!ubicacion.trim().equals(""))
                {
                    if(!validacion)
                    {
                        String usuario = (String)getIntent().getSerializableExtra("usuario");
                        startActivity(new Intent(UbicacionActivity.this,CodigoActivity.class)
                                .putExtra("usuario",usuario)
                                .putExtra("ubicacion",ubicacion));
                        //esconderKeyboard();
                    }
                    else
                    {
                        String usuario = (String)getIntent().getSerializableExtra("usuario");
                        startActivity(new Intent(UbicacionActivity.this,CodigoConValidacionActivity.class)
                                .putExtra("usuario",usuario)
                                .putExtra("ubicacion",ubicacion));
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ingrese Ubicacion", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    public void esconderKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private boolean ActionKeyPressTextUbicacion(EditText text)
    {
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    EditText txt = findViewById(R.id.txtUbicacion);
                    String ubicacion = txt.getText().toString();
                    if(!ubicacion.trim().equals(""))
                    {
                        String usuario = (String)getIntent().getSerializableExtra("usuario");
                        startActivity(new Intent(UbicacionActivity.this,CodigoActivity.class)
                                .putExtra("usuario",usuario)
                                .putExtra("ubicacion",ubicacion)
                                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        esconderKeyboard();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Ingrese Ubicacion", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
        return false;
    }
}
