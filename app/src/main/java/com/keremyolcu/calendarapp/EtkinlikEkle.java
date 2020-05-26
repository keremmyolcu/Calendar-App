package com.keremyolcu.calendarapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class EtkinlikEkle extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Spinner spinner;
    Button baslaGun,baslaSaat,bitGun,bitSaat,hatirGun,hatirSaat,ekle,konumEkle,guncelleButon;
    TextView ad,detay,baslangic,bitis,konum,yinele;
    EditText adEdit,detayEdit;
    String konumEtkinlik;
    String baslaEtkGun,baslaEtkSaat,bitEtkGun,bitEtkSaat,hatirEtkGun,hatirEtkSaat;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy/HH:mm");
    SimpleDateFormat sdftarih = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfsaat = new SimpleDateFormat("HH:mm");
    int baslaBitHatir= -1;
    EtkinlikDB mydb;
    String guncellebas,guncellebit,guncellekonum,guncellehatirlat;
    int guncelIcınPendid;

    int MAPS_LATLONG_REQUEST =11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etkinlik_ekle);

        SharedPreferences sprefs = getSharedPreferences("sharedpreferences",MODE_PRIVATE);

        mydb = new EtkinlikDB(this);

        setTitle("Etkinlik Ekle");

        ekraniDuzenle();
        guncelleButon.setVisibility(View.INVISIBLE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.yineleVals,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        onClickleriKur();

        Intent guncelIntent = getIntent();
        if(guncelIntent.getExtras() != null && guncelIntent.getExtras().getInt("guncelle") == 123 ){
            Bundle extras = guncelIntent.getExtras();
            ekle.setVisibility(View.INVISIBLE);
            guncelleButon.setVisibility(View.VISIBLE);
            adEdit.setText(extras.getString("ad"));
            detayEdit.setText(extras.getString("detay"));
            konumEtkinlik = extras.getString("konum");
            guncelIcınPendid = extras.getInt("pendid");
            guncellebas = extras.getString("basla");
            guncellebit = extras.getString("bit");
            guncellehatirlat = extras.getString("hatir");
            guncellekonum = extras.getString("konum");

            spinner.setSelection(adapter.getPosition(extras.getString("yine")));
        }

        ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SonEkle();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void listelereGit(){
        Intent intentlisteler = new Intent(EtkinlikEkle.this,EtkinlikListeler.class);
        startActivity(intentlisteler);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }


    public void SonEkle() throws ParseException {
        if(eklemeyeUygunMu()) {

            String finalad = adEdit.getText().toString();
            String finaldetay = detayEdit.getText().toString();
            String finalbaslangic = baslaEtkGun + "/" + baslaEtkSaat;
            String finalbitis = bitEtkGun + "/" + bitEtkSaat;
            String finalhatirlat = hatirEtkGun + "/" + hatirEtkSaat;
            String finalyinele = spinner.getSelectedItem().toString();

            ContentValues cv = new ContentValues();
            cv.put("ad", finalad);
            cv.put("detay", finaldetay);
            cv.put("basla", finalbaslangic);
            cv.put("bitis", finalbitis);
            cv.put("burdahatirlat", finalhatirlat);
            cv.put("yinele", finalyinele);
            cv.put("konum", konumEtkinlik);
            Random rand = new Random();
            int randomId = rand.nextInt(10000000);   //pending id unique olmali
            cv.put("pendingId", randomId);

            if (alarmiVarMi()) { //alarmi varsa
                if (zamanUygunMu(finalhatirlat) && sdf.parse(finalbitis).after(sdf.parse(finalhatirlat)) && sdf.parse(finalbaslangic).before(sdf.parse(finalbitis))) {
                    if(finalyinele.compareTo("VARSAYILAN") == 0){       //varsayilan secildiysa ayarlardan varsayilanin ne oldugunu ogren
                        SharedPreferences sprefs = getSharedPreferences("sharedpreferences",MODE_PRIVATE);
                        String atilacak = sprefs.getString("SIKLIK","ASLA");
                        startAlarm(randomId,finalad,atilacak,finalhatirlat);
                        mydb.insert(cv);
                        listelereGit();
                    }
                    else{
                        startAlarm(randomId, finalad, finalyinele, finalhatirlat);
                        mydb.insert(cv);
                        listelereGit();
                    }
                }
                else{
                    Toast.makeText(this, "Zamanlar yanlis secildi,tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                }

            }
            else{       //alarmi yoksa
                if (sdf.parse(finalbaslangic).before(sdf.parse(finalbitis))) {
                    mydb.insert(cv);
                    listelereGit();
                }
                else{
                    Toast.makeText(this, "Hatali Baslangic-Bitis zamanlarini duzenleyiniz!", Toast.LENGTH_SHORT).show();
                }
            }


        }
        else {
            AlertDialogFrag alertDialogFrag = new AlertDialogFrag();
            alertDialogFrag.show(getSupportFragmentManager(), "ex");

        }
    }

    public void startAlarm(int pendid,String baslik,String yine, String zaman){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar realzaman = stringToCalendar(zaman);

        Intent i = new Intent(EtkinlikEkle.this, AlertReceiver.class);
        i.putExtra("ID", pendid);
        i.putExtra("BASLIK",baslik);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,pendid,i,0);

        if(yine.compareTo("ASLA") == 0){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, realzaman.getTimeInMillis(),pendingIntent);
            Toast.makeText(this, "Alarm kuruldu!", Toast.LENGTH_SHORT).show();
        }

        if(yine.compareTo("HER GUN") == 0){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,realzaman.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
            Toast.makeText(this, "Her gun ayni saatte tekrarlayacak alarm kuruldu!", Toast.LENGTH_SHORT).show();
        }

        if(yine.compareTo("HER HAFTA") == 0){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,realzaman.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7,pendingIntent);
            Toast.makeText(this, "Her hafta ayni saatte tekrarlayacak alarm kuruldu!", Toast.LENGTH_SHORT).show();
        }

        if(yine.compareTo("HER AY") == 0){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,realzaman.getTimeInMillis(),AlarmManager.INTERVAL_DAY*30,pendingIntent);
            Toast.makeText(this, "Her ay tekrarlayacak alarm kuruldu!", Toast.LENGTH_SHORT).show();
        }

        if(yine.compareTo("HER YIL") == 0){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,realzaman.getTimeInMillis(),AlarmManager.INTERVAL_DAY*365,pendingIntent);
            Toast.makeText(this, "Her yil tekrarlayacak alarm kuruldu!", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Date tarih = c.getTime();

        if(baslaBitHatir == 0){         //baslangic gunu icin tiklanmissa
            baslaEtkGun = this.dateToString(tarih);
            Toast.makeText(this, baslaEtkGun+" baslangic gunu olarak secildi!", Toast.LENGTH_SHORT).show();
            baslaBitHatir = -1;
        }
        else if(baslaBitHatir ==1){       //bitis gunu icin tiklanmissa
            bitEtkGun = this.dateToString(tarih);
            Toast.makeText(this, bitEtkGun+" bitis gunu olarak secildi!", Toast.LENGTH_SHORT).show();
            baslaBitHatir = -1;
        }
        else if(baslaBitHatir == 2){                              //hatirlatma gunu icin tiklanmissa
            hatirEtkGun = this.dateToString(tarih);
            Toast.makeText(this, hatirEtkGun+" hatirlatma gunu olarak secildi!", Toast.LENGTH_SHORT).show();
            baslaBitHatir = -1;
        }
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String hourString;
        if (hourOfDay < 10)
            hourString = "0" + hourOfDay;
        else
            hourString = "" +hourOfDay;

        String minuteString;
        if (minute < 10)
            minuteString = "0" + minute;
        else
            minuteString = "" +minute;

            String saat = hourString+":"+minuteString;

        if(baslaBitHatir == 0){         //baslangic saati icin tiklanmissa
            baslaEtkSaat = saat;
            Toast.makeText(this, baslaEtkSaat+" baslangic saati olarak secildi!", Toast.LENGTH_SHORT).show();
            baslaBitHatir = -1;
        }
        else if(baslaBitHatir ==1){       //bitis gunu icin tiklanmissa
            bitEtkSaat = saat;
            Toast.makeText(this, bitEtkSaat+" bitis saati olarak secildi!", Toast.LENGTH_SHORT).show();
            baslaBitHatir = -1;
        }
        else if(baslaBitHatir == 2){       //hatirlatma gunu icin tiklanmissa
            hatirEtkSaat = saat;
            Toast.makeText(this, hatirEtkSaat+" hatirlatma saati olarak secildi!", Toast.LENGTH_SHORT).show();
            baslaBitHatir = -1;
        }

    }


    public String dateToString(Date tarih){
        return sdftarih.format(tarih);
    }


    public Calendar stringToCalendar(String zaman){     //05-06-2020/16:33
        String[] stringtarihvesaat = zaman.split("/");
        String butuntarih = stringtarihvesaat[0];
        String butunsaat = stringtarihvesaat[1];

        String[] tarih = butuntarih.split("-");
        String gun = tarih[0];
        String ay = tarih[1];
        String yil = tarih[2];

        String[] saatler = butunsaat.split(":");
        String saat = saatler[0];
        String dakika = saatler[1];

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(yil));
        calendar.set(Calendar.MONTH, Integer.parseInt(ay)-1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(gun));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(saat));
        calendar.set(Calendar.MINUTE,Integer.parseInt(dakika));
        calendar.set(Calendar.SECOND,00);

        return calendar;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == MAPS_LATLONG_REQUEST){                                //MAPS'TEN KONUM SECMEK ICIN
            if(resultCode == RESULT_OK && data.getExtras() != null){
                    String gelenLat = data.getStringExtra("latSonuc");
                    String gelenLng = data.getStringExtra("lngSonuc");
                    String gelenAdres = data.getStringExtra("adresSonuc");
                    Toast.makeText(this, gelenAdres+" secildi", Toast.LENGTH_SHORT).show();
                    this.konumEtkinlik = gelenLat+","+gelenLng;

            }
            else{
                Toast.makeText(this, "Konum alinamadi", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {       //spinner override methodlari
        String text = parent.getItemAtPosition(position).toString();
        //Toast.makeText(this, text+" secildi!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void ekraniDuzenle(){
        spinner = findViewById(R.id.yineleSec);
        baslaGun = findViewById(R.id.baslaGunSec);
        baslaSaat = findViewById(R.id.baslaSaatSec);
        bitGun = findViewById(R.id.bitGunSec);
        bitSaat = findViewById(R.id.bitSaatSec);
        hatirGun = findViewById(R.id.hatirGunSec);
        hatirSaat = findViewById(R.id.hatirSaatSec);
        ad = findViewById(R.id.adtext);
        detay = findViewById(R.id.detayText);
        baslangic = findViewById(R.id.baslangicText);
        bitis = findViewById(R.id.bitisText);
        konum = findViewById(R.id.konumText);
        yinele = findViewById(R.id.yineleText);
        adEdit = findViewById(R.id.adedit);
        detayEdit = findViewById(R.id.detayEdit);
        ekle = findViewById(R.id.ekleButon);
        konumEkle = findViewById(R.id.konumSec);
        guncelleButon = findViewById(R.id.guncelleButon);
    }

    public void onClickleriKur(){

        baslaGun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(),"Date Picker");
                baslaBitHatir = 0;
            }
        });
        bitGun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(),"Date Picker");         //baslangic,bitis,hatirlat gunleri icin datepicker
                baslaBitHatir = 1;                                                      //butonlari
            }
        });

        hatirGun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(),"Date Picker");
                baslaBitHatir = 2;

            }
        });

        //----------------------------------------------------------------------------------------------------------------------

        baslaSaat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFrag();
                timePicker.show(getSupportFragmentManager(),"Time Picker");
                baslaBitHatir = 0;
            }
        });

        bitSaat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                           //baslangic,bitis,hatirlat saatleri icin
                DialogFragment timePicker = new TimePickerFrag();                   //timepicker butonlari
                timePicker.show(getSupportFragmentManager(),"Time Picker");
                baslaBitHatir = 1;
            }
        });

        hatirSaat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFrag();
                timePicker.show(getSupportFragmentManager(),"Time Picker");
                baslaBitHatir = 2;
            }
        });

        //--------------------------------------------------------------------------------------------------------


        konumEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    Intent i = new Intent(EtkinlikEkle.this, MapsActivity.class);
                    i.putExtra("ekleorgoster", 1);
                    startActivityForResult(i, MAPS_LATLONG_REQUEST);
                }
                else{
                    Toast.makeText(EtkinlikEkle.this, "Internet baglantiniz yok!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        guncelleButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    guncelle();
                    Intent i = new Intent(EtkinlikEkle.this,EtkinlikListeler.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public boolean eklemeyeUygunMu(){
        if(adEdit.getText().toString().matches("") || detayEdit.getText().toString().matches("") || baslaEtkGun
                == null || baslaEtkSaat == null
        || bitEtkGun == null || bitEtkSaat == null ){
            return false;
        }
        else{
            return true;
        }
    }



    public boolean alarmiVarMi(){
        if(hatirEtkGun != null && hatirEtkSaat != null){
            return true;
        }
        else{
            return false;
        }
    }



   public boolean zamanUygunMu(String zaman) throws ParseException {
       Date suan = Calendar.getInstance().getTime();
       Calendar tete = stringToCalendar(zaman);

       if( tete.getTimeInMillis() > suan.getTime()){
           return true;
       }
       else{
           return false;
       }
   }

   public void guncelle() throws ParseException {
       if(guncelEklemeyeUygunMu()){
               ContentValues cv = new ContentValues();
               String finalad = adEdit.getText().toString();
               String finaldetay = detayEdit.getText().toString();
               String finalyinele = spinner.getSelectedItem().toString();

               if(baslaEtkGun !=null && baslaEtkSaat != null){
               String guncellebas = baslaEtkGun+"/"+baslaEtkSaat;
                   cv.put("basla",guncellebas);}
               else{
                   cv.put("basla",guncellebas);
               }

               if(bitEtkGun != null && bitEtkSaat != null){
               String guncellebit = bitEtkGun+"/"+bitEtkSaat;
                   cv.put("bitis",guncellebit);
               }
               else{
                   cv.put("bitis",guncellebit);
               }
               if(hatirEtkGun != null && hatirEtkSaat != null){
               String guncellehatirlat = hatirEtkGun+"/"+hatirEtkSaat;
                   cv.put("burdahatirlat",guncellehatirlat);
               }
               else{
                   cv.put("burdahatirlat",guncellehatirlat);
               }

               if(konumEtkinlik != null){
                   cv.put("konum",konumEtkinlik);
               }
               else{
                   cv.put("konum",guncellekonum);
               }

               cv.put("ad",finalad);
               cv.put("detay",finaldetay);
               cv.put("yinele",finalyinele);
               cv.put("pendingId",guncelIcınPendid);

               if(guncelAlarmiVarMi()) {
                   if (zamanUygunMu(guncellehatirlat) && sdf.parse(guncellebit).after(sdf.parse(guncellehatirlat)) && sdf.parse(guncellebas).before(sdf.parse(guncellebit))) {
                       startAlarm(guncelIcınPendid, finalad, finalyinele, guncellehatirlat);
                   }
               }

               mydb.update(guncelIcınPendid,cv);

           Toast.makeText(this, "Etkinlik guncellendi!", Toast.LENGTH_SHORT).show();

       }
       else{
           AlertDialogFrag alertDialogFrag = new AlertDialogFrag();
           alertDialogFrag.show(getSupportFragmentManager(),"ex");
       }

   }

   public boolean guncelAlarmiVarMi(){
       if(guncellehatirlat != null){
           return true;
       }
       else{
           return false;
       }
   }

    public boolean guncelEklemeyeUygunMu(){
        if(adEdit.getText().toString().matches("") || detayEdit.getText().toString().matches("") || guncellebas == null
                || guncellebit == null ){
            Toast.makeText(this, adEdit.getText().toString() + " "+detayEdit.getText().toString()+" "+guncellebas+" "+guncellebit, Toast.LENGTH_LONG).show();
            return false;
        }
        else{
            return true;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
