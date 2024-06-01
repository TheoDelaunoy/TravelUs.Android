package com.example.travelusandroid.FlightAPI;

import com.example.travelusandroid.Models.Basics.AmadeusToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TokenInterface {
    @FormUrlEncoded
    @POST("v1/security/oauth2/token")
    Call<AmadeusToken> getAccessToken(
            @Field("grant_type") String grantType,
            @Field("client_id") String apiKey,
            @Field("client_secret") String apiSecret);
}
