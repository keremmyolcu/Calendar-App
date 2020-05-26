package com.keremyolcu.calendarapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class EtkinlikDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "etkinlikler.db";
    public static final String E_TABLE_NAME = "etkinlik_table";
    public static final String COL_PENDID = "pendingId";
    //public static final String COL_ID = "id";
    public static final String COL_ISIM = "ad";
    public static final String COL_DETAY = "detay";
    public static final String COL_BASLA = "basla";
    public static final String COL_BIT = "bitis";
    public static final String COL_HATIR = "burdahatirlat";
    public static final String COL_YINE = "yinele";
    public static final String COL_KONUM = "konum";



    private final String CREATE_ETKINLIKLER = "CREATE TABLE "+
            E_TABLE_NAME + " ( " +
            COL_PENDID+" INTEGER, "+
            COL_ISIM+" TEXT, "+
            COL_DETAY+" TEXT, "+
            COL_BASLA+" TEXT, "+
            COL_BIT+" TEXT, "+
            COL_HATIR+" TEXT, "+
            COL_YINE+" TEXT, "+
            COL_KONUM+" TEXT "+
            ")";




    public EtkinlikDB(Context mctxt){
        super(mctxt,DATABASE_NAME,null,2);
    }


    public EtkinlikDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ETKINLIKLER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+E_TABLE_NAME);
    }


    public void insert(ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();
        if(db != null){
            int pendid = cv.getAsInteger(COL_PENDID);
            String ad = cv.getAsString(COL_ISIM);
            String detay = cv.getAsString(COL_DETAY);
            String basla = cv.getAsString(COL_BASLA);
            String bit = cv.getAsString(COL_BIT);
            String hatir = cv.getAsString(COL_HATIR);
            String yine = cv.getAsString(COL_YINE);
            String konum = cv.getAsString(COL_KONUM);

            String sqlinsert = "INSERT INTO " + E_TABLE_NAME + " VALUES (" + pendid + ",'" +ad+"','"+
                    detay + "','" +
                    basla + "','" + bit + "','" + hatir + "','" + yine + "','" + konum + "')";

            Log.d("inserting", ad + " " +detay);
            db.execSQL(sqlinsert);
            Log.d("databasee","Database e atildi");
        }

        db.close();
    }

    public void delete(int pendId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sqldelete = "DELETE FROM " + E_TABLE_NAME + " WHERE " + COL_PENDID + "=" + pendId  ;


        //db.delete(E_TABLE_NAME,COL_PENDID+"="+pendId,null);

        Log.d("deleting",sqldelete);

        db.execSQL(sqldelete);
        db.close();
    }

    public void update(int pendingId,ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();

        String ad = cv.getAsString(COL_ISIM);
        String detay = cv.getAsString(COL_DETAY);
        String basla = cv.getAsString(COL_BASLA);
        String bit = cv.getAsString(COL_BIT);
        String hatir = cv.getAsString(COL_HATIR);
        String yine = cv.getAsString(COL_YINE);
        String konum = cv.getAsString(COL_KONUM);

        String sqlupdate = "UPDATE "+ E_TABLE_NAME + " SET "+
                COL_ISIM+"='"+ad+"', "+
                COL_DETAY+"='"+detay+"', "+
                COL_BASLA+"='"+basla+"', "+
                COL_BIT+"='"+bit+"', "+
                COL_HATIR+"='"+hatir+"', "+
                COL_YINE+"='"+yine+"', "+
                COL_KONUM+"='"+konum+"'"+
                " WHERE "+ COL_PENDID+"="+pendingId;

        Log.d("updating",sqlupdate);
        db.execSQL(sqlupdate);
        db.close();

    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+E_TABLE_NAME,null);
    }



}
