package com.example.inventarioandroiddb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.inventarioandroiddb.Clases.DataBaseHelper;

public class MainActivity extends AppCompatActivity {
    DataBaseHelper myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new DataBaseHelper(getApplicationContext());
//-------------------Botones-----------------------------------
        Button btnInventario =findViewById(R.id.btnInventario);
        Button btnSalir = findViewById(R.id.btnSalir);
//-------------------Accion Botones----------------------------
        ActionBtnInventario(btnInventario);
        ActionBtnSalir(btnSalir);
    }

    private void ActionBtnInventario(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }

    private void ActionBtnEnviar(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void ActionBtnRecibir(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void ActionBtnSalir(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
