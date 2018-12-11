package com.shutipro.sdk;

import android.app.Activity;
import android.content.Intent;

import com.shutipro.sdk.activities.ShuftiVerifyActivity;
import com.shutipro.sdk.constants.Constants;
import com.shutipro.sdk.helpers.IntentHelper;
import com.shutipro.sdk.listeners.ShuftiVerifyListener;
import com.shutipro.sdk.models.AddressVerification;
import com.shutipro.sdk.models.ConsentVerification;
import com.shutipro.sdk.models.DocumentVerification;
import com.shutipro.sdk.models.FaceVerification;
import com.shutipro.sdk.models.ShuftiVerificationRequestModel;

public class Shuftipro {

    private static Shuftipro shuftipro = null;
    private String clientId;
    private String secretKey;
    private boolean async;

    private FaceVerification faceVerification;
    private DocumentVerification documentVerification;
    private AddressVerification addressVerification;
    private ConsentVerification consentVerification;

    private Shuftipro(String clientId, String secretKey, boolean async){
        this.clientId = clientId;
        this.secretKey = secretKey;
        this.async = async;
    }

    public static Shuftipro getInstance(String clientId, String secretKey, boolean async){
        if(shuftipro == null){
            shuftipro = new Shuftipro(clientId,secretKey,async);
        }

        return shuftipro;
    }

    public void shuftiproVerification(String reference, String country, String language, String email, String callbackUrl, String redirectUrl,
                                      FaceVerification faceVerification, DocumentVerification documentVerification, AddressVerification addressVerification,
                                      ConsentVerification consentVerification, Activity parentActivity, ShuftiVerifyListener shuftiVerifyListener){

        ShuftiVerificationRequestModel verificationRequestModel = new ShuftiVerificationRequestModel();
        verificationRequestModel.setClientId(clientId);
        verificationRequestModel.setSecretKey(secretKey);
        verificationRequestModel.setReference(reference);
        verificationRequestModel.setCountry(country);
        verificationRequestModel.setLanguage(language);
        verificationRequestModel.setEmail(email);
        verificationRequestModel.setCallbackUrl(callbackUrl);
        verificationRequestModel.setRedirectUrl(redirectUrl);

        verificationRequestModel.setFaceVerification(faceVerification);
        verificationRequestModel.setDocumentVerification(documentVerification);
        verificationRequestModel.setAddressVerification(addressVerification);
        verificationRequestModel.setConsentVerification(consentVerification);
        verificationRequestModel.setShuftiVerifyListener(shuftiVerifyListener);
        verificationRequestModel.setAsyncRequest(async);

        //Pas this request to the ShuftiVerification Activity
        IntentHelper.getInstance().insertObject(Constants.KEY_DATA_MODEL,verificationRequestModel);
        Intent intent = new Intent(parentActivity, ShuftiVerifyActivity.class);
        parentActivity.startActivity(intent);
    }
}
