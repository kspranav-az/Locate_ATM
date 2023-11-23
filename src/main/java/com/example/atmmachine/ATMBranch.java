package com.example.atmmachine;

// ATMBranch class
public class ATMBranch {
    private int branchId;
    private String branchName;
    private Address address;

    // Constructors, getters, and setters

    public ATMBranch(int branchId, String branchName, Address address) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.address = address;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
