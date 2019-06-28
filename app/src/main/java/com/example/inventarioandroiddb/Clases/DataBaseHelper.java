package com.example.inventarioandroiddb.Clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private String _mensaje;
    public String get_mensaje()
    {
        return _mensaje;
    }

    public void set_mensaje(String _mensaje)
    {

        this._mensaje = _mensaje;
    }

    private String _descripcion;

    public String get_descripcion()
    {
        return _descripcion;
    }

    public void set_descripcion(String _descripcion)
    {
        this._descripcion = _descripcion;
    }

    public static final int BaseDatos_Version = 1;
    public static final String Nombre_BaseDatos = "Inventario.db";
    // Tabla Inventario
    public static final String Nombre_Tabla = "tbl_inventario";
    public static final String Columna_1 = "ID";
    public static final String Columna_2 = "USUARIO";
    public static final String Columna_3 = "UBICACION";
    public static final String Columna_4 = "CODIGO";
    public static final String Columna_5 = "CANTIDAD";

    //Tabla Maestro
    public static final String Nombre_Tabla_2 = "tbl_maestro";
    public static final String Columna_2_1 = "CODIGO";
    public static final String Columna_2_2 = "DESCRIPCION";

    public DataBaseHelper(Context context) {
        super(context, Nombre_BaseDatos, null, BaseDatos_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ Nombre_Tabla +
                "(" + Columna_1 + " integer primary key autoincrement not null," +
                Columna_2 +" TEXT not null," +
                Columna_3 + " TEXT not null," +
                Columna_4 + " TEXT not null," +
                Columna_5 + " integer not null)");
        db.execSQL("create table "+ Nombre_Tabla_2 +
                "(" + Columna_2_1 + " TEXT primary key not null," +
                Columna_2_2 + " TEXT not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Nombre_Tabla);
        db.execSQL("drop table if exists " + Nombre_Tabla_2);
        onCreate(db);
    }


    public boolean InsertarData(String usuario, String ubicacion, String codigo, int cantidad)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues content = new ContentValues();
            content.put(Columna_2,usuario);
            content.put(Columna_3,ubicacion);
            content.put(Columna_4,codigo);
            content.put(Columna_5,cantidad);
            long insert = db.insert(Nombre_Tabla,null,content);
            if(insert !=-1)
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (SQLException e)
        {
            e.fillInStackTrace();
            set_mensaje(e.getMessage());
            return false;
        }

    }

    public void Insertar10000Registros()
    {
        for(int index = 0;index<10000;index++)
        {
            InsertarData("1","1","1",1);
        }
    }

    public void InsertarDataMasivoMaestro(String [] archivo)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            SQLiteStatement stmt = db.compileStatement(
                    "INSERT INTO "+ Nombre_Tabla_2 +"("+Columna_2_1+","+Columna_2_2+") VALUES (?,?)"
            );

            db.beginTransaction();
            Object[] values = new Object[2];
            for(int index= 0; index < archivo.length;index++)
            {
                String [] arraylinea = archivo[index].split(",");

                stmt.bindString(1,arraylinea[0].trim());
                stmt.bindString(2,arraylinea[1].trim());
                stmt.executeInsert();
                stmt.clearBindings();
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }
        catch (SQLException e)
        {
            e.fillInStackTrace();
            set_mensaje(e.getMessage());
        }

    }

    public boolean InsertarDataMaestro(String codigo, String descripcion)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues content = new ContentValues();
            content.put(Columna_2_1,codigo);
            content.put(Columna_2_2,descripcion);

            long insert = db.insert(Nombre_Tabla_2,null,content);
            if(insert !=-1)
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (SQLException e)
        {
            e.fillInStackTrace();
            set_mensaje(e.getMessage());
            return false;
        }

    }

    public Cursor Lista()
    {
        SQLiteDatabase db = this.getReadableDatabase();

// Definir que columnas mostrar

        String[] projection = {
                "ID _id",
                Columna_2,
                Columna_3,
                Columna_4,
                Columna_5
        };


// Sort
        String sortOrden =
                Columna_1 + " DESC";
//Cursor
        Cursor cursor = db.query(
                Nombre_Tabla,           // Nombre tabla
                projection,             // Columnas a traer
                null,          // no clausula where
                null,       // no valor de where
                null,          // no agrupar las filas
                null,            // no filtrar por grupo de filas
                sortOrden               // sort
        );
/*
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(Columna_1)
            );
            itemIds.add(itemId);
        }
        cursor.close();
*/

        return cursor;
    }

    public Cursor ListaMaestro()
    {
        SQLiteDatabase db = this.getReadableDatabase();

// Definir que columnas mostrar

        String[] projection = {
                "CODIGO _id",
                Columna_2_2,
        };


// Sort
        String sortOrden =
                Columna_2_1 + " DESC";
//Cursor
        Cursor cursor = db.query(
                Nombre_Tabla_2,           // Nombre tabla
                projection,             // Columnas a traer
                null,          // no clausula where
                null,       // no valor de where
                null,          // no agrupar las filas
                null,            // no filtrar por grupo de filas
                sortOrden               // sort
        );
/*
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(Columna_1)
            );
            itemIds.add(itemId);
        }
        cursor.close();
*/

        return cursor;
    }

    public Cursor ListaBusquedaCodigo(String codigo)
    {
        SQLiteDatabase db = this.getReadableDatabase();

// Definir que columnas mostrar

        String[] projection = {
                Columna_1,
                Columna_2,
                Columna_3,
                Columna_4,
                Columna_5
        };

// Filtro Where
        String selection = Columna_4 + " = ?";
        String[] selectionArgs = { codigo };

// Sort
        String sortOrden =
                Columna_1 + " DESC";
//Cursor
        Cursor cursor = db.query(
                Nombre_Tabla,           // Nombre tabla
                projection,             // Columnas a traer
                selection,              // clausula where
                selectionArgs,          // valor de where
                null,          // no agrupar las filas
                null,            // no filtrar por grupo de filas
                sortOrden               // sort
        );

        return cursor;
    }

    public boolean Borrar(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        // Define 'where' part of query.
        String selection = Columna_1 + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id };
        // Issue SQL statement.
        int deletedRows = db.delete(Nombre_Tabla, selection, selectionArgs);

        if(deletedRows !=0)
        {
            return true;
        }
        else
        {
            set_mensaje("Error al borrar el registro de la base de datos");
            return false;
        }
    }

    public boolean Modificar(int id, int cantidad)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // Nuevo valor en la columna
        ContentValues values = new ContentValues();
        values.put(Columna_5,cantidad);

        // Fila que sera modificada
        String selection = Columna_1 + " LIKE ?";
        String[] selectionArgs = { Integer.toString(id)};

        int count = db.update(
                Nombre_Tabla,
                values,
                selection,
                selectionArgs);
        if(count!=0)
        {
            return true;
        }
        else
        {
            set_mensaje("Error al modificar registro.");
            return false;
        }
    }

    public void ResetTable()
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("drop table if exists " + Nombre_Tabla);
            db.execSQL("create table "+ Nombre_Tabla +
                    "(" + Columna_1 + " integer primary key autoincrement not null," +
                    Columna_2 +" TEXT not null," +
                    Columna_3 + " TEXT not null," +
                    Columna_4 + " TEXT not null," +
                    Columna_5 + " integer not null)");
        }
        catch (Exception ex)
        {
            set_mensaje(ex.getMessage());
        }
    }

    public void ResetMaestro()
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("drop table if exists " + Nombre_Tabla_2);
            db.execSQL("create table "+ Nombre_Tabla_2 +
                    "(" + Columna_2_1 + " TEXT primary key not null," +
                    Columna_2_2 + " TEXT not null)");
        }
        catch (Exception ex)
        {
            set_mensaje(ex.getMessage());
        }
    }

    public String UltimoCodigoIngresado()
    {
        SQLiteDatabase db = this.getReadableDatabase();

// Definir que columnas mostrar

        String[] projection = {
                Columna_1,
                Columna_4
        };


// Sort
        String sortOrden =
                Columna_1 + " DESC";
//Cursor
        Cursor cursor = db.query(
                Nombre_Tabla,           // Nombre tabla
                projection,             // Columnas a traer
                null,          // no clausula where
                null,       // no valor de where
                null,          // no agrupar las filas
                null,            // no filtrar por grupo de filas
                sortOrden               // sort
        );
        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            String resultado = cursor.getString(1);
            return resultado;
        }
        else
        {
            return "";
        }
    }

    public int CantidadTotalDeRegistros()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        int cantidad = 0;
        Cursor c = db.rawQuery("select sum("+Columna_5+") from "+Nombre_Tabla+";", null);
        if(c.moveToFirst())
        {
            cantidad = c.getInt(0);
        }
        return cantidad;
    }

    public String CodigoExiste(String codigo)
    {
        SQLiteDatabase db = this.getReadableDatabase();

// Definir que columnas mostrar

        String[] projection = {
                Columna_2_1,
                Columna_2_2
        };
// Filtro Where
        String selection = Columna_2_1 + " = ?";
        String[] selectionArgs = { codigo };
//Cursor
        Cursor cursor = db.query(
                Nombre_Tabla_2,           // Nombre tabla
                projection,             // Columnas a traer
                selection,          // no clausula where
                selectionArgs,       // no valor de where
                null,          // no agrupar las filas
                null,            // no filtrar por grupo de filas
                null               // sort
        );
        int cant = cursor.getCount();
        if(cant > 0)
        {
            cursor.moveToFirst();
/*            String code = cursor.getString(1);
            String descripcion = cursor.getString(2);*/
            return cursor.getString(1);
        }
        else
        {
            return "";
        }
    }
}
