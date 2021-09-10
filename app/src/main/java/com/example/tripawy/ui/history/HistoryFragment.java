package com.example.tripawy.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripawy.HomeActivity;
import com.example.tripawy.R;
import com.example.tripawy.RoomDB;
import com.example.tripawy.Trip;
import com.example.tripawy.TripAdapter;
import com.example.tripawy.databinding.FragmentHistoryBinding;

import java.util.List;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private RecyclerView recyclerViewHistory;
    private TextView txtNoTrips;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        recyclerViewHistory = root.findViewById(R.id.recyclerViewHistory);
        txtNoTrips = root.findViewById(R.id.NoTripsHistory);


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // to get other trips
        LiveData<List<Trip>> otherListLiveData = RoomDB.getTrips(getContext()).getAllHistory();
        otherListLiveData.observe(this, new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                if (otherListLiveData.getValue().size() == 0) {
                    txtNoTrips.setVisibility(View.VISIBLE);
                } else {
                    txtNoTrips.setVisibility(View.GONE);
                }
                TripAdapter cardAdapter = new TripAdapter(getContext(), otherListLiveData);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerViewHistory.setLayoutManager(linearLayoutManager);
                recyclerViewHistory.setAdapter(cardAdapter);
            }
        });

        //swipe to delete the trip
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                Executors.newSingleThreadExecutor().execute(() -> {
                    RoomDB.getTrips(getContext()).delete(otherListLiveData.getValue().get(viewHolder.getAdapterPosition()));
                });
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }


        }).attachToRecyclerView(recyclerViewHistory);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}