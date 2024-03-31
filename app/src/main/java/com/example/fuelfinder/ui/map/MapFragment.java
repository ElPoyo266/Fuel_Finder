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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {
    private FragmentMapBinding binding;
    private ArrayList<Station> listStations;
    private MapView mapView;
    private GoogleMap googleMap;
    private static final LatLng LYON_COORDINATES = new LatLng(45.75, 4.85); // Coordonnées de Lyon
    private boolean loadingStations = false;
    private LatLng lastCameraPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = binding.mapView;
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        listStations = new ArrayList<>();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LYON_COORDINATES, 10)); // Zoom sur Lyon avec un niveau de zoom de 10
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraIdleListener(this);

        loadStations();
    }

    @Override
    public void onCameraMove() {
        lastCameraPosition = googleMap.getCameraPosition().target;
    }

    @Override
    public void onCameraIdle() {
        addMarkersToMap();

    }

    private void loadStations() {
        loadingStations = true;
        int offset = 0;
        int limit = 100;
        while (true) {
            Station.getStations(limit, offset, new Station.OnStationsLoadedListener() {
                @Override
                public void onStationsLoaded(ArrayList<Station> stations) {
                    listStations.addAll(stations);
                    addMarkersToMap();
                }
            });
            offset += limit;
            if (offset >= 4000) { // Limite maximale atteinte
                break;
            }
        }
    }

    private void addMarkersToMap() {
        if (googleMap != null) {
            googleMap.clear(); // Efface les marqueurs existants
            for (Station station : listStations) {
                LatLng position = station.withGeoPointObj();
                if (position != null && isStationInVisibleRegion(position)) {
                    googleMap.addMarker(new MarkerOptions().position(position).title(station.name));
                }
            }
        }
    }


    private boolean isStationInVisibleRegion(LatLng position) {
        if (googleMap != null) {
            // Obtient la projection de la carte
            Projection projection = googleMap.getProjection();
            if (projection != null) {
                // Obtient la région visible actuelle de la carte
                VisibleRegion visibleRegion = projection.getVisibleRegion();
                LatLngBounds bounds = visibleRegion.latLngBounds;
                // Vérifie si la position de la station est dans les limites de la région visible
                return bounds.contains(position);
            }
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

