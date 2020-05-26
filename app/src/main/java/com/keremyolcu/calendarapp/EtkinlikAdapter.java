package com.keremyolcu.calendarapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EtkinlikAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Etkinlik> etkinliks;
    LayoutInflater layoutInflater;

    public EtkinlikAdapter(Context ctx,ArrayList<Etkinlik> array){
        this.context = ctx;
        this.etkinliks = array;
    }


    @Override
    public int getCount() {
        return etkinliks.size();
    }

    @Override
    public Object getItem(int location) {
        return etkinliks.get(location);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    private class myViewHolder{
        TextView etkinlikAd,alarmZamani;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        myViewHolder mvh = new myViewHolder();
        if(convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.etkinlik_row,null);
            mvh.etkinlikAd = (TextView)convertView.findViewById(R.id.etkinlikAd);
            mvh.alarmZamani = (TextView)convertView.findViewById(R.id.alarmBaslaTarih);

            convertView.setTag(mvh);
        }
        else{
            Log.i("myViewHolder isnt null","adapter");
            mvh = (myViewHolder)convertView.getTag();
        }


        Etkinlik tmp = etkinliks.get(position);
        mvh.etkinlikAd.setText(tmp.getAd());
        mvh.alarmZamani.setText(tmp.getBaslangic());

        return convertView;
    }
}
