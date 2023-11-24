package com.example.atmmachine;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

// City class
public class City {
    private int cityId;
    private SimpleStringProperty cityName;
    private SimpleStringProperty state;

    // Constructors, getters, and setters

    public City(String cityName, String state) {
        this.cityName = new SimpleStringProperty(cityName);
        this.state = new SimpleStringProperty(state);
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName.get();
    }

    public void setCityName(String cityName) {
        this.cityName.set(cityName);
    }

    public ObservableValue<String> cityNameProperty() {
        return cityName;
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public ObservableValue<String> stateProperty() {
        return state;
    }
}
