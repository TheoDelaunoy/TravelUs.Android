package com.example.travelusandroid.Models.Requests.FlightAnywhere;

public class Datum {
    private String type;
    private String origin;
    private String destination;
    private String departureDate;
    private String returnDate;
    private Price price;
    private Links links;

    public String getType() {
        return type;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public Price getPrice() {
        return price;
    }

    public Links getLinks() {
        return links;
    }
}
