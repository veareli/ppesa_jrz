package com.daicon.pc.ppesa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_NONE;

public class MyDbHelper extends SQLiteOpenHelper {


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAR);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pedido15.db";
    public static final String TABLA_NOMBRES = "pedido";
    public static final String COLUMNA_ID = "_id";
    public static final String COLUMNA_PRODUCTO = "id_Producto";
    public static final String COLUMNA_CANT = "cantidad";
    public static final String COLUMNA_PRECIO = "precio";
    public static final String COLUMNA_FACTOR = "factor";
    public static final String COLUMNA_COSTO = "costo";
    public static final String COLUMNA_DESCRIPCION = "descripcion";
    public static final String COLUMNA_DESC = "descuento";





    private static final String SQL_CREAR = "CREATE TABLE "+TABLA_NOMBRES+" ("+COLUMNA_ID+" integer primary key autoincrement,"
    +COLUMNA_PRODUCTO+ " integer not null,"
            +COLUMNA_CANT+" integer not null,"
            +COLUMNA_PRECIO+" double not null,"
            +COLUMNA_FACTOR+" double not null,"
            +COLUMNA_COSTO+" double not null,"
            +COLUMNA_DESCRIPCION+" varchar(200) not null,"
            +COLUMNA_DESC+" double not null,"
            +"promocion int );";

    /*"CREATE TABLE "+DATABASE_NAME+" ("+COLUMNA_ID+" integer primary key autoincrement not null,
    "+COLUMNA_PRODUCTO+" integer not null,
    "+COLUMNA_CANT+" integer not null,
    "+COLUMNA_PRECIO+" double not null,
    "+COLUMNA_FACTOR+" double not null,
    "+COLUMNA_COSTO+" double not null);"
    */

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long insertar(int prod, int cant,String precio, double factor, double desc, String descrpcion) {
        //SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        //newValues.put(COLUMNA_ID , 0);
        newValues.put(COLUMNA_PRODUCTO , prod);
        newValues.put(COLUMNA_PRECIO, Double.valueOf( precio));
        newValues.put(COLUMNA_CANT, cant);
        newValues.put(COLUMNA_FACTOR, factor);
        newValues.put(COLUMNA_COSTO, factor*Double.valueOf( precio));
        newValues.put(COLUMNA_DESC, desc);
        newValues.put(COLUMNA_DESCRIPCION, descrpcion);


        //long resultado = db.insert(TABLA_NOMBRES, null, newValues);

        long resultado=db.insertWithOnConflict(TABLA_NOMBRES,null,newValues,CONFLICT_FAIL);


        db.close();

        return resultado;

    }

    public Cursor obtenerTodo(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMNA_ID};
        Cursor c;
        String[] columnas = new String[] {"_id","id_Producto", "sum(cantidad)", "precio","factor","descuento","descripcion"};
        c = db.query(false,
                TABLA_NOMBRES,
                columnas,
                null,
                null,
                "descripcion",
                null,null,null ); //rawQuery(" SELECT _id,descripcion, SUM(cantidad) cantidad,SUM(precio) precio FROM "+TABLA_NOMBRES+" GROUP BY id_Producto,descripcion;", null);
        return c;
    }

    public void obtener(int id){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMNA_ID};

        Cursor cursor =
                db.query(TABLA_NOMBRES,
                        projection,
                        "id = ?",
                        new String[] { String.valueOf(id) },
                        null,
                        null,
                        null,
                        null);


        if (cursor != null)
            cursor.moveToFirst();

        System.out.println("El nombre es " +  cursor.getString(1) );
        db.close();

    }

    public void actualizar (String nombre, int id){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre",nombre);

        int i = db.update(TABLA_NOMBRES,
                values,
                " id = ?",
                new String[] { String.valueOf( id ) });
        db.close();
    }

    public boolean eliminarPedido() {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(TABLA_NOMBRES,
                    null,
                    null);
            db.close();
            return true;

        }catch(Exception ex){
            return false;
        }
    }



}
