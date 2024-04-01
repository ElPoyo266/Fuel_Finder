package com.example.fuelfinder.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fuelfinder.R;
import com.example.fuelfinder.Station;
import com.example.fuelfinder.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARIS_COORDINATES, 11));
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraIdleListener(this);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null; // Utilise le contenu par défaut de l'info-bulle
            }

            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.custom_station_info, null);

                TextView titleTextView = view.findViewById(R.id.titleTextView);
                TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);

                titleTextView.setText(marker.getTitle());
                descriptionTextView.setText(marker.getSnippet());

                return view;
            }
        });

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
        int radius = 15;

        Station.getLocatedStations(String.valueOf(lon), String.valueOf(lat), radius, 100, 0, new Station.OnStationsLoadedListener() {
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
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");

                    // Vérifier si les prix ne sont pas null
                    String prixGazole = "Indisponible";
                    if (station.priceGazole != null) {
                        prixGazole = decimalFormat.format(station.priceGazole * 1000);
                    }
                    String prixE85 = "Indisponible";
                    if (station.priceE85 != null) {
                        prixE85 = decimalFormat.format(station.priceE85 * 1000);
                    }
                    String prixSp98 = "Indisponible";
                    if (station.priceSp98 != null) {
                        prixSp98 = decimalFormat.format(station.priceSp98 * 1000);
                    }
                    String prixSp95 = "Indisponible";
                    if (station.priceSp95 != null) {
                        prixSp95 = decimalFormat.format(station.priceSp95 * 1000);
                    }
                    String prixE10 = "Indisponible";
                    if (station.priceE10 != null) {
                        prixE10 = decimalFormat.format(station.priceE10 * 1000);
                    }

                    // Ajouter une description (snippet) avec les prix du carburant
                    String snippet = "Prix B7 : " + prixGazole + "\n" +
                            "Prix E85 : " + prixE85 + "\n" +
                            "Prix SP98 : " + prixSp98 + "\n" +
                            "Prix SP95 : " + prixSp95 + "\n" +
                            "Prix E10 : " + prixE10;

                    // Ajouter le marqueur avec le titre et la description
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(position).title(station.name).snippet(snippet));

                    // Afficher l'info-bulle pour chaque marqueur
                    marker.showInfoWindow();
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