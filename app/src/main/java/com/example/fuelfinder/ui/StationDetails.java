package com.example.fuelfinder.ui;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.fuelfinder.MainActivity;
import com.example.fuelfinder.R;
import com.example.fuelfinder.Station;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.util.Objects;

public class StationDetails extends MainActivity {
    private Station currentStation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_details);
        Intent intent = getIntent();
        currentStation = (Station) intent.getSerializableExtra("currentStation");
        updateVueData();
    }
    @SuppressLint("SetTextI18n")
    void updateVueData() {
        TextView nameText = (TextView) findViewById(R.id.stationDetailName);
        TextView adressText = (TextView) findViewById(R.id.stationDetailAdresse);
        TextView dabText = (TextView) findViewById(R.id.stationDetail24h);
        TextView brandText = (TextView) findViewById(R.id.stationDetailBrand);
        TextView stationB7 = findViewById(R.id.stationsPrixB7);
        TextView stationSP98 = findViewById(R.id.stationsPrixSP98);
        TextView stationE10 = findViewById(R.id.stationsPrixE10);
        TextView stationE85 = findViewById(R.id.stationsPrixE85);
        TextView stationSP95 = findViewById(R.id.stationsPrixSP95);

        nameText.setText(currentStation.name);
        adressText.setText(currentStation.address+ ", "+ currentStation.comArmName+ ", "+currentStation.cp);
        if(Objects.equals(currentStation.automate2424, "Oui")){
            dabText.setText("Cette station possède un automate 24/24");
        }else if(Objects.equals(currentStation.automate2424, "Non")){
            dabText.setText("");
        }else{
            dabText.setText("Cette station possède peut-être un automate 24/24");
        }

        brandText.setText((CharSequence) currentStation.brand);
        Log.d(TAG, "TimeTable : " + currentStation.timetable);

        Gson gson = new Gson();
        TimeTable timeTable = gson.fromJson(currentStation.timetable, TimeTable.class);
        TableLayout tableLayoutOpeningHours = findViewById(R.id.tableLayoutOpeningHours);

        fillOpeningHours(tableLayoutOpeningHours, timeTable);

        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        if (currentStation.priceGazole != null) {
            stationB7.setText(decimalFormat.format(currentStation.priceGazole * 1000)+"€");
        } else {
            stationB7.setText("Indisponible");
        }
        if (currentStation.priceE85 != null) {
            stationE85.setText(decimalFormat.format(currentStation.priceE85 * 1000)+"€");
        } else {
            stationE85.setText("Indisponible");
        }
        if (currentStation.priceSp98 != null) {
            stationSP98.setText(decimalFormat.format(currentStation.priceSp98 * 1000)+"€");
        } else {
            stationSP98.setText("Indisponible");
        }
        if (currentStation.priceE10 != null) {
            stationE10.setText(decimalFormat.format(currentStation.priceE10 * 1000)+"€");
        } else {
            stationE10.setText("Indisponible");
        }
        if (currentStation.priceSp95 != null) {
            stationSP95.setText(decimalFormat.format(currentStation.priceSp95 * 1000)+"€");
        } else {
            stationSP95.setText("Indisponible");
        }
    }


    private void fillOpeningHours(TableLayout tableLayout, TimeTable timeTable) {
        if (timeTable != null) {
            fillDay(tableLayout, R.id.tvMondayOpeningHours, timeTable.getMonday());
            fillDay(tableLayout, R.id.tvTuesdayOpeningHours, timeTable.getTuesday());
            fillDay(tableLayout, R.id.tvWednesdayOpeningHours, timeTable.getWednesday());
            fillDay(tableLayout, R.id.tvThursdayOpeningHours, timeTable.getThursday());
            fillDay(tableLayout, R.id.tvFridayOpeningHours, timeTable.getFriday());
            fillDay(tableLayout, R.id.tvSaturdayOpeningHours, timeTable.getSaturday());
            fillDay(tableLayout, R.id.tvSundayOpeningHours, timeTable.getSunday());
        } else {
            setDayText(tableLayout, R.id.tvMondayOpeningHours, "Non renseigné");
            setDayText(tableLayout, R.id.tvTuesdayOpeningHours, "Non renseigné");
            setDayText(tableLayout, R.id.tvWednesdayOpeningHours, "Non renseigné");
            setDayText(tableLayout, R.id.tvThursdayOpeningHours, "Non renseigné");
            setDayText(tableLayout, R.id.tvFridayOpeningHours, "Non renseigné");
            setDayText(tableLayout, R.id.tvSaturdayOpeningHours, "Non renseigné");
            setDayText(tableLayout, R.id.tvSundayOpeningHours, "Non renseigné");
        }
    }

    private void fillDay(TableLayout tableLayout, int textViewId, OpeningHours openingHours) {
        if (openingHours != null) {
            String hours = openingHours.getOpeningTime() + " - " + openingHours.getClosingTime();
            setDayText(tableLayout, textViewId, hours);
        } else {
            setDayText(tableLayout, textViewId, "Non renseigné");
        }
    }

    private void setDayText(TableLayout tableLayout, int textViewId, String text) {
        TextView textView = tableLayout.findViewById(textViewId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    public class OpeningHours {
        @SerializedName("ouvert")
        private int isOpen;

        @SerializedName("ouverture")
        private String openingTime;

        @SerializedName("fermeture")
        private String closingTime;

        public int getIsOpen() {
            return isOpen;
        }

        public String getOpeningTime() {
            return openingTime;
        }

        public String getClosingTime() {
            return closingTime;
        }
    }

    public class TimeTable {
        @SerializedName("Lundi")
        private OpeningHours monday;

        @SerializedName("Mardi")
        private OpeningHours tuesday;

        @SerializedName("Mercredi")
        private OpeningHours wednesday;

        @SerializedName("Jeudi")
        private OpeningHours thursday;

        @SerializedName("Vendredi")
        private OpeningHours friday;

        @SerializedName("Samedi")
        private OpeningHours saturday;

        @SerializedName("Dimanche")
        private OpeningHours sunday;

        public OpeningHours getMonday() {
            return monday;
        }

        public OpeningHours getTuesday() {
            return tuesday;
        }

        public OpeningHours getWednesday() {
            return wednesday;
        }

        public OpeningHours getThursday() {
            return thursday;
        }

        public OpeningHours getFriday() {
            return friday;
        }

        public OpeningHours getSaturday() {
            return saturday;
        }

        public OpeningHours getSunday() {
            return sunday;
        }
    }

}
