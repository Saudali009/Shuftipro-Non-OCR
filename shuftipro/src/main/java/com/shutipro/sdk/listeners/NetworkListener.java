package com.shutipro.sdk.listeners;

public interface NetworkListener {

    void successResponse(String result);
    void errorResponse(String reason);
}
