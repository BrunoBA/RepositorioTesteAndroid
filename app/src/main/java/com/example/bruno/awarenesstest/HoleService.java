package com.example.bruno.awarenesstest;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by bruno on 19/06/17.
 */

public class HoleService {

    public static ArrayList<Hole> getHoles(Context context){

        Log.d("HoleService","Buscanco pelos buracos no banco...");
        HoleDB db = new HoleDB(context);

        try{

            ArrayList<Hole> buracos = db.findAll();
            Log.d("HoleService","Foram encontrados um total de "+ buracos.size()+ " do banco de dados");

            return buracos;

        }finally {
            db.close();
        }
    }

    public static void markHoles(Context context, Hole hole){

        HoleDB db = new HoleDB(context);

        try{

            db.save(hole);
            Log.d("HoleService","Salvando novo ponto");

        }finally {

            db.close();

        }


    }

}
