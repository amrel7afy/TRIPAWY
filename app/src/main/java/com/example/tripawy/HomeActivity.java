package com.example.tripawy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.tripawy.helper.HelperMethods;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    public RecyclerView recyclerViewHome;
    public RecyclerView recyclerViewHistory;

    private ImageView imagePhoto;
    private TextView txtEmail;
    private TextView txtUserName;

    private List<Trip> tripList;

    private boolean fragmentFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.appBarHome.toolbar);

        recyclerViewHome = findViewById(R.id.recyclerViewHome);
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);

        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * On click the floating point button the add trip dialogue's pop up
                 */
                if (HelperMethods.canDrawOverlays(getApplicationContext())) {
                    Intent intent = new Intent(HomeActivity.this, AddNewTripActivity.class);
                    startActivity(intent);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(view, "Draw Overlays Error", Snackbar.LENGTH_INDEFINITE)
                            .setBackgroundTint(ContextCompat.getColor(HomeActivity.this, R.color.white))
                            .setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.sky))
                            .setActionTextColor(ContextCompat.getColor(HomeActivity.this, R.color.sky))
                            .setAction("FIX", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    HelperMethods.openDrawSettings(HomeActivity.this);
                                }
                            });
                    snackbar.show();
                }

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

        /****
         * Access user Data
         */
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            View hview = navigationView.getHeaderView(0);
            imagePhoto = hview.findViewById(R.id.photo);
            txtEmail = hview.findViewById(R.id.email);
            txtUserName = hview.findViewById(R.id.UserName);

            txtEmail.setText(email);
            txtUserName.setText(name);

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();


            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int id = menuItem.getItemId();
                    if (id == R.id.btn_sync) {
                        menuItem.setCheckable(false);
                        sync(uid);
                    } else if (id == R.id.btn_logout) {
                        menuItem.setCheckable(false);
                        auth.signOut();
                        signOut();
                    } else if (id == R.id.nav_home) {
                        fragmentFlag = true;
                    } else if (id == R.id.nav_history) {
                        fragmentFlag = false;
                    }
                    NavigationUI.onNavDestinationSelected(menuItem, navController);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                recyclerViewHome = findViewById(R.id.recyclerViewHome);
                recyclerViewHistory = findViewById(R.id.recyclerViewHistory);

                if ((fragmentFlag) && recyclerViewHome.getAdapter().getItemCount() != 0) {
                    alertDialogDeleteAll(true);
                } else if ((!fragmentFlag) && recyclerViewHistory.getAdapter().getItemCount() != 0) {
                    alertDialogDeleteAll(false);
                } else {
                    Toast.makeText(HomeActivity.this, "No Trip To Delete", Toast.LENGTH_LONG).show();
                }

                return true;
            }
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
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button yesButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button noButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Check Whether the recycler view is empty or not
                        if (flag) {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                RoomDB.getTrips(getApplication()).deleteAllUpcoming();
                            });
                            Toast.makeText(HomeActivity.this, "All UPCOMING Trips Are Deleted", Toast.LENGTH_LONG).show();

                        } else {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                RoomDB.getTrips(getApplication()).deleteAllHistory();
                            });
                            Toast.makeText(HomeActivity.this, "All History Trips Are Deleted", Toast.LENGTH_LONG).show();

                        }

                        alertDialog.dismiss();
                    }

                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }

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
        listLiveData.observe((LifecycleOwner) this, new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                tripList = trips;

            }
        });

    }

    public void sync(String uid) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                //Converters.fromArrayList(tripList.getValue().get(0).getNotes());

                databaseReference.child(uid).child("Trips").setValue(tripList);

                // after adding this data we are showing toast message.
                Toast.makeText(HomeActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(HomeActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOut() {
        Executors.newSingleThreadExecutor().execute(() -> {
            RoomDB.getTrips(getApplication()).deleteAll();
        });
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}





