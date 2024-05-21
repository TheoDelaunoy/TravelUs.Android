package com.example.travelusandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.travelusandroid.database.UserTableModel;
import com.example.travelusandroid.FlightAPI.FlightsToken;
//import com.example.travelus.views.Friends.TabsFriendPage;
//import com.example.travelus.views.Flight.InspirationPage;
//import com.example.travelus.views.Sharing.TripTabsPage;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FlightsToken flightsToken;

    private TextView helloLabel;
    private Button flightButton;

    //private Button disconnectButton;
    private Button friendButton;
    private Button tripButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get user from intent
        //myUser = (UserTableModel) getIntent().getSerializableExtra("user");

        flightsToken = new FlightsToken();
        helloLabel = findViewById(R.id.helloLabel);
        //helloLabel.setText("Bonjour " + myUser.getPseudo() + " !");

        flightButton = findViewById(R.id.flightButton);
        //disconnectButton = findViewById(R.id.disconnectButton);
        friendButton = findViewById(R.id.friendButton);
        tripButton = findViewById(R.id.tripButton);

        flightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flightButtonClicked();
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

/*
    private void flightButtonClicked() {
        try {
            flightsToken.getAccessTokenAsync(new FlightsToken.Callback() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(MainActivity.this, InspirationPage.class);
                    intent.putExtra("flightsToken", flightsToken);
                    intent.putExtra("user", myUser);
                    startActivity(intent);
                }

                @Override
                public void onError(Exception ex) {
                    Toast.makeText(MainActivity.this, "Erreur: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            Toast.makeText(this, "Erreur: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

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
