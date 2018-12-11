package com.shutipro.sdk.listeners;

import java.io.File;

public interface VideoListener {
    void onVideoRecorded(File recordedFile, String encodedBase64String);
}
