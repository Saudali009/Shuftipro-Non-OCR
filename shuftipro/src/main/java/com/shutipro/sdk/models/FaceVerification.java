package com.shutipro.sdk.models;

public class FaceVerification {

    private static FaceVerification instance = null;

    private FaceVerification(){

    }

    public static FaceVerification getInstance() {

        if (instance == null){
            instance = new FaceVerification();
        }
        return instance;
    }

    private boolean faceVerification;

    public boolean isFaceVerification() {
        return faceVerification;
    }

    public void setFaceVerification(boolean faceVerification) {
        this.faceVerification = faceVerification;
    }
}
