package com.example.fuelfinder.ui.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fuelfinder.R;
import com.example.fuelfinder.Station;
import com.example.fuelfinder.databinding.FragmentHomeBinding;
import com.example.fuelfinder.ui.StationDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ListView listStation;
    private listStationsAdapter adapter;
    private ArrayList<Station> listStations;
    private FragmentHomeBinding binding;
    private boolean isLoading = false;
    private ArrayList<Station> favoriteStations;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Log.d("HomeFragment", "Fragment initialisé");

        listStation = root.findViewById(R.id.listStation);
        listStations = new ArrayList<>();
        favoriteStations = new ArrayList<>();

        loadStations();
        loadFavoriteStations();

        adapter = new listStationsAdapter(requireContext(), listStations);
        listStation.setAdapter(adapter);

        listStation.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if (lastVisibleItem > totalItemCount -5) { //Paramètre -5 pour le confort
                    if (!isLoading) {
                        isLoading = true;
                        Station.getStations(20, listStations.size(), new Station.OnStationsLoadedListener() {
                            @Override
                            public void onStationsLoaded(ArrayList<Station> stations) {
                                listStations.addAll(stations);
                                Log.d(TAG, "Nombre de stations chargées : " + listStations.size());
                                adapter.notifyDataSetChanged();
                                isLoading = false;
                            }
                        });
                    }
                }
            }
        });

        listStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentStationDetails = new Intent(requireContext(), StationDetails.class);
                intentStationDetails.putExtra("currentStation", listStations.get(position));
                startActivity(intentStationDetails);
            }
        });

        listStation.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!favoriteStations.contains(listStations.get(position))){
                    addStationToFavorites(listStations.get(position));
                }
                Toast.makeText(requireContext(), "Station ajoutée aux favoris", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return root;
    }

    private void loadStations() {
        Station.getStations(20, 0, new Station.OnStationsLoadedListener() {
            @Override
            public void onStationsLoaded(ArrayList<Station> stations) {
                listStations.addAll(stations);
                Log.d("HomeFragment", "Nombre de stations chargées : " + listStations.size());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadFavoriteStations() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("favorite_stations", requireContext().MODE_PRIVATE);
        String favoriteStationsJson = sharedPreferences.getString("favorite_stations", null);

        if (favoriteStationsJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Station>>(){}.getType();
            favoriteStations = gson.fromJson(favoriteStationsJson, type);
        } else {
            favoriteStations = new ArrayList<>();
        }
    }


    private void addStationToFavorites(Station station) {
        favoriteStations.add(station);
        saveFavoriteStations();
    }

    private void saveFavoriteStations() {
        Gson gson = new Gson();
        String favoriteStationsJson = gson.toJson(favoriteStations);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("favorite_stations", requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("favorite_stations", favoriteStationsJson);
        editor.apply();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveFavoriteStations();
        binding = null;
    }
}
