package com.miui.earthquakewarning.model;

import java.io.Serializable;

public class LocationModel implements Serializable {
    private String city;
    private double latitude;
    private double longitude;
    private String place;

    public String getCity() {
        return this.city;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getPlace() {
        return this.place;
    }

    public void setCity(String str) {
        this.city = str;
    }

    public void setLatitude(double d2) {
        this.latitude = d2;
    }

    public void setLongitude(double d2) {
        this.longitude = d2;
    }

    public void setPlace(String str) {
        this.place = str;
    }
}
