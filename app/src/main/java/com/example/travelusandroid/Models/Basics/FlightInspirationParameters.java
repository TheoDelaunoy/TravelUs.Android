package com.example.travelusandroid.Models.Basics;

public class FlightInspirationParameters {
    private String departureCity;
    private int budget;
    private boolean hasStopovers;
    private String cityIata;

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public boolean isHasStopovers() {
        return hasStopovers;
    }

    public void setHasStopovers(boolean haveStopovers) {
        this.hasStopovers = haveStopovers;
    }

    public String getCityIata() {
        return cityIata;
    }

    public void setCityIata(String cityIata) {
        this.cityIata = cityIata;
    }
}
