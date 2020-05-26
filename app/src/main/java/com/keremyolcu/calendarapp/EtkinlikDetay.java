package com.keremyolcu.calendarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class EtkinlikDetay extends AppCompatActivity implements AlertDialogSil.AlertDialogSilListener {

    TextView adtext,detaytext,baslatext,bittext,yineletext,hatirtext,konumtext;  //bold yazilar
    TextView userad,userdetay,userbasla,userbit,useryine,userhatir;   // user gosterilcek textviewlar
    String ad,detay,basla,bit,yine,hatir,konum;
    int pendid;
    Button konumButon,silButon,paylasButon,guncelButon;
    EtkinlikDB mydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etkinlik_detay);

        setTitle("Etkinlik Detaylari");

        mydb = new EtkinlikDB(this);

        ekraniDoldur();

        userBilgileriAl();


        konumButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(konum != null ){
                    if(isNetworkAvailable()) {
                        LatLng mapsegonder = konumToLatlng(EtkinlikDetay.this.konum);
                        Intent i = new Intent(EtkinlikDetay.this, MapsActivity.class);
                        i.putExtra("ekleorgoster", 2);
                        i.putExtra("gosterlatlong", mapsegonder);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(EtkinlikDetay.this, "Internet baglantiniz yok", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(EtkinlikDetay.this, "Bu etkinlige konum eklenmemis!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        silButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EtkinlikDetay.this.pendid != 0){

                    AlertDialogSil dialog = new AlertDialogSil();
                    dialog.show(getSupportFragmentManager(), "sil");

                }
            }
        });

        guncelButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EtkinlikDetay.this,EtkinlikEkle.class);
                intent.putExtra("guncelle",123);
                intent.putExtra("pendid",pendid);
                intent.putExtra("ad",ad);
                intent.putExtra("detay",detay);
                intent.putExtra("basla",basla);
                intent.putExtra("bit",bit);
                intent.putExtra("hatir",hatir);
                intent.putExtra("yine",yine);
                intent.putExtra("konum",konum);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });


        paylasButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(konum != null) {
                    String mesaj = "http://www.google.com/maps/place/" + konum + "\n konumunda ";
                    String mesajdevam = ad + " adinda bir etkinlik var!";
                    String anamesaj = mesaj + mesajdevam;
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, anamesaj);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, "Etkinligi paylas:");
                    startActivity(shareIntent);
                }
                else{
                    String mesajdevam = ad + " adinda bir etkinlik var!";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mesajdevam);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, "Etkinligi paylas:");
                    startActivity(shareIntent);

                }

            }
        });


    }



    public void etkinlikSil(int pendid){

        cancelAlarm(pendid);

        mydb.delete(pendid);

    }

    public void cancelAlarm(int pendid){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("ID", pendid);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,pendid,intent,0);

        if(alarmManager != null)
            alarmManager.cancel(pendingIntent);

        Log.d("alarmsil","Alarm silindi");
    }

    public void userBilgileriAl(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pendid = extras.getInt("pendid");
            ad = extras.getString("ad");
            detay = extras.getString("detay");
            basla = extras.getString("basla");
            bit = extras.getString("bit");
            yine = extras.getString("yine");
            hatir = extras.getString("hatir");
            konum = extras.getString("konum");

            userad.setText(ad);
            userdetay.setText(detay);
            userbasla.setText(basla);
            userbit.setText(bit);
            useryine.setText(yine);
            userhatir.setText(hatir);

        }
        else{
            Toast.makeText(this, "Veri alinamadi", Toast.LENGTH_SHORT).show();
        }
    }



    public void ekraniDoldur(){
        adtext = findViewById(R.id.addetaytext);
        detaytext = findViewById(R.id.detaydetaytext);
        baslatext = findViewById(R.id.baslangicdetaytext);
        bittext = findViewById(R.id.bitisdetaytext);
        yineletext = findViewById(R.id.yineledetaytext);
        hatirtext = findViewById(R.id.hatirdetaytext);
        konumtext= findViewById(R.id.konumdetaytext);
        //-------------------------------------------------
        userad = findViewById(R.id.useraddetay);
        userdetay = findViewById(R.id.userdetaydetay);
        userbasla = findViewById(R.id.userbaslangicdetay);
        userbit = findViewById(R.id.userbitisdetay);
        useryine = findViewById(R.id.useryineledetay);
        userhatir = findViewById(R.id.userhatirdetay);
        //userkonumu latlng olarak alcan gostercen haritada

        //--------------------------------------------------

        silButon = findViewById(R.id.silButon);
        paylasButon = findViewById(R.id.paylasButon);
        guncelButon = findViewById(R.id.guncelButon);
        konumButon = findViewById(R.id.konumGosterButon);
    }

    public LatLng konumToLatlng(String konum){
        String[] latandlong = konum.split(",");

        double latitude = Double.parseDouble(latandlong[0]);
        double longitude = Double.parseDouble(latandlong[1]);

        LatLng location = new LatLng(latitude, longitude);
        return location;
    }

    @Override
    public void onYesClicked() {        //sile bastiktan sonra tamam derse silsin
        if(EtkinlikDetay.this.pendid != 0) {
            etkinlikSil(pendid);
            Intent intentlisteler = new Intent(EtkinlikDetay.this, EtkinlikListeler.class);
            startActivity(intentlisteler);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
