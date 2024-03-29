package com.example.fuelfinder.ui.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
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
import com.example.fuelfinder.databinding.FragmentNotificationsBinding;
import com.example.fuelfinder.ui.notifications.NotificationsViewModel;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private ListView listStation;
    private listStationsAdapter adapter;
    private Retrofit retrofit;
    private static final String BASE_URL = "https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/prix-des-carburants-j-1/";

    private ArrayList<Station> listStations;
    private FragmentHomeBinding binding;
    private Boolean isLoading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Log.d(TAG, "Fragment initialisé");

        listStation = root.findViewById(R.id.listStation);
        //SearchView searchBar = root.findViewById(R.id.searchView);
        listStations = new ArrayList<>();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        get20Stations(0);

        adapter = new listStationsAdapter(requireContext(), listStations);
        listStation.setAdapter(adapter);

        listStation.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if (lastVisibleItem > totalItemCount-3) { //Paramètre -3 pour le comfort
                    if (!isLoading) { // verifie que l'on n'est pas deja entrain de charger des donnée
                        get20Stations(listStations.size());
                    }
                }
            }
        });
        return root;
    }

    // Méthode pour récupérer les stations
    private void get20Stations(int offset){
        isLoading=Boolean.TRUE;
        IApiStationService stationService = retrofit.create(IApiStationService.class);
        Call<apiItems> resStation = stationService.get20Station(20,offset);
        resStation.enqueue(new Callback<apiItems>() {
            @Override
            public void onResponse(Call<apiItems> call, Response<apiItems> response) {
                for(int i=0; i<20; i++){
                    listStations.add(response.body().stations.get(i));
                    adapter.notifyDataSetChanged();
                }
                Log.d(TAG, "Reussi : API 20 stations récupérés");
                isLoading=Boolean.FALSE;
            }

            @Override
            public void onFailure(Call<apiItems> call, Throwable t) {
                Log.d(TAG, "onFailure: C'est cassé");
                isLoading=Boolean.FALSE;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
