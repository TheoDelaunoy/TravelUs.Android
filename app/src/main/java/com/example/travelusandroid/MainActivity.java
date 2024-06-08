package com.example.travelusandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.travelusandroid.database.UserTableModel;
import com.example.travelusandroid.Activities.FlightsActivities.InspirationActivity;
import com.example.travelusandroid.FlightAPI.AmadeusClient;
import com.example.travelusandroid.FlightAPI.TokenInterface;
import com.example.travelusandroid.Models.Basics.AmadeusToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import com.example.travelus.views.Friends.TabsFriendPage;
//import com.example.travelus.views.Flight.InspirationPage;
//import com.example.travelus.views.Sharing.TripTabsPage;


public class MainActivity extends AppCompatActivity {

    private static String API_KEY = "74gCH4xEzsTqR8v8tmRq5VTeVU1I62j5";
    private static String API_SECRET = "VNvwxPnQFwwZl5cT";

    private TextView helloLabel;
    private Button flightButton;

    //private Button disconnectButton;
    private Button friendButton;
    private Button tripButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w("MainActivity", "Main Activity Entered");

        // Get user from intent
        //myUser = (UserTableModel) getIntent().getSerializableExtra("user");

        helloLabel = findViewById(R.id.helloLabel);
        //helloLabel.setText("Bonjour " + myUser.getPseudo() + " !");

        flightButton = findViewById(R.id.flightButton);
        //disconnectButton = findViewById(R.id.disconnectButton);
        friendButton = findViewById(R.id.friendButton);
        tripButton = findViewById(R.id.tripButton);

        flightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flightButtonClicked();
            }
        });

        /*
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disconnectButtonClicked();
            }
        });
         */


        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //friendButtonClicked();
            }
        });

        tripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tripButtonClicked();
            }
        });
    }


    private void flightButtonClicked(){
        TokenInterface tokenInterface = AmadeusClient.getClient().create(TokenInterface.class);
        Call<AmadeusToken> call = tokenInterface.getAccessToken("client_credentials", API_KEY, API_SECRET);
        call.enqueue(new Callback<AmadeusToken>() {
            @Override
            public void onResponse(Call<AmadeusToken> call, Response<AmadeusToken> response) {
                if(response.isSuccessful()){
                    AmadeusToken token = response.body();
                    if(token != null){
                        Log.d("Token", "Access Token: " + token.getAccess_token());
                        Intent intent = new Intent(MainActivity.this, InspirationActivity.class);
                        intent.putExtra("flightsToken", "Bearer " + token.getAccess_token());
                        startActivity(intent);
                    }
                } else {
                    Log.e("API Error", "Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AmadeusToken> call, Throwable t) {
                Log.e("API Error", "Error: " + t.getMessage());
            }
        });
        //intent.putExtra("user", myUser);
    }


/*
    private void disconnectButtonClicked() {
        getSharedPreferences("app", MODE_PRIVATE).edit().remove(KEY_TOKEN).apply();
        finish();
    }

    private void friendButtonClicked() {
        UsersTable.requestForMe(myUser.getId(), new UsersTable.Callback() {
            @Override
            public void onSuccess(List<UserTableModel> friendsrequest) {
                Intent intent = new Intent(MainActivity.this, TabsFriendPage.class);
                intent.putExtra("user", myUser);
                intent.putExtra("friendsrequest", (Serializable) friendsrequest);
                startActivity(intent);
            }

            @Override
            public void onError(Exception ex) {
                Toast.makeText(MainActivity.this, "Erreur: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tripButtonClicked() {
        Intent intent = new Intent(MainActivity.this, TripTabsPage.class);
        intent.putExtra("user", myUser);
        startActivity(intent);
    }*/
}
