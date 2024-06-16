package com.example.travelusandroid.Models.Basics;

import java.io.Serializable;

public class DatabaseAirport implements Serializable {
    private int id;
    private String iata;
    private String airports;
    private String city;
    private String country;
    private String englishCity;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getIata() {
        return iata;
    }
    public void setIata(String iata) {
        this.iata = iata;
    }
    public String getAirports() {
        return airports;
    }
    public void setAirports(String airports) {
        this.airports = airports;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getEnglishCity() {
        return englishCity;
    }
    public void setEnglishCity(String englishCity) {
        this.englishCity = englishCity;
    }
}
