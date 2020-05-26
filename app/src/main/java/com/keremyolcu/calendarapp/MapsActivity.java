package com.keremyolcu.calendarapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button mapButton;
    String latSonuc;
    String lngSonuc;
    String adresSonuc;
    LatLng sonucLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapButton = findViewById(R.id.mapButton);
}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle extras = getIntent().getExtras();
        int eog = extras.getInt("ekleorgoster");

        if(eog == 1){           //EtkinlikEkle'den konum secmek icin ulasildiysa
            LatLng basla = new LatLng(41.00624923426495,28.983610644936556);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(basla,10));
            haritadanSec();
            secilenAdresiAl();
        }

        else if(eog == 2){      //EtkinlikDetay'dan  etkinlik konumuna bakmak icin ulasildiysa
            mapButton.setVisibility(View.INVISIBLE);
            LatLng gosterilcekYer = extras.getParcelable("gosterlatlong");
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gosterilcekYer,15));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(gosterilcekYer);
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gosterilcekYer,13));
            mMap.addMarker(markerOptions);

        }



    }








    public void haritadanSec(){
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                mMap.clear();
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(addresses.get(0).getAddressLine(0))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                MapsActivity.this.sonucLatLng = latLng;
                MapsActivity.this.latSonuc = Double.toString(latLng.latitude);
                MapsActivity.this.lngSonuc = Double.toString(latLng.longitude);
                MapsActivity.this.adresSonuc = addresses.get(0).getAddressLine(0);


            }
        });
    }

    public void secilenAdresiAl(){
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MapsActivity.this.latSonuc != null && MapsActivity.this.lngSonuc != null && MapsActivity.this.adresSonuc != null){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("latSonuc",latSonuc);
                    returnIntent.putExtra("lngSonuc",lngSonuc);
                    returnIntent.putExtra("adresSonuc",adresSonuc);
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
                else{
                    Toast.makeText(MapsActivity.this, "Konum secilemedi", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
