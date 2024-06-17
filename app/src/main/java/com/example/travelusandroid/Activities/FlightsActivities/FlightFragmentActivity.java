package com.example.travelusandroid.Activities.FlightsActivities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.travelusandroid.Datas.AirportCompletableFuture;
import com.example.travelusandroid.Models.Basics.DatabaseAirport;
import com.example.travelusandroid.Models.Basics.FlightInspirationParameters;
import com.example.travelusandroid.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FlightFragmentActivity extends Fragment {

    private static final String INDEX = "index";
    private static final String ORIGIN_AIRPORT_PARAMETERS = "originAirportsParameters";
    private int indexParameter;
    private ArrayList<FlightInspirationParameters> originAirportsParameters;


    public FlightFragmentActivity() {
        // Required empty public constructor
    }

    public static FlightFragmentActivity newInstance(int index, ArrayList<FlightInspirationParameters> flightInspirationParameters) {
        FlightFragmentActivity fragment = new FlightFragmentActivity();
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        args.putParcelableArrayList(ORIGIN_AIRPORT_PARAMETERS, flightInspirationParameters);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            indexParameter = getArguments().getInt(INDEX);
            originAirportsParameters = getArguments().getParcelableArrayList(ORIGIN_AIRPORT_PARAMETERS);
            //TextView fragmentText = (TextView) getView().findViewById(R.id.fragmentText);
            FlightInspirationParameters originAirportParameters = originAirportsParameters.get(indexParameter);
            /*try {
                DatabaseAirport originAirport = AirportCompletableFuture.getAirportFromIata(originAirportParameters.getCityIata()).thenApply(airport -> {
                    return airport;
                }).get();
                fragmentText.setText(originAirport.getAirports());
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_flight_fragment, container, false);
    }
}