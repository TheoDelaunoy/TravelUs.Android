package com.example.travelusandroid.Datas;

import com.example.travelusandroid.Models.Basics.AmadeusToken;
import com.example.travelusandroid.Models.Basics.DatabaseAirport;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AirportInterface {

    @GET("aeroports/getAllCities.php")
    Call<List<String>> getAllCities();

    @GET("aeroports/cityToEnglishCity.php")
    Call<String> getEnglishCity(
            @Query("city") String city);
}
