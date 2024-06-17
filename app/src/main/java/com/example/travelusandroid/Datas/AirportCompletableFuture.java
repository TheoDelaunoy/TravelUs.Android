package com.example.travelusandroid.Datas;

import com.example.travelusandroid.FlightAPI.AmadeusClient;
import com.example.travelusandroid.FlightAPI.CityInterface;
import com.example.travelusandroid.Models.Basics.DatabaseAirport;
import com.example.travelusandroid.Models.Requests.CityIATA.CityIATA;
import com.example.travelusandroid.Models.Requests.CityIATA.LocationData;
import com.example.travelusandroid.Utils.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

public class AirportCompletableFuture {

    public static CompletableFuture<DatabaseAirport> getAirportFromIata(String iata) {
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

    public static CompletableFuture<String> getIata(String departureCity, String token) {
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
}
