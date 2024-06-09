package com.example.travelusandroid.Models.Requests;

import com.example.travelusandroid.Models.Requests.FlightAnywhere.*;

import java.util.List;

public class AmadeusFlightAnywhere {

    private List<Datum> data;
    private Dictionaries dictionaries;
    private Meta meta;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Dictionaries getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(Dictionaries dictionaries) {
        this.dictionaries = dictionaries;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
