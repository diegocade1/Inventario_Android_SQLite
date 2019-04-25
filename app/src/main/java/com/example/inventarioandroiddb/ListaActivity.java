package com.example.inventarioandroiddb;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.inventarioandroiddb.Clases.DataBaseHelper;

public class ListaActivity extends AppCompatActivity {

    DataBaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        myDB = new DataBaseHelper(getApplicationContext());
        Cursor cur = myDB.Lista();

        TableLayout tablelayout = new TableLayout(this);
        tablelayout.setVerticalScrollBarEnabled(true);
        TableRow tablerow;
        TextView textview, textview2, textview3, textview4 ,textview5;
        tablerow = new TableRow(this);

        textview = new TextView(this);
        textview.setText("ID");
        textview.setTextColor(Color.RED);
        textview.setTypeface(null, Typeface.BOLD);
        textview.setPadding(20, 20, 20, 20);
        tablerow.addView(textview);


        textview2 = new TextView(this);
        textview2.setText("USUARIO");
        textview2.setTextColor(Color.BLACK);
        textview2.setTypeface(null, Typeface.BOLD);
        textview2.setPadding(20, 20, 20, 20);
        tablerow.addView(textview2);


        textview3 = new TextView(this);
        textview3.setText("UBICACION");
        textview3.setTextColor(Color.BLACK);
        textview3.setTypeface(null, Typeface.BOLD);
        textview3.setPadding(20, 20, 20, 20);
        tablerow.addView(textview3);

        textview4 = new TextView(this);
        textview4.setText("CODIGO");
        textview4.setTextColor(Color.BLACK);
        textview4.setTypeface(null, Typeface.BOLD);
        textview4.setPadding(20, 20, 20, 20);
        tablerow.addView(textview4);

        textview5 = new TextView(this);
        textview5.setText("CANTIDAD");
        textview5.setTextColor(Color.BLACK);
        textview5.setTypeface(null, Typeface.BOLD);
        textview5.setPadding(20, 20, 20, 20);
        tablerow.addView(textview5);

        tablelayout.addView(tablerow);

        if(cur.getCount()==0)
        {
            ShowMensage("Error","No se han encontrado registros");
            return;
        }
        else
        {
            while(cur.moveToNext())
            {
                tablerow = new TableRow(this);

                textview = new TextView(this);
                textview.setText(cur.getString(0));
                textview.setTextColor(Color.RED);
                textview.setTypeface(null, Typeface.BOLD);
                textview.setPadding(20, 20, 20, 20);
                tablerow.addView(textview);

                textview2 = new TextView(this);
                textview2.setText(cur.getString(1));
                textview2.setTextColor(Color.BLACK);
                textview2.setTypeface(null, Typeface.BOLD);
                textview2.setPadding(20, 20, 20, 20);
                tablerow.addView(textview2);

                textview3 = new TextView(this);
                textview3.setText(cur.getString(2));
                textview3.setTextColor(Color.BLACK);
                textview3.setTypeface(null, Typeface.BOLD);
                textview3.setPadding(20, 20, 20, 20);
                tablerow.addView(textview3);

                textview4 = new TextView(this);
                textview4.setText(cur.getString(3));
                textview4.setTextColor(Color.BLACK);
                textview4.setTypeface(null, Typeface.BOLD);
                textview4.setPadding(20, 20, 20, 20);
                tablerow.addView(textview4);

                textview5 = new TextView(this);
                textview5.setText(cur.getString(4));
                textview5.setTextColor(Color.BLACK);
                textview5.setTypeface(null, Typeface.BOLD);
                textview5.setPadding(20, 20, 20, 20);
                tablerow.addView(textview5);

                tablelayout.addView(tablerow);
            }

        }

        setContentView(tablelayout);

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
