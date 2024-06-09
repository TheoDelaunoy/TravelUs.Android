package com.example.travelusandroid.Activities.FlightsActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelusandroid.Datas.AirportInterface;
import com.example.travelusandroid.Datas.DatabaseClient;
import com.example.travelusandroid.Datas.OnReceived.OnEnglishCityReceived;
import com.example.travelusandroid.Datas.OnReceived.OnInspirationReceived;
import com.example.travelusandroid.FlightAPI.AmadeusClient;
import com.example.travelusandroid.FlightAPI.CityInterface;
import com.example.travelusandroid.FlightAPI.FlightInterface;
import com.example.travelusandroid.Models.Basics.DatabaseAirport;
import com.example.travelusandroid.Models.Basics.FlightInspirationParameters;
import com.example.travelusandroid.Models.Requests.AmadeusFlightAnywhere;
import com.example.travelusandroid.Models.Requests.CityIATA.CityIATA;
import com.example.travelusandroid.Models.Requests.CityIATA.LocationData;
import com.example.travelusandroid.R;
import com.example.travelusandroid.Utils.ListUtils;
import com.example.travelusandroid.Utils.StringUtils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InspirationActivity extends AppCompatActivity {

    private String token;
    private LinearLayout labelStack;
    private LinearLayout userLayout;
    private DatePicker dateDeparture;
    private TextView airportLabel;
    private ListView destinationCell;

    private List<String> allCities;
    private String departureDate;
    private List<FlightInspirationParameters> flightInspirationParametersList;
    private List<List<String>> allDestinations = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        token = intent.getStringExtra("flightsToken");
        allCities = new ArrayList<>();
        getCities();
        setContentView(R.layout.activity_inspiration);

        //frameStack = findViewById(R.id.frameStack);
        labelStack = findViewById(R.id.labelStack);
        userLayout = findViewById(R.id.userLayout);
        dateDeparture = findViewById(R.id.dateDeparture);
        airportLabel = findViewById(R.id.airportLabel);
        destinationCell = findViewById(R.id.destinationCell);

        Button newTravelUserButton = findViewById(R.id.newTravelUserButton);
        newTravelUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTravelUserButtonClicked();
            }
        });

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    searchButtonClicked();
            }
        });
    }

    private void newTravelUserButtonClicked() {
        labelStack.setVisibility(View.VISIBLE);

        LinearLayout newUser = new LinearLayout(this);
        newUser.setOrientation(LinearLayout.HORIZONTAL);

        AutoCompleteTextView cityTextView = new AutoCompleteTextView(this);
        cityTextView.setHint("Ville");

        EditText budgetEntry = new EditText(this);
        budgetEntry.setHint("Budget");
        CheckBox checkBox = new CheckBox(this);

        Button deleteButton = new Button(this);
        deleteButton.setText("Supprimer");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLayout.removeView((View) v.getParent());
                if (userLayout.getChildCount() == 0) { // Ajustez la condition selon votre logique
                    labelStack.setVisibility(View.GONE);
                }
            }
        });

        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Calculate widths
        int autoCompleteWidth = (2*screenWidth) / 5;
        int editTextWidth = screenWidth / 6;
        int deleteButtonWidth = screenWidth / 5;

        // Set layout parameters
        LinearLayout.LayoutParams autoCompleteLayoutParams = new LinearLayout.LayoutParams(autoCompleteWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(editTextWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams deleteButtonLayoutParams = new LinearLayout.LayoutParams(deleteButtonWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        cityTextView.setLayoutParams(autoCompleteLayoutParams);
        budgetEntry.setLayoutParams(editTextLayoutParams);
        deleteButton.setLayoutParams(deleteButtonLayoutParams);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, allCities);

        // Set the adapter to the AutoCompleteTextView
        cityTextView.setThreshold(3);
        cityTextView.setAdapter(adapter);

        newUser.addView(cityTextView);
        newUser.addView(budgetEntry);
        newUser.addView(checkBox);
        newUser.addView(deleteButton);

        userLayout.addView(newUser);
    }

    private void searchButtonClicked() {
        Toast.makeText(this, "Recherche de vols en cours...", Toast.LENGTH_SHORT).show();
        flightInspirationParametersList = new ArrayList<>();
        int year = dateDeparture.getYear();
        int month = dateDeparture.getMonth() + 1;
        int dayOfMonth = dateDeparture.getDayOfMonth();
        String monthString = String.valueOf(month);
        String dayString = String.valueOf(dayOfMonth);
        if(month<10)
        {
            monthString = "0" + month;
        }
        if (dayOfMonth<10){
            dayString = "0" + dayOfMonth;
        }

        departureDate = year + "-" + monthString + "-" + dayString;

        int childCount = userLayout.getChildCount();
        CountDownLatch latch = new CountDownLatch(childCount); // Initialize CountDownLatch with the number of children

        initiateCitySearch(childCount, latch);

        waitForAllCitiesToBeFetched(latch);
    }

    private void initiateCitySearch(int childCount, CountDownLatch latch) {
        for (int i = 0; i < childCount; i++) {
            LinearLayout linearLayout = (LinearLayout) userLayout.getChildAt(i);

            AutoCompleteTextView city = (AutoCompleteTextView) linearLayout.getChildAt(0);
            String cityText = city.getText().toString();

            EditText budget = (EditText) linearLayout.getChildAt(1);
            int budgetInt = 0;
            if (!budget.getText().toString().isEmpty()) {
                budgetInt = Integer.parseInt(budget.getText().toString());
            }

            CheckBox hasStepovers = (CheckBox) linearLayout.getChildAt(2);
            boolean hasStepoversBoolean = hasStepovers.isChecked();

            FlightInspirationParameters flightInspirationParameters = new FlightInspirationParameters();
            flightInspirationParameters.setDepartureCity(cityText);
            flightInspirationParameters.setBudget(budgetInt);
            flightInspirationParameters.setHasStopovers(hasStepoversBoolean);
            flightInspirationParametersList.add(flightInspirationParameters);
            // TO CHANGE
            fetchEnglishCity(latch, flightInspirationParameters, i);
        }
    }

    private void fetchEnglishCity(CountDownLatch latch, FlightInspirationParameters flightInspirationParameters, int iteration) {
        getEnglishCitySynchronous(new OnEnglishCityReceived() {
            @Override
            public void onEnglishCityReceived(FlightInspirationParameters flightInspirationParameters1, int iteration) {
                flightInspirationParametersList.get(iteration).setDepartureCity(flightInspirationParameters1.getDepartureCity());
                latch.countDown(); // Decrement the latch count
            }
        }, flightInspirationParameters, iteration);
    }

    private void waitForAllCitiesToBeFetched(CountDownLatch latch) {
        new Thread(() -> {
            try {
                latch.await(); // Wait until the count reaches zero
            } catch (InterruptedException e) {
                Log.d("Stack Trace", Objects.requireNonNull(e.getMessage()));
            }

            displayFetchedCities();
        }).start();
    }

    private void displayFetchedCities() {
        CountDownLatch inspirationLatch = new CountDownLatch(flightInspirationParametersList.size());
        runOnUiThread(() -> {
            for (FlightInspirationParameters flightInspirationParameters : flightInspirationParametersList) {
                getInspirationsSynchronous(new OnInspirationReceived() {
                    @Override
                    public void onInspirationReceived(String departureCity) throws InterruptedException, IOException {
                        inspirationLatch.countDown();
                    }
                }, flightInspirationParameters);
            }
        });

        waitForAllInspirationsToBeFound(inspirationLatch);
    }

    public void getInspirationsSynchronous(final OnInspirationReceived inspirationListener, FlightInspirationParameters flightInspirationParameters){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String cityIATA = getIata(flightInspirationParameters.getDepartureCity()).thenApply(iataCode -> {
                        return iataCode;
                    }).get();
                    flightInspirationParameters.setCityIata(cityIATA);
                    FlightInterface flightInterface = AmadeusClient.getClient().create(FlightInterface.class);
                    Call<AmadeusFlightAnywhere> call = null;
                    if(flightInspirationParameters.getBudget() == 0){
                        call = flightInterface.getFlightsAnywhereWithoutBudget(token, flightInspirationParameters.getCityIata(), departureDate, true, !flightInspirationParameters.isHasStopovers(), "DESTINATION");
                    }else{
                        call = flightInterface.getFlightsAnywhereWithBudget(token, flightInspirationParameters.getCityIata(), departureDate, true, !flightInspirationParameters.isHasStopovers(), flightInspirationParameters.getBudget(), "DESTINATION");
                    }
                    Response<AmadeusFlightAnywhere> response = call.execute();
                    AmadeusFlightAnywhere inspirations = response.body();
                    List<String> destinations = new ArrayList<>();
                    for(int i = 0; i< inspirations.getData().size(); i++ ){
                        destinations.add(inspirations.getData().get(i).getDestination());
                    }
                    Set<String> destinationSet = new HashSet<>(destinations);
                    List<String> uniqueDestinations = new ArrayList<>(destinationSet);
                    allDestinations.add(uniqueDestinations);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                inspirationListener.onInspirationReceived(flightInspirationParameters.getCityIata());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public CompletableFuture<String> getIata(String departureCity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CityInterface cityInterface = AmadeusClient.getClient().create(CityInterface.class);
                Call<CityIATA> call = cityInterface.getCityIata(token, StringUtils.trimEnd(departureCity), "1");
                Response<CityIATA> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    CityIATA cityIATA = response.body();
                    List<LocationData> data = cityIATA.getData();

                    if (data != null && !data.isEmpty()) {
                        return data.get(0).getIataCode();
                    } else {
                        throw new RuntimeException("No data available");
                    }
                } else {
                    throw new IOException("API Error: " + response.code());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void getEnglishCitySynchronous(final OnEnglishCityReceived listener, FlightInspirationParameters flightInspirationParameters, int iteration) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String trueCity = extractCityName(flightInspirationParameters.getDepartureCity());
                    AirportInterface airportInterface = DatabaseClient.getClient().create(AirportInterface.class);
                    Call<String> call = airportInterface.getEnglishCity(trueCity);
                    Response<String> response = call.execute();

                    if (response.isSuccessful()) {
                        String englishCity = response.body();
                        if (englishCity != null) {
                            Log.d("DB Call", "English City received");
                            flightInspirationParameters.setDepartureCity(englishCity);
                            // Post the result back to the main thread
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        listener.onEnglishCityReceived(flightInspirationParameters, iteration);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }
                    } else {
                        Log.e("API Error", "Response Code: " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("API Error", "Error: " + e.getMessage());
                }
            }
        }).start();
    }

    private void waitForAllInspirationsToBeFound(CountDownLatch inspirationLatch) {
        new Thread(() -> {
            try {
                inspirationLatch.await(); // Wait until the count reaches zero
            } catch (InterruptedException e) {
                Log.d("Stack Trace", Objects.requireNonNull(e.getMessage()));
            }

            // Get the intersection of all destinations
            List<String> intersectionDestinations = ListUtils.getIntersection(allDestinations);

            List<DatabaseAirport> destinationAirports = new ArrayList<>();
            for (String intersectionDestination : intersectionDestinations) {
                try {
                    DatabaseAirport destinationAirport = getAirportFromIata(intersectionDestination).thenApply(airport -> {
                        return airport;
                    }).get();
                    destinationAirports.add(destinationAirport);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // Extract city names
            List<String> cities = new ArrayList<>();
            for (DatabaseAirport airport : destinationAirports) {
                cities.add(airport.getCity());
            }

            // Update ListView and TextView on UI thread
            runOnUiThread(() -> {
                // Update the TextView with the number of cities
                airportLabel.setText(String.valueOf(cities.size()));

                // Update the ListView
                adapter = new ArrayAdapter<>(this, R.layout.destination_cell, R.id.cityTextView, cities);
                destinationCell.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }



    public CompletableFuture<DatabaseAirport> getAirportFromIata(String iata) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                AirportInterface airportInterface = DatabaseClient.getClient().create(AirportInterface.class);
                Call<DatabaseAirport> call = airportInterface.getAirportFromIata(iata);
                Response<DatabaseAirport> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    return response.body();
                } else {
                    throw new IOException("Database Error: " + response.code());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String extractCityName(String input) {
        int index = input.indexOf('(');
        String cityName = index != -1 ? input.substring(0, index).trim() : input;
        return cityName;
    }

    public void getCities(){
        AirportInterface airportInterface = DatabaseClient.getClient().create(AirportInterface.class);
        Call<List<String>> call = airportInterface.getAllCities();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.isSuccessful()){
                    allCities = response.body();
                    if(allCities != null){
                        Log.d("DB Call", "Cities Received");
                    }
                } else {
                    Log.e("API Error", "Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("API Error", "Error: " + t.getMessage());
            }
        });
    }
}