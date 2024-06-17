package com.example.travelusandroid.Activities.FlightsActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelusandroid.Datas.AirportInterface;
import com.example.travelusandroid.Datas.AirportCompletableFuture;
import com.example.travelusandroid.Datas.DatabaseClient;
import com.example.travelusandroid.Datas.OnReceived.OnEnglishCityReceived;
import com.example.travelusandroid.Datas.OnReceived.OnInspirationReceived;
import com.example.travelusandroid.FlightAPI.AmadeusClient;
import com.example.travelusandroid.FlightAPI.FlightInterface;
import com.example.travelusandroid.Models.Basics.DatabaseAirport;
import com.example.travelusandroid.Models.Basics.FlightInspirationParameters;
import com.example.travelusandroid.Models.Requests.AmadeusFlightAnywhere;
import com.example.travelusandroid.R;
import com.example.travelusandroid.Utils.ListUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private List<DatabaseAirport> finalDestinations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        token = intent.getStringExtra("flightsToken");
        allCities = new ArrayList<>();
        getCities();
        setContentView(R.layout.activity_inspiration);

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

        destinationCell.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseAirport selectedAirport = finalDestinations.get(position);
                Toast.makeText(InspirationActivity.this, "Clicked: " + selectedAirport.getAirports(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InspirationActivity.this, FlightsActivity.class);
                intent.putExtra("destinationAirport", selectedAirport);
                intent.putParcelableArrayListExtra("originAirportsParameters", (ArrayList<? extends Parcelable>) flightInspirationParametersList);
                startActivity(intent);
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
                if (userLayout.getChildCount() == 0) {
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
        finalDestinations = new ArrayList<>();
        Toast.makeText(this, "Recherche de vols en cours...", Toast.LENGTH_SHORT).show();
        flightInspirationParametersList = new ArrayList<>();
        allDestinations = new ArrayList<>();
        adapter = null;

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
            EditText budget = null;
            budget = (EditText) linearLayout.getChildAt(1);
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
                    String cityIATA = AirportCompletableFuture.getIata(flightInspirationParameters.getDepartureCity(), token).thenApply(iataCode -> {
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

            for (String intersectionDestination : intersectionDestinations) {
                try {
                    DatabaseAirport destinationAirport = AirportCompletableFuture.getAirportFromIata(intersectionDestination).thenApply(airport -> {
                        return airport;
                    }).get();
                    finalDestinations.add(destinationAirport);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // Extract city names
            List<String> finalCities = new ArrayList<>();
            for (DatabaseAirport airport : finalDestinations) {
                finalCities.add(airport.getCity());
            }

            // Update ListView and TextView on UI thread
            runOnUiThread(() -> {
                // Update the TextView with the number of cities
                airportLabel.setText(String.valueOf(finalCities.size()));

                // Update the ListView
                adapter = new ArrayAdapter<>(this, R.layout.destination_cell, R.id.cityTextView, finalCities);
                destinationCell.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            });
        }).start();
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