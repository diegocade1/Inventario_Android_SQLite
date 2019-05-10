package com.example.inventarioandroiddb;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.inventarioandroiddb.Clases.DataBaseHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        Button btnEnviar = findViewById(R.id.btnEnviar);
//-------------------Accion Botones----------------------------
        ActionBtnInventario(btnInventario);
        ActionBtnSalir(btnSalir);
        ActionBtnEnviar(btnEnviar);
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
                if(myDB.Lista().getCount()!=0)
                {
                    Cursor cursor = myDB.Lista();
                    String fileName = "Datos" + ".txt";//like 2016_01_12.txt


                    try
                    {
                        File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Inventario");
                        //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                        if (!root.exists())
                        {
                            root.mkdirs();
                        }
                        File archivo = new File(root, fileName);

                        FileWriter writer = new FileWriter( archivo,true);

                        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                        {
                            //String []lineTemp = arrayTemp.get(i).split(",");

                            String line = Padding(' ',2,cursor.getString(1)) + ","+ Padding(' ',15,cursor.getString(2)) +","+
                                    Padding(' ',25,cursor.getString(3))+","+Padding('0',10, Integer.toString(cursor.getInt(4)));
                            writer.append(line+"\r\n");
                        }
                        writer.flush();
                        writer.close();
                        cursor.close();
                        myDB.ResetTable();
                        Toast.makeText(getApplicationContext(), "Archivo generado.", Toast.LENGTH_SHORT).show();
                    }
                    catch(IOException | SQLException e)
                    {
                        ShowMensage("Error",e.getMessage().toString());
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Base de datos vacia", Toast.LENGTH_SHORT).show();
                }
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

    private String Padding(char relleno,int largo, String palabra)
    {
        String padded = new String(new char[largo - palabra.length()]).replace('\0', relleno) + palabra;
        return padded;
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
