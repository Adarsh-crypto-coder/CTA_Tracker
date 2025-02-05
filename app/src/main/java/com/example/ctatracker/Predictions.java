package com.example.ctatracker;

public class Predictions {
    private String minutes;
    private String arrivalTime;
    private String vehicleId;
    private String distance;
    private boolean delayed;
    private String destination;
    private String direction;
    private String latitude;
    private String longitude;


    public String getDirection() {
        return direction;
    }

    public Predictions(String minutes, String arrivalTime, String vehicleId,
                       String distance, boolean delayed, String destination, String direction,String latitude, String longitude) {
        this.minutes = minutes;
        this.arrivalTime = arrivalTime;
        this.vehicleId = vehicleId;
        this.distance = distance;
        this.delayed = delayed;
        this.destination = destination;
        this.direction = direction;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getMinutes() { return minutes; }
    public String getArrivalTime() { return arrivalTime; }
    public String getVehicleId() { return vehicleId; }
    public String getDistance() { return distance; }
    public boolean isDelayed() { return delayed; }
    public String getDestination() { return destination; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
}
