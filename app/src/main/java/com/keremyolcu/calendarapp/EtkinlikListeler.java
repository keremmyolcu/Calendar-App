package com.keremyolcu.calendarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EtkinlikListeler extends AppCompatActivity {
    ListView etkinlikListe;
    TextView etkad,etkzaman;
    EtkinlikAdapter myAdapter;
    ArrayList<Etkinlik> etkinlikler = new ArrayList<>();
    EtkinlikDB mydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etkinlik_listeler);
        setTitle("Etkinlikler");
        mydb = new EtkinlikDB(this);
        myAdapter = new EtkinlikAdapter(this,etkinlikler);
        etkinlikListe = findViewById(R.id.listViewId);
        etkad = findViewById(R.id.etkAdBaslik);
        etkzaman = findViewById(R.id.baslaZamanBaslik);
        etkinlikListe.setAdapter(myAdapter);
        etkinlikleriDuzenle();

        etkinlikListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int gonderPendid = etkinlikler.get(position).getPendingId();
                String gonderAd = etkinlikler.get(position).getAd();
                String gonderDetay = etkinlikler.get(position).getDetay();
                String gonderBasla = etkinlikler.get(position).getBaslangic();
                String gonderBit = etkinlikler.get(position).getBitis();
                String gonderHatir = etkinlikler.get(position).getBurdaHatirlat();
                String gonderYine = etkinlikler.get(position).getYinele();
                String gonderKonum = etkinlikler.get(position).getKonum();

                Intent gonder = new Intent(EtkinlikListeler.this,EtkinlikDetay.class);

                gonder.putExtra("pendid",gonderPendid);
                gonder.putExtra("ad",gonderAd);
                gonder.putExtra("detay",gonderDetay);
                gonder.putExtra("basla",gonderBasla);
                gonder.putExtra("bit",gonderBit);
                gonder.putExtra("hatir",gonderHatir);
                gonder.putExtra("yine",gonderYine);
                gonder.putExtra("konum",gonderKonum);

                startActivity(gonder);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });


    }



    public void etkinlikleriDuzenle(){
        Cursor cursor = mydb.getAllData();
        etkinlikler.clear();
        while (cursor.moveToNext()){

            int pendid = cursor.getInt(0);
            String ad = cursor.getString(1);
            String detay  = cursor.getString(2);
            String basla = cursor.getString(3);
            String bit = cursor.getString(4);
            String hatir = cursor.getString(5);
            String yine = cursor.getString(6);
            String konum = cursor.getString(7);



            Etkinlik ekle = new Etkinlik(ad, detay,basla,bit,hatir,yine,konum);
            ekle.setPendingId(pendid);

            etkinlikler.add(ekle);
        }

        myAdapter.notifyDataSetChanged();
        if (etkinlikler.size()==0){

            Toast.makeText(this, "Etkinlik bulunamadi...", Toast.LENGTH_SHORT).show();
        }
        Log.d("eleman ekleme","elemanlar guncellendi ve gosteriliyor");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_etkinlik,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.etkinlikEkle){

            Intent i = new Intent(EtkinlikListeler.this,EtkinlikEkle.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();                 //finish demezsem onCreate bidaha çalışmayacak ve liste güncellenmeyecek
        }

        return super.onOptionsItemSelected(item);
    }



}
