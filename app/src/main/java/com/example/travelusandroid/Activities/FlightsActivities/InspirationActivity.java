package com.example.travelusandroid.Activities.FlightsActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelusandroid.Datas.AirportInterface;
import com.example.travelusandroid.Datas.CompletableFuture.AirportCompletableFuture;
import com.example.travelusandroid.Datas.DatabaseClient;
import com.example.travelusandroid.FlightAPI.CompletableFuture.CityCompletableFuture;
import com.example.travelusandroid.FlightAPI.CompletableFuture.FlightCompletableFuture;
import com.example.travelusandroid.Models.Basics.DatabaseAirport;
import com.example.travelusandroid.Models.Basics.FlightInspirationParameters;
import com.example.travelusandroid.Models.Requests.AmadeusFlightAnywhere;
import com.example.travelusandroid.Models.Requests.CityIATA.CityIATA;
import com.example.travelusandroid.R;
import com.example.travelusandroid.Utils.ListUtils;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
                try {
                    searchButtonClicked();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        destinationCell.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseAirport selectedAirport = finalDestinations.get(position);
                Toast.makeText(InspirationActivity.this, "Clicked: " + selectedAirport.getAirports(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InspirationActivity.this, FlightsActivity.class);
                intent.putExtra("destinationAirport", selectedAirport);
                //intent.putParcelableArrayListExtra("originAirportsParameters", (ArrayList<? extends Parcelable>) flightInspirationParametersList);
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


    private void searchButtonClicked() throws ExecutionException, InterruptedException {
        finalDestinations = new ArrayList<>();
        Toast.makeText(this, "Recherche de vols en cours...", Toast.LENGTH_SHORT).show();
        List<FlightInspirationParameters> flightInspirationParametersList = new ArrayList<>();
        List<List<String>> allDestinations = new ArrayList<>();

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
        flightInspirationParametersList = initiateCitySearch(childCount);
        flightInspirationParametersList = getCityIata(flightInspirationParametersList);

        allDestinations = getDestinationsInspirations(flightInspirationParametersList, departureDate);
        List<String> uniqueDestinations = ListUtils.getIntersection(allDestinations);
        List<DatabaseAirport> uniqueAirports = getAllAirports(uniqueDestinations);

        List<String> listViewCities = uniqueAirports.stream()
                .map(DatabaseAirport::getCity)
                .collect(Collectors.toList());
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listViewCities);
        destinationCell.setAdapter(listViewAdapter);
        airportLabel.setText(String.valueOf(uniqueAirports.size()));

        //TODO: Ability To click on List
    }

    private List<FlightInspirationParameters> initiateCitySearch(int childCount) {
        List<FlightInspirationParameters> flightInspirationParametersList = new ArrayList<FlightInspirationParameters>();
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

        }
        return flightInspirationParametersList;
    }

    public List<FlightInspirationParameters> getCityIata(List<FlightInspirationParameters> flightInspirationParametersList) throws ExecutionException, InterruptedException {
        for(int i = 0; i< flightInspirationParametersList.size(); i++)
        {
            String englishCity = AirportCompletableFuture.getEnglishCity(flightInspirationParametersList.get(i).getDepartureCity().split(" ")[0]).thenApply(englishTown ->{
                return englishTown;
            }).get();

            CityIATA cityIATA = CityCompletableFuture.getCityIata(token, englishCity, "1").thenApply(iataCode -> {
                return iataCode;
            }).get();
            flightInspirationParametersList.get(i).setCityIata(cityIATA.getData().get(0).getIataCode());
        }
        return flightInspirationParametersList;
    }

    public List<List<String>> getDestinationsInspirations(List<FlightInspirationParameters> flightInspirationParametersList, String departureDate) throws ExecutionException, InterruptedException {
        List<List<String>> allDestinations = new ArrayList<>();
        for(int i = 0; i< flightInspirationParametersList.size(); i++)
        {
            FlightInspirationParameters flightParameters = flightInspirationParametersList.get(i);
            AmadeusFlightAnywhere response = null;
            if(flightParameters.getBudget() == 0){
                response = FlightCompletableFuture.getFlightsAnywhereWithoutBudget(token, flightParameters.getCityIata(),departureDate, flightParameters.isHasStopovers()).thenApply(apiResponse ->{
                    return apiResponse;
                }).get();
            }else {
                response = FlightCompletableFuture.getFlightsAnywhereWithBudget(token, flightParameters.getCityIata(), departureDate, flightParameters.isHasStopovers(),flightParameters.getBudget()).thenApply(apiResponse ->{
                    return apiResponse;
                }).get();
            }

            List<String> destinations = new ArrayList<>();
            for(int j = 0; j <response.getData().size(); j++ ){
                destinations.add(response.getData().get(j).getDestination());
            }

            Set<String> destinationSet = new HashSet<>(destinations);
            List<String> uniqueDestinations = new ArrayList<>(destinationSet);
            allDestinations.add(uniqueDestinations);
        }
        return allDestinations;
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

    public List<DatabaseAirport> getAllAirports(List<String> uniqueIata) throws ExecutionException, InterruptedException {
        List<DatabaseAirport> uniqueAirports = new ArrayList<>();
        for(int i = 0; i< uniqueIata.size(); i++){
            DatabaseAirport response = AirportCompletableFuture.getAirportFromIata(uniqueIata.get(i)).thenApply(databaseResponse ->{
                return databaseResponse;
            }).get();
            uniqueAirports.add(response);
        }
        return uniqueAirports;
    }
}