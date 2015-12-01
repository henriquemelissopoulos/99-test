package com.henriquemelissopoulos.igot99problemsbutanappaintone.model;

/**
 * Created by h on 01/12/15.
 */
public class Taxi {

    private double latitude;
    private double longitude;
    private int driverId;
    private boolean driverAvailable;


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public boolean isDriverAvailable() {
        return driverAvailable;
    }

    public void setDriverAvailable(boolean driverAvailable) {
        this.driverAvailable = driverAvailable;
    }
}
