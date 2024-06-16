package com.example.travelusandroid.Models.Basics;

import android.os.Parcel;
import android.os.Parcelable;

public class FlightInspirationParameters implements Parcelable {
    private String departureCity;
    private int budget;
    private boolean hasStopovers;
    private String cityIata;

    public FlightInspirationParameters(){
        this.departureCity = null;
        this.budget = 0;
        this.cityIata= null;
        this.hasStopovers = false;
    }

    protected FlightInspirationParameters(Parcel in) {
        departureCity = in.readString();
        budget = in.readInt();
        hasStopovers = in.readByte() != 0;
        cityIata = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(departureCity);
        dest.writeInt(budget);
        dest.writeByte((byte) (hasStopovers ? 1 : 0));
        dest.writeString(cityIata);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FlightInspirationParameters> CREATOR = new Creator<FlightInspirationParameters>() {
        @Override
        public FlightInspirationParameters createFromParcel(Parcel in) {
            return new FlightInspirationParameters(in);
        }

        @Override
        public FlightInspirationParameters[] newArray(int size) {
            return new FlightInspirationParameters[size];
        }
    };

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
