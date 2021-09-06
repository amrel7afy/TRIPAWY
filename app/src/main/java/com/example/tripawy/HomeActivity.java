package com.example.tripawy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    public RecyclerView recyclerViewHome;
    public RecyclerView recyclerViewHistory;

    private ImageView imagePhoto;
    private TextView txtEmail;
    private TextView txtUserName;


    public static boolean fragmentFlag;

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
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                /**
                 * On click the floating point button the add trip dialogfragment pop up
                 */
                Intent intent = new Intent(HomeActivity.this, AddNewTripActivity.class);
                startActivity(intent);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url

            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            View hview = navigationView.getHeaderView(0);
            imagePhoto = hview.findViewById(R.id.photo);
            txtEmail = hview.findViewById(R.id.email);
            txtUserName = hview.findViewById(R.id.UserName);

            txtEmail.setText(email);
            txtUserName.setText(name);

            //imagePhoto.setImageResource(R.drawable.user);

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.btn_sync) {
                    sync();
                }
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });



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
                    Toast.makeText(HomeActivity.this, "No Trips To delete", Toast.LENGTH_LONG).show();
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

    }

    public void sync() {

    }


}