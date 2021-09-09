package com.example.tripawy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.tripawy.methods.Methods;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripawy.databinding.ActivityHomeBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private RecyclerView recyclerViewHome;
    private RecyclerView recyclerViewHistory;

    private List<Trip> tripList;
    private boolean fragmentFlag = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.tripawy.databinding.ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.appBarHome.toolbar);

        recyclerViewHome = findViewById(R.id.recyclerViewHome);
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);

        binding.appBarHome.fab.setOnClickListener(view -> {
            if (Methods.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(HomeActivity.this, AddNewTripActivity.class);
                startActivity(intent);
            } else {
                Snackbar snackbar = Snackbar
                        .make(view, "Permission Required", Snackbar.LENGTH_INDEFINITE)
                        .setBackgroundTint(ContextCompat.getColor(HomeActivity.this, R.color.white))
                        .setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.sky))
                        .setActionTextColor(ContextCompat.getColor(HomeActivity.this, R.color.sky))
                        .setAction("FIX", view1 -> Methods.openDrawSettings(HomeActivity.this));
                snackbar.show();
            }

        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.btn_sync, R.id.btn_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            View hview = navigationView.getHeaderView(0);
            TextView txtEmail = hview.findViewById(R.id.email);
            TextView txtUserName = hview.findViewById(R.id.UserName);

            txtEmail.setText(email);
            txtUserName.setText(name);

            String uid = user.getUid();


            navigationView.setNavigationItemSelectedListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.btn_sync) {
                    if (Methods.isNetworkConnected(getApplicationContext())) {
                        sync(uid);
                    }else{
                        Toast.makeText(HomeActivity.this,"Network Problem",Toast.LENGTH_SHORT).show();
                    }
                    menuItem.setCheckable(false);

                } else if (id == R.id.btn_logout) {
                    if (Methods.isNetworkConnected(getApplicationContext())) {
                        auth.signOut();
                        signOut();
                    }else{
                        Toast.makeText(HomeActivity.this,"Network Problem",Toast.LENGTH_SHORT).show();
                    }
                    menuItem.setCheckable(false);
                } else if (id == R.id.nav_home) {
                    fragmentFlag = true;
                } else if (id == R.id.nav_history) {
                    fragmentFlag = false;
                }
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        menu.getItem(0).setOnMenuItemClickListener(item -> {
            recyclerViewHome = findViewById(R.id.recyclerViewHome);
            recyclerViewHistory = findViewById(R.id.recyclerViewHistory);

            if ((fragmentFlag) && Objects.requireNonNull(recyclerViewHome.getAdapter()).getItemCount() != 0) {
                alertDialogDeleteAll(true);
            } else if ((!fragmentFlag) && Objects.requireNonNull(recyclerViewHistory.getAdapter()).getItemCount() != 0) {
                alertDialogDeleteAll(false);
            } else {
                Toast.makeText(HomeActivity.this, "No Trip To Delete", Toast.LENGTH_LONG).show();
            }

            return true;
        });
        return true;
    }

    private void alertDialogDeleteAll(boolean flag) {
        final AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
                .setCancelable(false)
                .setTitle("Confirmation")
                .setMessage("Do you want to remove All Trips ?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button yesButton = (alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
            Button noButton = (alertDialog).getButton(AlertDialog.BUTTON_NEGATIVE);
            yesButton.setOnClickListener(view -> {
                //Check Whether the recycler view is empty or not
                if (flag) {
                    Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(getApplication()).deleteAllUpcoming());
                    Toast.makeText(HomeActivity.this, "All UPCOMING Trips Are Deleted", Toast.LENGTH_LONG).show();

                } else {
                    Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(getApplication()).deleteAllHistory());
                    Toast.makeText(HomeActivity.this, "All History Trips Are Deleted", Toast.LENGTH_LONG).show();

                }

                alertDialog.dismiss();
            });
            noButton.setOnClickListener(view -> alertDialog.dismiss());
        });
        alertDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LiveData<List<Trip>> listLiveData = RoomDB.getTrips(getApplicationContext()).getAll();
        listLiveData.observe(this, trips -> tripList = trips);

    }

    public void sync(String uid) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                databaseReference.child(uid).child("Trips").setValue(tripList);

                Toast.makeText(HomeActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(HomeActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOut() {
        Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(getApplication()).deleteAll());
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}





