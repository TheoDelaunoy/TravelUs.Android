package com.example.travelusandroid.FlightAPI;

import com.example.travelusandroid.Models.Requests.AmadeusFlightAnywhere;
import com.example.travelusandroid.Models.Requests.CityIATA.CityIATA;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface CityInterface {
    @GET("v1/reference-data/locations/cities")
    Call<CityIATA> getCityIata(
            @Header("Authorization") String token,
            @Query("keyword") String city,
            @Query("max") String max);
}