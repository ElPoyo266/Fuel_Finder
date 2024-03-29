package com.example.fuelfinder.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fuelfinder.R;
import com.example.fuelfinder.Station;

import java.util.ArrayList;
import java.util.Objects;

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
        Station currentStation = listStations.get(position);
        LayoutInflater mInflater = LayoutInflater.from(context);
        if (convertView == null) {
            layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.stations_layout, parent, false);

        } else {
            layoutItem = (ConstraintLayout) convertView;
        }
        TextView stationName = layoutItem.findViewById(R.id.stationNom);
        TextView stationAdresse = layoutItem.findViewById(R.id.stationAdresse);
        TextView stationB7 = layoutItem.findViewById(R.id.stationsPrixB7);
        TextView stationSP98 = layoutItem.findViewById(R.id.stationsPrixSP98);
        TextView stationE10 = layoutItem.findViewById(R.id.stationsPrixE10);
        TextView stationE85 = layoutItem.findViewById(R.id.stationsPrixE85);
        TextView stationSP95 = layoutItem.findViewById(R.id.stationsPrixSP95);




        if (currentStation.name != null && !currentStation.name.equals("")) {
            stationName.setText(currentStation.name);
        } else {
            stationName.setText(currentStation.address);
        }
        stationAdresse.setText(currentStation.address+", "+currentStation.cp+", "+currentStation.comArmName);

        if (currentStation.priceGazole != null) {
            stationB7.setText(String.valueOf(currentStation.priceGazole * 1000)+"€");
        } else {
            stationB7.setText("Indisponible");
        }
        if (currentStation.priceE85 != null) {
            stationE85.setText(String.valueOf(currentStation.priceE85 * 1000)+"€");
        } else {
            stationE85.setText("Indisponible");
        }
        if (currentStation.priceSp98 != null) {
            stationSP98.setText(String.valueOf(currentStation.priceSp98 * 1000)+"€");
        } else {
            stationSP98.setText("Indisponible");
        }
        if (currentStation.priceE10 != null) {
            stationE10.setText(String.valueOf(currentStation.priceE10 * 1000)+"€");
        } else {
            stationE10.setText("Indisponible");
        }
        if (currentStation.priceSp95 != null) {
            stationSP95.setText(String.valueOf(currentStation.priceSp95 * 1000)+"€");
        } else {
            stationSP95.setText("Indisponible");
        }


        return layoutItem;
    }
}