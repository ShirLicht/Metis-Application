package com.example.admin.metis;

public class BarInfo {

    private String name;
    private double location_latitude, location_longitude;

    public BarInfo(String name, double location_latitude, double location_longitude){
        this.name = name;
        this.location_latitude = location_latitude;
        this.location_longitude = location_longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(double location_latitude) {
        this.location_latitude = location_latitude;
    }

    public double getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(double location_longitude) {
        this.location_longitude = location_longitude;
    }
}
