package com.example.atmmachine;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class ATMBranch {
    private SimpleIntegerProperty branchId;
    private SimpleStringProperty branchName;
    private Address address;

    // Constructors, getters, and setters

    public ATMBranch(int branchId, String branchName, Address address) {
        this.branchId = new SimpleIntegerProperty(branchId);
        this.branchName = new SimpleStringProperty(branchName);
        this.address = address;
    }

    public int getBranchId() {
        return branchId.get();
    }

    public void setBranchId(int branchId) {
        this.branchId.set(branchId);
    }

    public SimpleIntegerProperty branchIdProperty() {
        return branchId;
    }

    public String getBranchName() {
        return branchName.get();
    }

    public void setBranchName(String branchName) {
        this.branchName.set(branchName);
    }

    public ObservableValue<String> branchNameProperty() {
        return branchName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
