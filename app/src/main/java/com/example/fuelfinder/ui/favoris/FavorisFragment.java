package com.example.fuelfinder.ui.favoris;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fuelfinder.R;
import com.example.fuelfinder.Station;
import com.example.fuelfinder.databinding.FragmentHomeBinding;
import com.example.fuelfinder.ui.StationDetails;
import com.example.fuelfinder.ui.home.listStationsAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavorisFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ArrayList<Station> favoriteStations;
    private listStationsAdapter adapter;
    private ListView listFavoritStation;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listFavoritStation = root.findViewById(R.id.listStation);

        favoriteStations = loadFavoriteStations();
        adapter = new listStationsAdapter(requireContext(), favoriteStations);

        listFavoritStation.setAdapter(adapter);

        listFavoritStation.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                favoriteStations.remove(position);
                saveFavoriteStations();
                adapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Station retirée des favoris", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        listFavoritStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentStationDetails = new Intent(requireContext(), StationDetails.class);
                intentStationDetails.putExtra("currentStation", favoriteStations.get(position));
                startActivity(intentStationDetails);
            }
        });

        return root;
    }

    private ArrayList<Station> loadFavoriteStations() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("favorite_stations", requireContext().MODE_PRIVATE);
        String favoriteStationsJson = sharedPreferences.getString("favorite_stations", null);

        if (favoriteStationsJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Station>>(){}.getType();
            return gson.fromJson(favoriteStationsJson, type);
        } else {
            return new ArrayList<>();
        }
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
        binding = null;
    }
}
