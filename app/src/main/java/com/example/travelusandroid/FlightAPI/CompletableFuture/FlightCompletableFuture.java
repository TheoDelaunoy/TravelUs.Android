package com.example.travelusandroid.FlightAPI.CompletableFuture;

import com.example.travelusandroid.FlightAPI.AmadeusClient;
import com.example.travelusandroid.FlightAPI.FlightInterface;
import com.example.travelusandroid.Models.Requests.AmadeusFlightAnywhere;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

public class FlightCompletableFuture {

    public static CompletableFuture<AmadeusFlightAnywhere> getFlightsAnywhereWithBudget(String token, String departureIata, String departureDate, boolean sansEscales, int maxPrice) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FlightInterface flightInterface = AmadeusClient.getClient().create(FlightInterface.class);

                Call<AmadeusFlightAnywhere> call = flightInterface.getFlightsAnywhereWithBudget(token, departureIata,departureDate, true, sansEscales, maxPrice, "DESTINATION");
                Response<AmadeusFlightAnywhere> response = call.execute();
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

    public static CompletableFuture<AmadeusFlightAnywhere> getFlightsAnywhereWithoutBudget(String token, String departureIata, String departureDate, boolean sansEscales) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FlightInterface flightInterface = AmadeusClient.getClient().create(FlightInterface.class);

                Call<AmadeusFlightAnywhere> call = flightInterface.getFlightsAnywhereWithoutBudget(token, departureIata,departureDate, true, sansEscales, "DESTINATION");
                Response<AmadeusFlightAnywhere> response = call.execute();
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
