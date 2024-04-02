    package com.example.fuelfinder.ui.map;

    import android.Manifest;
    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.os.Bundle;
    import android.util.Log;
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
    import com.example.fuelfinder.ui.StationDetails;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.MapView;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.gms.maps.model.VisibleRegion;
    import com.google.maps.android.clustering.Cluster;
    import com.google.maps.android.clustering.ClusterManager;
    import com.google.maps.android.clustering.view.DefaultClusterRenderer;
    import com.google.maps.android.collections.MarkerManager;

    import java.util.ArrayList;

    public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, ClusterManager.OnClusterClickListener<Station> {
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
        private FragmentMapBinding binding;
        private ArrayList<Station> listStations;
        private MapView mapView;
        private GoogleMap googleMap;
        private static final LatLng PARIS_COORDINATES = new LatLng(48.8566, 2.3522);
        private boolean loadingStations = false;
        private LatLng lastCameraPosition;
        private ClusterManager<Station> clusterManager;
        private boolean markerClicked = false;


        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = FragmentMapBinding.inflate(inflater, container, false);
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d("MapFragment", "Test");

            mapView = binding.mapView;
            if (mapView != null) {
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(this);
            }
            listStations = new ArrayList<>();
        }

        @SuppressLint("PotentialBehaviorOverride")
        @Override
        public void onMapReady(@NonNull GoogleMap map) {
            Log.d("MapFragment", "Entrée dans le onMapReady");
            googleMap = map;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARIS_COORDINATES, 11));
            googleMap.setOnCameraMoveListener(this);
            googleMap.setOnCameraIdleListener(this);
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                googleMap.setMyLocationEnabled(true);
            }
            clusterManager = new ClusterManager<Station>(getActivity(),googleMap,new MarkerManager(googleMap){
                @Override
                public View getInfoContents(Marker marker) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_station_info, null);
                    Station station = (Station) marker.getTag();

                    TextView titleTextView = view.findViewById(R.id.titleTextView);
                    TextView snippetTextView = view.findViewById(R.id.descriptionTextView);

                    if (station != null) {
                        titleTextView.setText(station.getTitle());
                        snippetTextView.setText(station.getSnippet());
                    }
                    return view;
                }


                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intentStationDetails = new Intent(requireContext(), StationDetails.class);
                    intentStationDetails.putExtra("currentStation",(Station) marker.getTag());
                    startActivity(intentStationDetails);
                    super.onInfoWindowClick(marker);
                }

                @Override
                public boolean onMarkerClick(Marker marker) {
                    markerClicked = true;
                    return super.onMarkerClick(marker);
                }
            });
            clusterManager.setRenderer(new CustomClusterRenderer( getContext() , googleMap, clusterManager));
            clusterManager.setOnClusterClickListener(this);
            googleMap.setOnMarkerClickListener(clusterManager);


        }

        @Override
        public void onCameraMove() {
            lastCameraPosition = googleMap.getCameraPosition().target;
        }

        @Override
        public void onCameraIdle() {
            if (!markerClicked) {
                loadStations();
            }
            markerClicked = false;
        }

        private void loadStations() {
            LatLng center = googleMap.getCameraPosition().target;
            double lat = center.latitude;
            double lon = center.longitude;
            int radius = 30;
            Log.d("MapFragment", "loadStations: Latitude = " + lat + ", Longitude = " + lon);

            Station.getLocatedStations(String.valueOf(lon), String.valueOf(lat), radius, 100, 0, new Station.OnStationsLoadedListener() {
                @Override
                public void onStationsLoaded(ArrayList<Station> stations) {
                    Log.d("MapFragment", "loadStations: Stations loaded successfully. Count: " + stations.size());
                    listStations.clear();
                    listStations.addAll(stations);
                    addMarkersToMap();
                }
            });
        }

        private void addMarkersToMap() {
            clusterManager.clearItems();
            Log.d("MapFragment", "addMarkersToMap: Adding markers to map.");
            if (googleMap != null) {
                for (Station station : listStations) {
                    LatLng position = station.withGeoPointObj();
                    if (position != null && isStationInVisibleRegion(position)) {
                        clusterManager.addItem(station);
                    }
                }
                clusterManager.cluster();
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

        @Override
        public boolean onClusterClick(Cluster<Station> cluster) {
            return false;
        }

        public class CustomClusterRenderer extends DefaultClusterRenderer<Station> {
            private final Context mContext;

            public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<Station> clusterManager) {
                super(context, map, clusterManager);
                mContext = context;
            }

            @Override
            protected void onBeforeClusterItemRendered(Station item, MarkerOptions markerOptions) {
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_local_gas_station_24));
                markerOptions.title(item.getTitle());
                markerOptions.snippet(item.getSnippet());
                super.onBeforeClusterItemRendered(item, markerOptions);
            }

            @Override
            protected void onClusterItemRendered(Station clusterItem, Marker marker) {
                super.onClusterItemRendered(clusterItem, marker);
                marker.setTag(clusterItem);
            }

            @Override
            protected void onClusterRendered(Cluster<Station> cluster, Marker marker) {
                super.onClusterRendered(cluster, marker);
            }

            @Override
            protected boolean shouldRenderAsCluster(Cluster<Station> cluster) {
                return super.shouldRenderAsCluster(cluster);
            }
        }
    }