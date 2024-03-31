package com.example.fuelfinder.ui.map;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fuelfinder.IApiStationService;
import com.example.fuelfinder.R;
import com.example.fuelfinder.Station;
import com.example.fuelfinder.apiItems;
import com.example.fuelfinder.databinding.FragmentHomeBinding;
import com.example.fuelfinder.databinding.FragmentMapBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapBinding binding;
    private Retrofit retrofit;
    private ArrayList<Station> listStations;
    private MapView map;
    private static final String BASE_URL = "https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/prix-des-carburants-j-1/";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listStations = new ArrayList<>();

        Station.getStations(-1, 0, new Station.OnStationsLoadedListener() {
            @Override
            public void onStationsLoaded(ArrayList<Station> stations) {
                listStations.addAll(stations);
                Log.d(TAG, "Nombre de stations chargées : " + listStations.size());
            }
        });

        map = root.findViewById(R.id.mapView);
        map.getMapAsync(this);
        map.onCreate(savedInstanceState);
        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        for(int i = 0; i < listStations.size(); i++) {
            if(listStations.get(i).withGeoPointObj()!=null){
                googleMap.addMarker(new MarkerOptions()
                        .position(listStations.get(i).withGeoPointObj())
                        .title(listStations.get(i).name));
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}