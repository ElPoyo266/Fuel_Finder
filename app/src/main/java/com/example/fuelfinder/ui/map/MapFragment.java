package com.example.fuelfinder.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fuelfinder.Station;
import com.example.fuelfinder.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {
    private FragmentMapBinding binding;
    private ArrayList<Station> listStations;
    private MapView mapView;
    private GoogleMap googleMap;
    private static final LatLng PARIS_COORDINATES = new LatLng(48.8566, 2.3522); // Coordonnées de Paris
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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARIS_COORDINATES, 10)); // Zoom sur Paris avec un niveau de zoom de 10
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraIdleListener(this);
    }

    @Override
    public void onCameraMove() {
        lastCameraPosition = googleMap.getCameraPosition().target;
    }

    @Override
    public void onCameraIdle() {
        loadStations();
    }

    private void loadStations() {
        LatLng center = googleMap.getCameraPosition().target;
        double lat = center.latitude;
        double lon = center.longitude;
        int radius = 10; // Rayon en kilomètres

        Station.getLocatedStations(String.valueOf(lon), String.valueOf(lat), radius, 20, 0, new Station.OnStationsLoadedListener() {
            @Override
            public void onStationsLoaded(ArrayList<Station> stations) {
                listStations.clear();
                listStations.addAll(stations);
                addMarkersToMap();
            }
        });
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
            VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();
            return visibleRegion.latLngBounds.contains(position);
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
