package com.example.travelusandroid.FlightAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAPI {
    private static String API_KEY = "74gCH4xEzsTqR8v8tmRq5VTeVU1I62j5";
    private static String API_SECRET = "VNvwxPnQFwwZl5cT";
    private static String TOKEN_ADDRESS = "https://test.api.amadeus.com/v1/security/oauth2/token";


    public void getAccessToken() throws IOException {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");
        parameters.put("client_id", API_KEY);
        parameters.put("client_secret", API_SECRET);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TOKEN_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }



}
