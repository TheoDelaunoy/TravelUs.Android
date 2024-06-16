package com.example.travelusandroid.Activities.FlightsActivities;

import android.content.Intent;
import android.os.Bundle;

import com.example.travelusandroid.Models.Basics.DatabaseAirport;
import com.example.travelusandroid.Models.Basics.FlightInspirationParameters;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import com.example.travelusandroid.R;

import java.util.ArrayList;

public class FlightsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        Intent intent = getIntent();
        DatabaseAirport destinationAirport = (DatabaseAirport) intent.getSerializableExtra("destinationAirport");
        ArrayList<FlightInspirationParameters> flightInspirationParameters = intent.getParcelableArrayListExtra("originAirportsParameters");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        addMenuItems(bottomNavigationView.getMenu(), flightInspirationParameters);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                selectedFragment = FlightFragmentActivity.newInstance(menuItem.getItemId(), flightInspirationParameters);

                if (selectedFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, selectedFragment);
                    transaction.commit();
                }

                return true;
            }
        });

        // Set the initial fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(0);
        }
    }

    private void addMenuItems(Menu menu, ArrayList<FlightInspirationParameters> flightInspirationParameters) {
        // Add items dynamically
        for(int i=0; i<flightInspirationParameters.size(); ++i)
        {
            menu.add(Menu.NONE, i, Menu.NONE, flightInspirationParameters.get(i).getDepartureCity());
        }
    }
}