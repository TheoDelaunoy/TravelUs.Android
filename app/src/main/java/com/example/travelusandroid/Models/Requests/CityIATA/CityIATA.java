package com.example.travelusandroid.Models.Requests.CityIATA;

import java.util.List;

public class CityIATA {
    private Meta meta;
    private List<LocationData> data;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        meta = meta;
    }

    public List<LocationData> getData() {
        return data;
    }

    public void setData(List<LocationData> data) {
        data = data;
    }
}
