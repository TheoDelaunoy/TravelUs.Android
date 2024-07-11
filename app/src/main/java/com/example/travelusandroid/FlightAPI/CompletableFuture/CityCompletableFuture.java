package com.example.travelusandroid.FlightAPI.CompletableFuture;

import com.example.travelusandroid.Datas.AirportInterface;
import com.example.travelusandroid.Datas.DatabaseClient;
import com.example.travelusandroid.FlightAPI.AmadeusClient;
import com.example.travelusandroid.FlightAPI.CityInterface;
import com.example.travelusandroid.Models.Basics.DatabaseAirport;
import com.example.travelusandroid.Models.Requests.CityIATA.CityIATA;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

public class CityCompletableFuture {

    public static CompletableFuture<CityIATA> getCityIata(String token, String keyword, String max) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CityInterface cityInterface = AmadeusClient.getClient().create(CityInterface.class);

                Call<CityIATA> call = cityInterface.getCityIata(token, keyword,"1");
                Response<CityIATA> response = call.execute();
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


}
