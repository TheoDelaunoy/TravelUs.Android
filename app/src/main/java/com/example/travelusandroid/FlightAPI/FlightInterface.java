package com.example.travelusandroid.FlightAPI;

import com.example.travelusandroid.Models.Requests.AmadeusFlightAnywhere;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FlightInterface {
    @GET("v1/shopping/flight-destinations")
    Call<AmadeusFlightAnywhere> getFlightsAnywhereWithBudget(
            @Header("Authorization") String token,
            @Query("origin") String departureIata,
            @Query("departureDate") String departureDate,
            @Query("oneWay") boolean oneWay,
            @Query("nonStop") boolean sansEscale,
            @Query("maxPrice") int maxPrice,
            @Query("viewBy") String destination);

    @GET("v1/shopping/flight-destinations")
    Call<AmadeusFlightAnywhere> getFlightsAnywhereWithoutBudget(
        @Header("Authorization") String token,
        @Query("origin") String departureIata,
        @Query("departureDate") String departureDate,
        @Query("oneWay") boolean oneWay,
        @Query("nonStop") boolean sansEscale,
        @Query("viewBy") String destination);
}
