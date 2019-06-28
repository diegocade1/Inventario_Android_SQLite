package com.example.inventarioandroiddb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ConfigActivity extends AppCompatActivity {

    private static String IP;
    private static int Port;
    private Context context;
    private EditText etIP,etPort;
    private Button btnAceptarConfig,btnCancelarConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        context = this;

        SharedPreferences prefs = getSharedPreferences("Socket_Adress", MODE_PRIVATE);
        IP = prefs.getString("IP", "169.1.10.10");
        Port = prefs.getInt("Port", 0);

        //Controles
        btnAceptarConfig = findViewById(R.id.btnAceptarConfig);
        btnCancelarConfig = findViewById(R.id.btnCancelarConfig);

        etIP = findViewById(R.id.txtIP);
        etIP.setText(this.IP);

        etPort = findViewById(R.id.txtPort);
        etPort.setText(Integer.toString(this.Port));

        //Acciones
        ActionBtnCancelar(btnCancelarConfig);
        ActionBtnAceptar(btnAceptarConfig);
    }
    private void ActionBtnAceptar(Button boton)
    {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "config.txt";

                SharedPreferences pref = getApplicationContext().getSharedPreferences("Socket_Adress", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Inventario"+File.separator+"Config");

                String ip = etIP.getText().toString();
                int port = TryParse(etPort.getText().toString());
                editor.putString("IP", ip);
                editor.putInt("Port", port);
                editor.commit();

                File archivo = new File(root, fileName);
                try
                {
                        FileOutputStream out = new FileOutputStream(archivo, false);
                        String data = "IP="+ip+",\r\n"+"Port="+Integer.toString(port);
                        byte[] contents = data.getBytes();
                        out.write(contents);
                        out.flush();
                        out.close();

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }


                Toast.makeText(context,"Configuracion guardada.",Toast.LENGTH_LONG).show();
                finish();
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
}
