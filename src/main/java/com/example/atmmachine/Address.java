package com.example.atmmachine;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

// Address class
public class Address {
    private int addressId;
    private SimpleStringProperty street;
    private String zipCode;
    private City city;

    // Constructors, getters, and setters

    public Address(String street, String zipCode, City city) {
        this.street = new SimpleStringProperty(street);
        this.zipCode = zipCode;
        this.city = city;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getStreet() {
        return street.get();
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public ObservableValue<String> streetProperty() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
