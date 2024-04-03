package com.example.fuelfinder;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fuelfinder.ui.map.apiItems;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Station implements Serializable, ClusterItem
{
    @SerializedName("cp")
    @Expose
    public String cp;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("city")
    @Expose
    public String comArmName;
    @SerializedName("automate_24_24")
    @Expose
    public String automate2424;
    @SerializedName("timetable")
    @Expose
    public String timetable;
    @SerializedName("fuel")
    @Expose
    public List<String> fuel = new ArrayList<String>();
    @SerializedName("shortage")
    @Expose
    public List<String> shortage = new ArrayList<String>();
    @SerializedName("update")
    @Expose
    public String update;
    @SerializedName("price_gazole")
    @Expose
    public Double priceGazole;
    @SerializedName("price_sp95")
    @Expose
    public Double priceSp95;
    @SerializedName("price_sp98")
    @Expose
    public Double priceSp98;
    @SerializedName("price_gplc")
    @Expose
    public Double priceGplc;
    @SerializedName("price_e10")
    @Expose
    public Double priceE10;
    @SerializedName("price_e85")
    @Expose
    public Double priceE85;
    @SerializedName("services")
    @Expose
    public List<String> services = new ArrayList<String>();
    @SerializedName("brand")
    @Expose
    public Object brand;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("geo_point")
    @Expose
    public Object geoPoint;
    private final static long serialVersionUID = 2183534151118685669L;

    /**
     * No args constructor for use in serialization
     * @param priceE85
     * @param address
     * @param priceE10
     * @param priceGazole
     * @param automate2424
     * @param fuel
     * @param update
     * @param priceSp95
     * @param services
     * @param priceGplc
     * @param geoPoint
     * @param cp
     * @param priceSp98
     * @param timetable
     * @param comArmName
     * @param shortage
     * @param name
     * @param brand
     */
    public Station(String cp, String address, String comArmName, String automate2424, String timetable, List<String> fuel, List<String> shortage, String update, Double priceGazole, Double priceSp95, Double priceSp98, Double priceGplc, Double priceE10, Double priceE85, List<String> services, Object brand, String name, Object geoPoint) {
        super();
        this.cp = cp;
        this.address = address;
        this.comArmName = comArmName;
        this.automate2424 = automate2424;
        this.timetable = timetable;
        this.fuel = fuel;
        this.shortage = shortage;
        this.update = update;
        this.priceGazole = priceGazole;
        this.priceSp95 = priceSp95;
        this.priceSp98 = priceSp98;
        this.priceGplc = priceGplc;
        this.priceE10 = priceE10;
        this.priceE85 = priceE85;
        this.services = services;
        this.brand = brand;
        this.name = name;
        this.geoPoint = geoPoint;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return withGeoPointObj();
    }

    @Nullable
    @Override
    public String getTitle() {
        return name;
    }

    @Nullable
    @Override
    public String getSnippet() {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String prixGazole = (priceGazole != null) ? decimalFormat.format(priceGazole * 1000) : "Indisponible";
        String prixE85 = (priceE85 != null) ? decimalFormat.format(priceE85 * 1000) : "Indisponible";
        String prixSp98 = (priceSp98 != null) ? decimalFormat.format(priceSp98 * 1000) : "Indisponible";
        String prixSp95 = (priceSp95 != null) ? decimalFormat.format(priceSp95 * 1000) : "Indisponible";
        String prixE10 = (priceE10 != null) ? decimalFormat.format(priceE10 * 1000) : "Indisponible";
        return "Prix B7 : " + prixGazole + "\n" +
                "Prix E85 : " + prixE85 + "\n" +
                "Prix SP98 : " + prixSp98 + "\n" +
                "Prix SP95 : " + prixSp95 + "\n" +
                "Prix E10 : " + prixE10;
    }

    public interface OnStationsLoadedListener {
        void onStationsLoaded(ArrayList<Station> stations);
    }

    public static void getStations(int limit, int offset, OnStationsLoadedListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/prix_des_carburants_j_7/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IApiStationService stationService = retrofit.create(IApiStationService.class);

        Call<apiItems> resStation = stationService.getStation(limit, offset);
        resStation.enqueue(new Callback<apiItems>() {
            @Override
            public void onResponse(Call<apiItems> call, Response<apiItems> response) {
                if (response.isSuccessful() && response.body() != null && response.body().stations != null) {
                    ArrayList<Station> listStations = (ArrayList<Station>) response.body().stations;
                    Log.d(TAG, "Reussi : API Toutes les stations récupérées" + response);
                    listener.onStationsLoaded(listStations);
                } else {
                    Log.d(TAG, "Réponse API invalide ou liste de stations null");
                }
            }

            @Override
            public void onFailure(Call<apiItems> call, Throwable t) {
                Log.d(TAG, "onFailure: C'est cassé");
            }
        });
    }

    public static void getLocatedStations(String lon, String lat, int radius, int limit, int offset, OnStationsLoadedListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/prix_des_carburants_j_7/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IApiStationService stationService = retrofit.create(IApiStationService.class);


        Call<apiItems> resStation = stationService.getLocatedStation("within_distance(geo_point%2C%20geom'POINT("+lon+"%20"+lat+")'%2C"+radius+"km)%20" , 100, 0);

        resStation.enqueue(new Callback<apiItems>() {
            @Override
            public void onResponse(Call<apiItems> call, Response<apiItems> response) {
                if (response.isSuccessful() && response.body() != null && response.body().stations != null) {
                    ArrayList<Station> listStations = (ArrayList<Station>) response.body().stations;
                    Log.d(TAG, "Reussi : API Toutes les stations récupérées" + listStations.size());
                    listener.onStationsLoaded(listStations);
                } else {
                    Log.d(TAG, "Réponse API invalide ou liste de stations null" + response);
                }
            }

            @Override
            public void onFailure(Call<apiItems> call, Throwable t) {
                Log.d(TAG, "onFailure: C'est cassé");
            }
        });
    }


    public Station withCp(String cp) {
        this.cp = cp;
        return this;
    }

    public Station withAddress(String address) {
        this.address = address;
        return this;
    }

    public String withComArmName() {
        return comArmName;
    }

    public Station withAutomate2424(String automate2424) {
        this.automate2424 = automate2424;
        return this;
    }

    public Station withTimetable(String timetable) {
        this.timetable = timetable;
        return this;
    }

    public Station withFuel(List<String> fuel) {
        this.fuel = fuel;
        return this;
    }

    public Station withShortage(List<String> shortage) {
        this.shortage = shortage;
        return this;
    }

    public Station withUpdate(String update) {
        this.update = update;
        return this;
    }

    public Station withPriceGazole(Double priceGazole) {
        this.priceGazole = priceGazole;
        return this;
    }

    public Station withPriceSp95(Double priceSp95) {
        this.priceSp95 = priceSp95;
        return this;
    }

    public Station withPriceSp98(Double priceSp98) {
        this.priceSp98 = priceSp98;
        return this;
    }

    public Station withPriceGplc(Double priceGplc) {
        this.priceGplc = priceGplc;
        return this;
    }

    public Station withPriceE10(Double priceE10) {
        this.priceE10 = priceE10;
        return this;
    }

    public Station withPriceE85(Double priceE85) {
        this.priceE85 = priceE85;
        return this;
    }

    public Station withServices(List<String> services) {
        this.services = services;
        return this;
    }

    public Station withBrand(Object brand) {
        this.brand = brand;
        return this;
    }

    public Station withName(String name) {
        this.name = name;
        return this;
    }

    public Station withGeoPoint(Object geoPoint) {
        this.geoPoint = geoPoint;
        return this;
    }

    public LatLng withGeoPointObj(){
        if (geoPoint != null) {
            String[] parts = geoPoint.toString().split(", lat=");
            if (parts.length == 2) {
                String lonPart = parts[0];
                String latPart = parts[1].replace("}", ""); // Supprime le caractère '}' à la fin
                double longitude = Double.parseDouble(lonPart.substring(5));
                double latitude = Double.parseDouble(latPart);
                return new LatLng(latitude, longitude);
            }
        }
        return null;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Station.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("cp");
        sb.append('=');
        sb.append(((this.cp == null)?"<null>":this.cp));
        sb.append(',');
        sb.append("address");
        sb.append('=');
        sb.append(((this.address == null)?"<null>":this.address));
        sb.append(',');
        sb.append("comArmName");
        sb.append('=');
        sb.append(((this.comArmName == null)?"<null>":this.comArmName));
        sb.append(',');
        sb.append("automate2424");
        sb.append('=');
        sb.append(((this.automate2424 == null)?"<null>":this.automate2424));
        sb.append(',');
        sb.append("timetable");
        sb.append('=');
        sb.append(((this.timetable == null)?"<null>":this.timetable));
        sb.append(',');
        sb.append("fuel");
        sb.append('=');
        sb.append(((this.fuel == null)?"<null>":this.fuel));
        sb.append(',');
        sb.append("shortage");
        sb.append('=');
        sb.append(((this.shortage == null)?"<null>":this.shortage));
        sb.append(',');
        sb.append("update");
        sb.append('=');
        sb.append(((this.update == null)?"<null>":this.update));
        sb.append(',');
        sb.append("priceGazole");
        sb.append('=');
        sb.append(((this.priceGazole == null)?"<null>":this.priceGazole));
        sb.append(',');
        sb.append("priceSp95");
        sb.append('=');
        sb.append(((this.priceSp95 == null)?"<null>":this.priceSp95));
        sb.append(',');
        sb.append("priceSp98");
        sb.append('=');
        sb.append(((this.priceSp98 == null)?"<null>":this.priceSp98));
        sb.append(',');
        sb.append("priceGplc");
        sb.append('=');
        sb.append(((this.priceGplc == null)?"<null>":this.priceGplc));
        sb.append(',');
        sb.append("priceE10");
        sb.append('=');
        sb.append(((this.priceE10 == null)?"<null>":this.priceE10));
        sb.append(',');
        sb.append("priceE85");
        sb.append('=');
        sb.append(((this.priceE85 == null)?"<null>":this.priceE85));
        sb.append(',');
        sb.append("services");
        sb.append('=');
        sb.append(((this.services == null)?"<null>":this.services));
        sb.append(',');
        sb.append("brand");
        sb.append('=');
        sb.append(((this.brand == null)?"<null>":this.brand));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("geoPoint");
        sb.append('=');
        sb.append(((this.geoPoint == null)?"<null>":this.geoPoint));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}