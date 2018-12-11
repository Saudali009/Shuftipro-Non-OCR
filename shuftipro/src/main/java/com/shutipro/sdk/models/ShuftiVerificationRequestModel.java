package com.shutipro.sdk.models;

import android.app.Activity;

import com.shutipro.sdk.listeners.ShuftiVerifyListener;


public class ShuftiVerificationRequestModel {
    private String clientId;
    private String secretKey;
    private String reference;
    private String country;
    private String language;
    private String email;
    private String callbackUrl;
    private String redirectUrl;
    private FaceVerification faceVerification;
    private DocumentVerification documentVerification;
    private AddressVerification addressVerification;
    private ConsentVerification consentVerification;

    private Activity parentActivity;
    private ShuftiVerifyListener shuftiVerifyListener;
    private boolean asyncRequest;

    public ShuftiVerificationRequestModel() {
    }

    public ShuftiVerificationRequestModel(String reference, String country, String language, String email, String callbackUrl, String redirectUrl,
                                          FaceVerification faceVerification, DocumentVerification documentVerification, AddressVerification addressVerification,
                                          ConsentVerification consentVerification, Activity parentActivity, ShuftiVerifyListener shuftiVerifyListener, boolean asyncRequest) {
        this.reference = reference;
        this.country = country;
        this.language = language;
        this.email = email;
        this.callbackUrl = callbackUrl;
        this.redirectUrl = redirectUrl;

        this.faceVerification = faceVerification;
        this.documentVerification = documentVerification;
        this.addressVerification = addressVerification;
        this.consentVerification = consentVerification;

        this.parentActivity = parentActivity;
        this.shuftiVerifyListener = shuftiVerifyListener;
        this.asyncRequest = asyncRequest;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public FaceVerification getFaceVerification() {
        return faceVerification;
    }

    public void setFaceVerification(FaceVerification faceVerification) {
        this.faceVerification = faceVerification;
    }

    public DocumentVerification getDocumentVerification() {
        return documentVerification;
    }

    public void setDocumentVerification(DocumentVerification documentVerification) {
        this.documentVerification = documentVerification;
    }

    public AddressVerification getAddressVerification() {
        return addressVerification;
    }

    public void setAddressVerification(AddressVerification addressVerification) {
        this.addressVerification = addressVerification;
    }

    public Activity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public ShuftiVerifyListener getShuftiVerifyListener() {
        return shuftiVerifyListener;
    }

    public void setShuftiVerifyListener(ShuftiVerifyListener shuftiVerifyListener) {
        this.shuftiVerifyListener = shuftiVerifyListener;
    }

    public boolean isAsyncRequest() {
        return asyncRequest;
    }

    public void setAsyncRequest(boolean asyncRequest) {
        this.asyncRequest = asyncRequest;
    }

    public ConsentVerification getConsentVerification() {
        return consentVerification;
    }

    public void setConsentVerification(ConsentVerification consentVerification) {
        this.consentVerification = consentVerification;
    }

}
