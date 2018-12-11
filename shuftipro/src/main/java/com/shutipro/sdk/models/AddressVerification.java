package com.shutipro.sdk.models;

import java.util.ArrayList;

public class AddressVerification {

    private static AddressVerification instance = null;
    private ArrayList<String> supportedTypes;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullAddress;
    private boolean fuzzyMatch;

    private AddressVerification(){

    }

    public static AddressVerification getInstance() {
        if (instance == null){
            instance = new AddressVerification();
        }
        return instance;
    }

    public static void setInstance(AddressVerification instance) {
        AddressVerification.instance = instance;
    }

    public ArrayList<String> getSupportedTypes() {
        return supportedTypes;
    }

    public void setSupportedTypes(ArrayList<String> supportedTypes) {
        this.supportedTypes = supportedTypes;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public boolean isFuzzyMatch() {
        return fuzzyMatch;
    }

    public void setFuzzyMatch(boolean fuzzyMatch) {
        this.fuzzyMatch = fuzzyMatch;
    }
}
