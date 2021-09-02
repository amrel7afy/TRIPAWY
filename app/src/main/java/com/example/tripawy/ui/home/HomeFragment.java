package com.example.tripawy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripawy.R;
import com.example.tripawy.RoomDB;
import com.example.tripawy.Trip;
import com.example.tripawy.TripAdapter;
import com.example.tripawy.databinding.FragmentHomeBinding;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private RecyclerView recyclerViewHome;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewHome = root.findViewById(R.id.recyclerViewHome);


       /* final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        // to get all upcoming trips
        LiveData<List<Trip>> listLiveData = RoomDB.getTrips(getContext()).getAllUpcoming();
        listLiveData.observe((LifecycleOwner) getContext(), new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                TripAdapter cardAdapter = new TripAdapter(getContext(), listLiveData);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerViewHome.setLayoutManager(linearLayoutManager);
                recyclerViewHome.setAdapter(cardAdapter);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}