package com.example.travelusandroid.Models.Requests.CityIATA;

public class LocationData {
    private String type;
    private String subType;
    private String name;
    private String iataCode;
    private Address address;
    private GeoCode geoCode;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        subType = subType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        iataCode = iataCode;
    }

    public com.example.travelusandroid.Models.Requests.CityIATA.Address getAddress() {
        return address;
    }

    public void setAddress(com.example.travelusandroid.Models.Requests.CityIATA.Address address) {
        address = address;
    }

    public com.example.travelusandroid.Models.Requests.CityIATA.GeoCode getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(com.example.travelusandroid.Models.Requests.CityIATA.GeoCode geoCode) {
        geoCode = geoCode;
    }
}
