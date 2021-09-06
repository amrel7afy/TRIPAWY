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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripawy.HomeActivity;
import com.example.tripawy.R;
import com.example.tripawy.RoomDB;
import com.example.tripawy.Trip;
import com.example.tripawy.TripAdapter;
import com.example.tripawy.databinding.FragmentHomeBinding;

import org.w3c.dom.Text;

import java.util.List;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public RecyclerView recyclerViewHome;
    private TextView txtNoTrips;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewHome = root.findViewById(R.id.recyclerViewHome);
        txtNoTrips = root.findViewById(R.id.NoTripsHome);


        return root;

    }

    @Override
    public void onResume() {
        super.onResume();

        LiveData<List<Trip>> listLiveData = RoomDB.getTrips(getContext()).getAllUpcoming();
        listLiveData.observe((LifecycleOwner) getContext(), new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                if (listLiveData.getValue().size() == 0) {
                    txtNoTrips.setVisibility(View.VISIBLE);
                } else {
                    txtNoTrips.setVisibility(View.GONE);
                }
                TripAdapter cardAdapter = new TripAdapter(getContext(), listLiveData);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerViewHome.setLayoutManager(linearLayoutManager);
                recyclerViewHome.setAdapter(cardAdapter);


            }
        });

        //swipe to delete the trip from home
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //mHomeViewModel.delete(mTripAdapter.getTripAt(viewHolder.getAdapterPosition()));
                Executors.newSingleThreadExecutor().execute(() -> {
                    RoomDB.getTrips(getContext()).delete(listLiveData.getValue().get(viewHolder.getAdapterPosition()));
                });
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }


        }).attachToRecyclerView(recyclerViewHome);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HomeActivity.fragmentFlag = true;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}