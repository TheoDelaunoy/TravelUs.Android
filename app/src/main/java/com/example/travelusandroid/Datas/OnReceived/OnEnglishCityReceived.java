package com.example.travelusandroid.Datas.OnReceived;

import com.example.travelusandroid.Models.Basics.FlightInspirationParameters;

public interface OnEnglishCityReceived {
    void onEnglishCityReceived(FlightInspirationParameters flightInspirationParameters, int iteration) throws InterruptedException;
}
