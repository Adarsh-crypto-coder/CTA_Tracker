package com.example.ctatracker;

public class Stops {

    private String stpid;
    private String stpnm;
    private double lat;
    private double lon;
    private double distance;

    private double bearing;

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getBearing() {
        return bearing;
    }

    public static String getCardinalDirection(double bearing) {
        if (bearing >= 337.5 || bearing < 22.5) return "North";
        if (bearing >= 22.5 && bearing < 67.5) return "North East";
        if (bearing >= 67.5 && bearing < 112.5) return "East";
        if (bearing >= 112.5 && bearing < 157.5) return "South East";
        if (bearing >= 157.5 && bearing < 202.5) return "South";
        if (bearing >= 202.5 && bearing < 247.5) return "South West";
        if (bearing >= 247.5 && bearing < 292.5) return "West";
        return "Norht West";
    }


    public String getStpid() { return stpid; }
    public void setStpid(String stpid) { this.stpid = stpid; }

    public String getStpnm() { return stpnm; }
    public void setStpnm(String stpnm) { this.stpnm = stpnm; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }


}
