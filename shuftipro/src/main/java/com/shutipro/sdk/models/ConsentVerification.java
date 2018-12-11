package com.shutipro.sdk.models;

import java.util.ArrayList;

public class ConsentVerification {

    private static ConsentVerification instance = null;
    private ArrayList<String> supportedTypes;
    private String consentText;

    private ConsentVerification() {

    }

    public static ConsentVerification getInstance() {
        if (instance == null) {
            instance = new ConsentVerification();
        }
        return instance;
    }

    public ArrayList<String> getSupportedTypes() {
        return supportedTypes;
    }

    public void setSupportedTypes(ArrayList<String> supportedTypes) {
        this.supportedTypes = supportedTypes;
    }

    public String getConsentText() {
        return consentText;
    }

    public void setConsentText(String consentText) {
        this.consentText = consentText;
    }
}
