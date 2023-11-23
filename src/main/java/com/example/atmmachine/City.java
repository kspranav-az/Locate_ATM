package com.example.atmmachine;

// City class
public class City {
    private int cityId;
    private String cityName;
    private String state;

    // Constructors, getters, and setters

    public City(String cityName, String state) {
        this.cityName = cityName;
        this.state = state;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
