package com.example.inventarioandroiddb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.inventarioandroiddb.Clases.DataBaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    DataBaseHelper myDB;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new DataBaseHelper(getApplicationContext());
//-------------------Botones-----------------------------------
        Button btnInventario =findViewById(R.id.btnInventario);
        Button btnInventarioValidacion =findViewById(R.id.btnInventarioValidacion);
        Button btnSalir = findViewById(R.id.btnSalir);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        Button btnBorrarTodo = findViewById(R.id.btnBorrarDatos);
        Button btnRecibir = findViewById(R.id.btnRecibir);
//-------------------Accion Botones----------------------------
        ActionBtnInventario(btnInventario);
        ActionBtnSalir(btnSalir);
        ActionBtnEnviar(btnEnviar);
        ActionBtnBorrarTodo(btnBorrarTodo);
        ActionBtnRecibir(btnRecibir);
        ActionBtnInventarioValidacion(btnInventarioValidacion);
    }

    private void ActionBtnInventario(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,LoginActivity.class).putExtra("validacion",false));
            }
        });
    }

    private void ActionBtnInventarioValidacion(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,LoginActivity.class).putExtra("validacion",true));
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
                        //myDB.ResetTable();
                        Toast.makeText(getApplicationContext(), "Archivo generado.", Toast.LENGTH_SHORT).show();
                    }
                    catch(IOException e)
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

                if(myDB.ListaMaestro().getCount()>0)
                {
                    myDB.ResetMaestro();
                }

                    progressDialog = new ProgressDialog(MainActivity.this);
                    //progressDialog.setMax(1); // Progress Dialog Max Value
                    progressDialog.setMessage("Cargando..."); // Setting Message
                    progressDialog.setTitle("Maestro"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Horizontal
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                    progressDialog.getProgress();

                final Handler mHandler = new Handler();

                    final Thread mThread = new Thread() {

                        @Override
                        public void run() {
                            try
                            {
                                String fileName = "Maestro" + ".txt";//like 2016_01_12.txt
                                File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Inventario");
                                //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                                if (!root.exists())
                                {
                                    root.mkdirs();
                                }
                                File archivo = new File(root, fileName);
                                if(!archivo.exists())
                                {
                                    Toast.makeText(getApplicationContext(), "No se encontro el archivo Maestro.txt", Toast.LENGTH_SHORT).show();

                                }
                                String linea;

                                FileInputStream fIn = new FileInputStream(archivo);
                                int byteLength;
                                //find a good buffer size here.
                                byte[] buffer = new byte[1024 * 128];
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                while((byteLength = fIn.read(buffer)) != -1){
                                    byte[] copy = Arrays.copyOf(buffer, byteLength);
                                    out.write(copy, 0, copy.length);
                                }
                                String output = out.toString();
                                String []arrayArchivo = output.split("\r\n");
                                myDB.InsertarDataMasivoMaestro(arrayArchivo);
                                progressDialog.dismiss();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Se ha cargado el archivo maestro.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            catch (IOException | SQLException e)
                            {
                                Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                mThread.start();
            }
        });
    }

    private void ActionBtnBorrarTodo(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    if(myDB.Lista().getCount()>0)
                    {
                        myDB.ResetTable();
                        if(!isNullOrEmpty(myDB.get_mensaje()))
                        {
                            ShowMensage("Borrar",myDB.get_mensaje());
                        }
                        else
                        {
                            ShowMensage("Borrar","Datos borrados");
                        }

                    }
                    else
                    {
                        ShowMensage("Advertencia","No se han encontrado datos que borrar");
                    }

                }
                catch (SQLException ex)
                {
                    ShowMensage("Error",ex.getMessage());
                }
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

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
    }
}
