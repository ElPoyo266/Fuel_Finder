package com.example.fuelfinder.ui.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fuelfinder.R;
import com.example.fuelfinder.Station;
import com.example.fuelfinder.databinding.FragmentHomeBinding;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private ListView listStation;
    private listStationsAdapter adapter;
    private ArrayList<Station> listStations;
    private FragmentHomeBinding binding;
    private boolean isLoading = false; // Initialise isLoading à false

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Log.d(TAG, "Fragment initialisé");

        listStation = root.findViewById(R.id.listStation);
        listStations = new ArrayList<>();

        Station.getStations(20, 0, new Station.OnStationsLoadedListener() {
            @Override
            public void onStationsLoaded(ArrayList<Station> stations) {
                listStations.addAll(stations);
                Log.d(TAG, "Nombre de stations chargées : " + listStations.size());
                adapter.notifyDataSetChanged(); // Met à jour l'adapter avec les nouvelles données
            }
        });

        adapter = new listStationsAdapter(requireContext(), listStations);
        listStation.setAdapter(adapter);

        listStation.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if (lastVisibleItem > totalItemCount -5) { //Paramètre -3 pour le confort
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
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
