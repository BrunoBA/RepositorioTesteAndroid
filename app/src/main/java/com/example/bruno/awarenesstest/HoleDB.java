package com.example.bruno.awarenesstest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 19/06/17.
 */

public class HoleDB extends SQLiteOpenHelper {

    private static final String TAG = "Sql";
    public static final String NOME_BANCO = "holes";
    private static final int VERSAO_BANCO = 1;
    private static final long ERROR_INSERT = 0;


    public HoleDB(Context context) {
        super(context, NOME_BANCO,null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"Crindo a tabela de buracos...");

        db.execSQL("Create table if not exists hole (_id intever primary key autoincrement, lat float, long float);");
        Log.d(TAG,"Tabela criada com sucesso!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Não é nescessário implementar
    }

    public long save(Hole buraco){
        long id = buraco.id;

        SQLiteDatabase db = getWritableDatabase();

        try{

            ContentValues values = new ContentValues();
            values.put("lat",buraco.latLng.latitude);
            values.put("long",buraco.latLng.longitude);

            if(id == 0){

                //Inserir Buraco

                id = db.insert("hole","",values);
                return id;
            }

        }finally {
            db.close();
        }

        return ERROR_INSERT;
    }

    public ArrayList<Hole> findAll(){
        SQLiteDatabase db = getWritableDatabase();

        try{
            // TRÁS TODOS PARA SEREM CONSUMIDOS NA ACTIVITY PRINCIPAL

            Cursor holes = db.query("hole",null,null,null,null,null,null,null);

            return toList(holes);

        }finally {
            db.close();
        }
    }

    private ArrayList<Hole> toList(Cursor holes) {

        ArrayList<Hole> buracos = new ArrayList<Hole>();

        if(holes.moveToFirst()){

            do{

                Hole hole = new Hole();

                buracos.add(hole);
                hole.id = holes.getColumnIndex("_id");
                hole.latLng = new LatLng(holes.getColumnIndex("lat"),holes.getColumnIndex("long"));

            }while(holes.moveToNext());

        }

        return buracos;
    }

    public void exeSQL (String sql){
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.execSQL(sql);
        }finally {
            db.close();
        }
    }
}
