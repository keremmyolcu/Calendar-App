package com.keremyolcu.calendarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class EtkinlikAyarlar extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String varsayilanRing,varsayilanZaman,varsayilanSiklik;
    TextView varsayRing,varsayZaman,varsaySiklik,lightDark;
    Spinner ringSpin,zamanSpin,siklikSpin;
    Button kaydet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etkinlik_ayarlar);

        setTitle("Ayarlar");

        SharedPreferences sharedpreferences = getSharedPreferences("sharedpreferences", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();


        varsayRing = findViewById(R.id.textView3);
        varsaySiklik = findViewById(R.id.hatirSiklik);
        varsayZaman = findViewById(R.id.hatirZaman);
        lightDark = findViewById(R.id.lightdark);
        ringSpin = findViewById(R.id.ringSpinner);
        zamanSpin = findViewById(R.id.zamanSpinner);
        siklikSpin = findViewById(R.id.siklikSpinner);
        kaydet = findViewById(R.id.button2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.varsayilanVals,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        siklikSpin.setAdapter(adapter);
        siklikSpin.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.ringVals,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ringSpin.setAdapter(adapter2);
        ringSpin.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.hatirZamans,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zamanSpin.setAdapter(adapter3);
        zamanSpin.setOnItemSelectedListener(this);

        siklikSpin.setSelection(adapter.getPosition(sharedpreferences.getString("SIKLIK","ASLA")));
        ringSpin.setSelection(adapter2.getPosition(sharedpreferences.getString("RING","ALARM")));


        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                varsayilanRing =ringSpin.getSelectedItem().toString();
                varsayilanSiklik = siklikSpin.getSelectedItem().toString();
                varsayilanZaman = zamanSpin.getSelectedItem().toString();
                        editor.putString("RING",varsayilanRing);
                        editor.putString("SIKLIK",varsayilanSiklik);
                        editor.putString("ONCE",varsayilanZaman);
                        editor.apply();
                        finish();
            }
        });

    }






    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
