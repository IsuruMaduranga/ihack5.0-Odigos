package com.cwhq.odigos.Models;



public class UserLocation {
    private double latitude;
    private double longitude;


    public UserLocation(){

    }


    public UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public void setLatitude(double lat){
        this.latitude=lat;
    }

    public void setLongitude(double lon){
        this.longitude=lon;
    }

}
