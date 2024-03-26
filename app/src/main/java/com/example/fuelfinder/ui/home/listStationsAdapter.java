package com.example.fuelfinder.ui.home;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fuelfinder.R;
import com.example.fuelfinder.Station;
import com.example.fuelfinder.MainActivity;

import java.util.ArrayList;

public class listStationsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Station> listStations;

    public listStationsAdapter(ArrayList<Station> listStations) {
        super();
        this.listStations = listStations;
    }

    public listStationsAdapter(Context context, ArrayList<Station> listStations) {
        super();
        this.context = context;
        this.listStations = listStations;
    }


    @Override
    public int getCount() {
        return listStations.size();
    }

    @Override
    public Object getItem(int position) {
        return listStations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConstraintLayout layoutItem;
        LayoutInflater mInflater = LayoutInflater.from(context);
        if (convertView == null) {
            layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.stations_layout, parent, false);

        } else {
            layoutItem = (ConstraintLayout) convertView;
        }
        TextView stationName = layoutItem.findViewById(R.id.stationNom);
        TextView stationAdresse = layoutItem.findViewById(R.id.stationAdresse);
        TextView stationB7 = layoutItem.findViewById(R.id.stationsPrixB7);
        stationName.setText(listStations.get(position).address);
        stationAdresse.setText(listStations.get(position).address);
        stationB7.setText(String.valueOf(listStations.get(position).priceGazole));


        return layoutItem;
    }
}