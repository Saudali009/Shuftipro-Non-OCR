package com.shutipro.sdk.models;

import java.util.ArrayList;

public class DocumentVerification {

    private static DocumentVerification instance = null;
    private ArrayList<String> supportedTypes;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String documentNumber;
    private String expiryDate;
    private String issueDate;

    private DocumentVerification(){

    }

    public static DocumentVerification getInstance() {

        if (instance == null){
            instance = new DocumentVerification();
        }
        return instance;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
}
