package com.example.travelusandroid.Activities.FlightsActivities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelusandroid.Datas.AirportInterface;
import com.example.travelusandroid.Datas.DatabaseClient;
import com.example.travelusandroid.Datas.OnReceived.OnEnglishCityReceived;
import com.example.travelusandroid.R;

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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InspirationActivity extends AppCompatActivity {

    private LinearLayout frameStack;
    private LinearLayout labelStack;
    private LinearLayout userLayout;
    private DatePicker dateDeparture;
    private TextView airportLabel;
    private ListView destinationCell;

    private List<String> allCities;

    //private List<FlightClass> flightClasses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        List<String> englishCities = new ArrayList<>();
        int year = dateDeparture.getYear();
        int month = dateDeparture.getMonth() + 1;
        int dayOfMonth = dateDeparture.getDayOfMonth();

        int childCount = userLayout.getChildCount();
        CountDownLatch latch = new CountDownLatch(childCount); // Initialize CountDownLatch with the number of children

        initiateCitySearch(childCount, latch, englishCities);

        waitForAllCitiesToBeFetched(latch, englishCities);
    }

    private void initiateCitySearch(int childCount, CountDownLatch latch, List<String> englishCities) {
        for (int i = 0; i < childCount; i++) {
            LinearLayout linearLayout = (LinearLayout) userLayout.getChildAt(i);
            AutoCompleteTextView City = (AutoCompleteTextView) linearLayout.getChildAt(0);

            fetchEnglishCity(latch, englishCities, City.getText().toString());
        }
    }

    private void fetchEnglishCity(CountDownLatch latch, List<String> englishCities, String city) {
        getEnglishCitySynchronous(new OnEnglishCityReceived() {
            @Override
            public void onEnglishCityReceived(String englishCity) {
                englishCities.add(englishCity);
                latch.countDown(); // Decrement the latch count
            }
        }, city);
    }

    private void waitForAllCitiesToBeFetched(CountDownLatch latch, List<String> englishCities) {
        new Thread(() -> {
            try {
                latch.await(); // Wait until the count reaches zero
            } catch (InterruptedException e) {
                Log.d("Stack Trace", Objects.requireNonNull(e.getMessage()));
            }

            displayFetchedCities(englishCities);
        }).start();
    }

    private void displayFetchedCities(List<String> englishCities) {
        runOnUiThread(() -> {
            for (String englishCity : englishCities) {
                Log.d("TEST", englishCity);
            }
        });
    }


    public void getEnglishCitySynchronous(final OnEnglishCityReceived listener, String city) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String trueCity = extractCityName(city);
                    AirportInterface airportInterface = DatabaseClient.getClient().create(AirportInterface.class);
                    Call<String> call = airportInterface.getEnglishCity(trueCity);
                    Response<String> response = call.execute();

                    if (response.isSuccessful()) {
                        String englishCity = response.body();
                        if (englishCity != null) {
                            Log.d("DB Call", "English City received");
                            // Post the result back to the main thread
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        listener.onEnglishCityReceived(englishCity);
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