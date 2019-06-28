package com.example.inventarioandroiddb;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.inventarioandroiddb.Clases.DataBaseHelper;
import com.example.inventarioandroiddb.Clases.ZipHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    Context context;
    DataBaseHelper myDB;
    ProgressDialog progressDialog;

    final int MIS_PERMISOS = 100;

    private Thread Thread_SendFile,Thread_ReceiveFile = null;

    private String IP;
    private int Port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        myDB = new DataBaseHelper(getApplicationContext());
        ValidarPermisos();
        CargarConfig();


        //myDB.Insertar10000Registros();
//-------------------Botones-----------------------------------
        Button btnInventario =findViewById(R.id.btnInventario);
        Button btnInventarioValidacion =findViewById(R.id.btnInventarioValidacion);
        Button btnSalir = findViewById(R.id.btnSalir);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        Button btnBorrarTodo = findViewById(R.id.btnBorrarDatos);
        Button btnRecibir = findViewById(R.id.btnRecibir);
        Button btnConfig = findViewById(R.id.btnConfig);
//-------------------Accion Botones----------------------------
        ActionBtnInventario(btnInventario);
        ActionBtnSalir(btnSalir);
        ActionBtnEnviar(btnEnviar);
        ActionBtnBorrarTodo(btnBorrarTodo);
        ActionBtnRecibir(btnRecibir);
        ActionBtnInventarioValidacion(btnInventarioValidacion);
        ActionBtnConfig(btnConfig);
    }

    private void CargarConfig()
    {
        String fileName = "config.txt";

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Socket_Adress", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Config");

        if (!root.exists())
        {
            root.mkdirs();
        }

        File archivo = new File(root, fileName);


        try
        {
            if(!archivo.exists())
            {
                //archivo.createNewFile();
                FileOutputStream out = new FileOutputStream(archivo, false);
                String data = "IP=192.168.1.135,\r\nPort=100";
                byte[] contents = data.getBytes();
                out.write(contents);
                out.flush();
                out.close();
            }

            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String line,line2="";

            while ((line = br.readLine()) != null) {
                line2+=line;
            }
            br.close();
            if(!line2.equals(""))
            {
                String[] array = line2.split(",");
                editor.putString("IP", array[0].split("=")[1]);
                editor.putInt("Port", TryParse(array[1].split("=")[1]));
                editor.commit();
            }
            else
            {
                Log.d("Advertencia","Archivo de configuracion en blanco, configurar IP y Port manualmente en Configuraciones");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean ValidarPermisos() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            return true;
        }

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(INTERNET)== PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(ACCESS_NETWORK_STATE)== PackageManager.PERMISSION_GRANTED))
        {
            return true;
        }

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE))
                || (shouldShowRequestPermissionRationale(INTERNET))|| (shouldShowRequestPermissionRationale(ACCESS_NETWORK_STATE)))
        {
            cargarDialogoRecomendacion();
        }
        else
        {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,INTERNET,ACCESS_NETWORK_STATE},MIS_PERMISOS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MIS_PERMISOS)
        {
            if(!(grantResults.length==4 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED && grantResults[3]==PackageManager.PERMISSION_GRANTED))
            {
                solicitarPermisosManual();
            }
        }
    }

    private void solicitarPermisosManual()
    {
        final CharSequence[] opciones = {"Si","No"};
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        alertDialog.setTitle("Desea configurar los permisos de forma manual");
        alertDialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(opciones[which].equals("Si"))
                {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(context,"Los permisos no fueron aceptados",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void cargarDialogoRecomendacion()
    {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setTitle("Permisos Desactivados");
        dialog.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la app.");
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,INTERNET,ACCESS_NETWORK_STATE},MIS_PERMISOS);
            }
        });

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

    private void ActionBtnConfig(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,ConfigActivity.class));
            }
        });
    }

    private void ActionBtnInventarioValidacion(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myDB.ListaMaestro().getCount()>0)
                {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class).putExtra("validacion",true));
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Debe cargar el archivo maestro primero.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void ActionBtnEnviar(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myDB.Lista().getCount()>0)
                {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    //progressDialog.setMax(1); // Progress Dialog Max Value
                    progressDialog.setMessage("Cargando..."); // Setting Message
                    progressDialog.setTitle("Maestro"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Horizontal
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                    progressDialog.getProgress();


                    SharedPreferences prefs = getSharedPreferences("Socket_Adress", MODE_PRIVATE);
                    IP = prefs.getString("IP", "169.1.10.10");
                    Port = prefs.getInt("Port", 0);


                    Cursor cursor = myDB.Lista();
                    String fileName = "Maestro" + ".txt";//like 2016_01_12.txt

                    try
                    {

                        File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Enviado");
                        //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                        if (!root.exists())
                        {
                            root.mkdirs();
                        }
                        File archivo = new File(root, fileName);
                        if(archivo.exists())
                        {
                            if(archivo.delete())
                            {
                                Log.d("Archivo eliminado","Correctamente");
                            }
                            else
                            {
                                Log.d("Archivo eliminado","No se pudo borrar");
                            }
                        }

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

                        Thread_SendFile = new Thread(new Thread_SendFile());
                        Thread_SendFile.start();


                        //myDB.ResetTable();
                        Toast.makeText(getApplicationContext(), "Archivo generado.", Toast.LENGTH_SHORT).show();
                    }
                    catch(IOException e)
                    {
                        ShowMensage("Error",e.getMessage().toString());
                        progressDialog.dismiss();
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

                progressDialog = new ProgressDialog(MainActivity.this);
                //progressDialog.setMax(1); // Progress Dialog Max Value
                progressDialog.setMessage("Cargando..."); // Setting Message
                progressDialog.setTitle("Maestro"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Horizontal
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                progressDialog.getProgress();

                SharedPreferences prefs = getSharedPreferences("Socket_Adress", MODE_PRIVATE);
                IP = prefs.getString("IP", "169.1.10.10");
                Port = prefs.getInt("Port", 0);

                Thread_ReceiveFile = new Thread(new Thread_ReceiveFile());
                Thread_ReceiveFile.start();

                if(myDB.ListaMaestro().getCount()>0)
                {
                    myDB.ResetMaestro();
                }

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

    private void showMessageThread(final String titulo,final String message) {
        ((MainActivity)context).runOnUiThread(new Runnable() {
            public void run() {

                new AlertDialog.Builder(context)
                        .setTitle(titulo)
                        .setMessage(message)
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private int TryParse(String string)
    {
        if(isParsable(string))
        {
            return Integer.valueOf(string);
        }
        else
        {
            return 0;
        }
    }

    boolean isParsable(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /*-----------------------------------------Threads---------------------------------------------------*/

    private Socket socket;

    class Thread_SendFile implements Runnable
    {
        @Override
        public void run() {
            try
            {
                socket = new Socket();
                SocketAddress direccion = new InetSocketAddress(IP, Port);
                socket.connect(direccion,5*1000);

                String fileName = "Maestro" + ".txt";//like 2016_01_12.txt
                String zipName = "Maestro" + ".zip";
                File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Enviado");

                if (!root.exists())
                {
                    root.mkdirs();
                }
                File archivo = new File(root, fileName);
                File archivo2 = new File(root, zipName);

                ZipHelper.Zip(new String[]{ Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Enviado"+File.separator+
                        "Maestro.txt"},Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Enviado"+File.separator+
                        "Maestro.zip");

                if(archivo2.exists())
                {
                    if(archivo.delete())
                    {
                        Log.d("Archivo eliminado","Correctamente");
                        if(!archivo2.renameTo(archivo))
                        {
                            showMessageThread("Error:","No se pudo completar la operacion, error en la compresion del archivo.(0x03)");
                            progressDialog.dismiss();
                            return;
                        }
                    }
                    else
                    {
                        Log.d("Archivo eliminado","No se pudo borrar");
                        showMessageThread("Error:","No se pudo completar la operacion, error en la compresion del archivo.(0x02)");
                        progressDialog.dismiss();
                        return;
                    }
                }
                else
                {
                    showMessageThread("Error:","No se pudo completar la operacion, error en la compresion del archivo.(0x01)");
                    progressDialog.dismiss();
                    return;
                }



                byte[] bytes = new byte[(int) archivo.length()];
                BufferedInputStream bis;

                bis = new BufferedInputStream(new FileInputStream(archivo));
                bis.read(bytes, 0, bytes.length);
                OutputStream os = socket.getOutputStream();
                os.write(bytes, 0, bytes.length);
                os.flush();
                os.close();
                socket.close();

                final String sentMsg = "Archivo enviado a: " + socket.getInetAddress();
                progressDialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, sentMsg, Toast.LENGTH_LONG).show();
                        Thread_SendFile.interrupt();

                    }
                });

            }
            catch (IOException e)
            {
                e.printStackTrace();
                showMessageThread("Error:",e.getMessage());
                progressDialog.dismiss();
            }
        }

    }

    class Thread_ReceiveFile implements Runnable
    {
        private String mensaje;
        @Override
        public void run() {

            try
            {
                int actual = 0;
                socket = new Socket();
                SocketAddress direccion = new InetSocketAddress(IP, Port);
                socket.connect(direccion,5*1000);
                InputStream is = socket.getInputStream();
                File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Recibido");
                if (!root.exists())
                {
                    root.mkdirs();
                }
                final File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Recibido","Maestro.txt");
                byte[] bytes = new byte[1024];
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int byteRead;

                while((byteRead = is.read(bytes,0,bytes.length)) != -1)
                {
                    bos.write(bytes,0,byteRead);
                }

                bos.flush();
                bos.close();
                socket.close();
                mensaje = "Archivo Recibido";

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                        Thread_ReceiveFile.interrupt();
                        //progressDialog.dismiss();
                    }
                });

                new Thread() {

                    @Override
                    public void run() {
                        try
                        {
                            String fileName = "Maestro" + ".txt";//like 2016_01_12.txt
                            String zipName = "Maestro" + ".zip";
                            File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Recibido");
                            //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                            if (!root.exists())
                            {
                                root.mkdirs();
                            }
                            File archivo = new File(root, fileName);
                            File archivo2 = new File(root, zipName);
                            if(!archivo.exists())
                            {
                                showMessageThread("Error:","No se encontro el archivo Maestro.txt");
                                progressDialog.dismiss();
                                interrupt();
                                return;
                                //Toast.makeText(getApplicationContext(), "No se encontro el archivo Maestro.txt", Toast.LENGTH_SHORT).show();
                            }

                            boolean correcto = archivo.renameTo(archivo2);
                            if(correcto)
                            {
                                ZipHelper.UnZip(Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Recibido"+File.separator, zipName,fileName);
                                if(archivo2.exists())
                                {
                                    if(archivo2.delete())
                                    {
                                        Log.d("Archivo eliminado","Correctamente");
                                    }
                                    else
                                    {
                                        Log.d("Archivo eliminado","No se pudo borrar");
                                    }
                                }
                            }
                            else
                            {
                                showMessageThread("Error:","No se pudo descomprimir el archivo");
                                progressDialog.dismiss();
                                return;
                            }


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
                            showMessageThread("Cargado","Se ha cargado el archivo maestro.");

                        }
                        catch (IOException | SQLException e)
                        {
                            showMessageThread("Error:",e.getMessage());
                            progressDialog.dismiss();
                            interrupt();
                            //Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }.start();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                showMessageThread("Error:",e.getMessage());
                progressDialog.dismiss();
            }
        }
    }
}
