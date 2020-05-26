package com.keremyolcu.calendarapp;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Etkinlik {
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy/HH:mm");
    private String ad;
    private String detay;
    private String burdaHatirlat;
    private String yinele;
    private String konum;
    private String baslangic;
    private String bitis;
    private int pendingId;


    public Etkinlik(String ad, String detay,String baslangic,String bitis,String burda,String yine, String konum) {
        this.ad = ad;
        this.detay = detay;
        this.burdaHatirlat = burda;
        this.konum = konum;
        this.baslangic = baslangic;
        this.bitis = bitis;
        this.yinele = yine;
    }



    public int getPendingId() {
        return pendingId;
    }

    public void setPendingId(int pendingId) {
        this.pendingId = pendingId;
    }




    public String getYinele() {
        return yinele;
    }

    public void setYinele(String yinele) {
        this.yinele = yinele;
    }

    public String getKonum() {
        return konum;
    }

    public void setKonum(String konum) {
        this.konum = konum;
    }

    public String getBaslangic() {
        return baslangic;
    }

    public void setBaslangic(String baslangic) {
        this.baslangic = baslangic;
    }

    public String getBitis() {
        return bitis;
    }

    public void setBitis(String bitis) {
        this.bitis = bitis;
    }

    public String getAd() {
        return ad;
    }

    public String getBurdaHatirlat() {
        return burdaHatirlat;
    }

    public void setBurdaHatirlat(String burdaHatirlat) {
        this.burdaHatirlat = burdaHatirlat;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getDetay() {
        return detay;
    }

    public void setDetay(String detay) {
        this.detay = detay;
    }


    public Date stringToDate(String strdate) throws ParseException {
        return sdf.parse(strdate);
    }



}
