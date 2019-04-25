package com.example.inventarioandroiddb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//-------------------Botones-----------------------------------
        Button btnAceptar =findViewById(R.id.btnAceptarUsuario);
        Button btnCancelar =findViewById(R.id.btnCancelarUsuario);
//-------------------Accion Botones----------------------------
        ActionBtnAceptar(btnAceptar);
        ActionBtnCancelar(btnCancelar);
//-------------------Edit Text----------------------------------
        EditText txtUsuario = findViewById(R.id.txtUsuario);
//------------------Acciones Text Edit------------------------------
        ActionKeyPressTextUsuario(txtUsuario);
    }

    private void ActionBtnAceptar(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText txt = findViewById(R.id.txtUsuario);
                String usuario = txt.getText().toString();
                if(!usuario.trim().equals(""))
                {
                    startActivity(new Intent(LoginActivity.this,UbicacionActivity.class)
                            .putExtra("usuario",usuario));
                    esconderKeyboard();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ingrese Usuario", Toast.LENGTH_SHORT).show();
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

    private boolean ActionKeyPressTextUsuario(EditText text)
    {
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    EditText txt = findViewById(R.id.txtUsuario);
                    String usuario = txt.getText().toString();
                    if(!usuario.trim().equals(""))
                    {
                        startActivity(new Intent(LoginActivity.this,UbicacionActivity.class)
                                .putExtra("usuario",usuario)
                                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));

                        esconderKeyboard();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Ingrese Usuario", Toast.LENGTH_SHORT).show();
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
