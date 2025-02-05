package com.example.ctatracker;

public class VehicleLocation {
    String latitude;
    String longitude;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    VehicleLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
