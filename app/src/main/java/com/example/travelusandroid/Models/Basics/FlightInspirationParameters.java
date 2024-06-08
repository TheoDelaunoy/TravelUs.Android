package com.example.travelusandroid.Models.Basics;

public class FlightInspirationParameters {
    private String departureCity;
    private int budget;
    private boolean hasStopovers;

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

    public boolean isHaveStopovers() {
        return hasStopovers;
    }

    public void setHasStopovers(boolean haveStopovers) {
        this.hasStopovers = haveStopovers;
    }
}
