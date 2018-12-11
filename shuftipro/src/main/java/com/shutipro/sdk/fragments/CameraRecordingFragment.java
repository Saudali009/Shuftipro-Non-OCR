package com.shutipro.sdk.fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shuftipro.R;
import com.shutipro.sdk.constants.CameraConfig;
import com.shutipro.sdk.constants.Constants;
import com.shutipro.sdk.custom_views.CameraPreview;
import com.shutipro.sdk.listeners.VideoListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * A simple {@link Fragment} subclass.
 */

public class CameraRecordingFragment extends Fragment implements MediaRecorder.OnInfoListener {

    private static final String TAG = CameraRecordingFragment.class.getSimpleName();
    private static final int FOCUS_AREA_SIZE = 500;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private CountDownTimer countDownTimer;
    private TextView countDownTextView;
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private static boolean cameraFront = false;
    private static boolean flash = false;
    private long countUp;
    private Context mContext;
    private int quality = CamcorderProfile.QUALITY_720P;
    private ImageView capture, switchCamera, buttonFlash, chronoRecordingImage;
    private LinearLayout cameraPreview;
    private Chronometer chronometer;
    private String storagePath;
    private VideoListener videoStateListener;

    public CameraRecordingFragment() {

    }

    public static CameraRecordingFragment newInstance(boolean isToOpenFrontFacingCamera) {
        CameraRecordingFragment f = new CameraRecordingFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_TO_OPEN_FRONT_CAM, isToOpenFrontFacingCamera);
        f.setArguments(bundle);
        return f;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_camera, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = getActivity();
        capture = view.findViewById(R.id.button_capture);
        switchCamera = view.findViewById(R.id.button_ChangeCamera);
        cameraPreview = view.findViewById(R.id.camera_preview);
        buttonFlash = view.findViewById(R.id.buttonFlash);
        chronoRecordingImage = view.findViewById(R.id.chronoRecordingImage);
        chronometer = view.findViewById(R.id.textChrono);
        countDownTextView = view.findViewById(R.id.countDownTextView);

        //Fetch the data
        Bundle bundle = getArguments();
        if (bundle != null) {
            cameraFront = bundle.getBoolean(Constants.IS_TO_OPEN_FRONT_CAM, false);
        }

        initialize();

        return view;
    }

    public void setVideoStateListener(VideoListener videoStateListener) {
        this.videoStateListener = videoStateListener;
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!isDeviceHasCamera(mContext)) {
            Toast toast = Toast.makeText(mContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            getActivity().finish();
        }
        if (mCamera == null) {

            releaseCamera();
            final boolean frontal = cameraFront;
            int cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                switchCameraListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "No front facing camera found.", Toast.LENGTH_LONG).show();
                    }
                };

                cameraId = findBackFacingCamera();
                if (flash) {
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    buttonFlash.setImageResource(R.drawable.ic_flash_on_white);
                }
            } else if (!frontal) {
                cameraId = findBackFacingCamera();
                if (flash) {
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    buttonFlash.setImageResource(R.drawable.ic_flash_on_white);
                }
            }

            mCamera = Camera.open(cameraId);
            mPreview.refreshCamera(mCamera);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void initialize() {

        mPreview = new CameraPreview(mContext, mCamera);
        cameraPreview.addView(mPreview);

        capture.setOnClickListener(videoCaptureListener);

        switchCamera.setOnClickListener(switchCameraListener);

        buttonFlash.setOnClickListener(flashListener);


        cameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        focusOnTouch(event);
                    } catch (Exception e) {
                        Log.i(TAG, "Fail when camera try autofocus");
                    }
                }
                return true;
            }
        });

    }

    View.OnClickListener flashListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording && !cameraFront) {
                if (flash) {
                    flash = false;
                    buttonFlash.setImageResource(R.drawable.ic_flash_on_white);
                    setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                } else {
                    flash = true;
                    buttonFlash.setImageResource(R.drawable.ic_flash_off_white);
                    setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
            }
        }
    };

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(mContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            switchCamera.setImageResource(R.drawable.ic_camera_front);
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                switchCamera.setImageResource(R.drawable.ic_camera_rear);
                mCamera = Camera.open(cameraId);
                if (flash) {
                    flash = false;
                    buttonFlash.setImageResource(R.drawable.ic_flash_off_white);
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private boolean isDeviceHasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    boolean recording = false;
    View.OnClickListener videoCaptureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (recording) {

                try {
                    mediaRecorder.stop();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                stopCountDownTimer();
                changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                releaseMediaRecorder();
                recording = false;
                String encodedStringBase64 = videoToBase64(new File(storagePath));

                if (videoStateListener != null) {
                    videoStateListener.onVideoRecorded(new File(storagePath), encodedStringBase64);
                }

            } else {

                capture.setClickable(false);
                if (!prepareMediaRecorder()) {
                    Toast.makeText(mContext, "Device is unable to record video.", Toast.LENGTH_LONG).show();
                    capture.setClickable(true);
                    if (getActivity() != null) {
                        getActivity().finish();
                    } else {
                        return;
                    }
                }

                try {
                    mediaRecorder.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        capture.setClickable(true);
                    }
                }, 1000);

                startCountDownTimer();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                capture.setImageResource(R.drawable.button_red_capture);
                recording = true;
            }
        }
    };

    private void startCountDownTimer() {
        countDownTextView.setVisibility(View.VISIBLE);
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(11000, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished < 1950) {
                        countDownTextView.setText("00:00");
                        onFinish();
                    }
                    String timeAs = "00" + ":" + String.format("%02d", millisUntilFinished / 1000);
                    countDownTextView.setText(timeAs);
                }

                public void onFinish() {
                    countDownTextView.setVisibility(View.GONE);
                    releaseMediaRecorder();
                    recording = false;
                    String encodedStringBase64 = videoToBase64(new File(storagePath));
                    if (videoStateListener != null) {
                        videoStateListener.onVideoRecorded(new File(storagePath), encodedStringBase64);
                    }
                }
            };
        }

        countDownTimer.start();

    }

    private void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTextView.setVisibility(View.GONE);
        }
    }

    private void changeRequestedOrientation(int orientation) {
        getActivity().setRequestedOrientation(orientation);
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            if(mCamera != null){
                mCamera.lock();
            }
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        /*
         * Adding Additional Camcoder check if front camera do not support HIGH quality.
         * Set the default quality.
         * Additional quality issue check for the latest android version.
         */

        CamcorderProfile profile = null;
        if (!cameraFront) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        } else {
            if (CamcorderProfile.hasProfile(0, CamcorderProfile.QUALITY_HIGH)) {
                profile = CamcorderProfile.get(0, CamcorderProfile.QUALITY_HIGH);
            } else {
                profile = CamcorderProfile.get(0, CamcorderProfile.QUALITY_LOW);
            }
        }

        mCamera.unlock();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (cameraFront) {
                mediaRecorder.setOrientationHint(270);
            } else {
                mediaRecorder.setOrientationHint(90);
            }
        }

        //ADDING ADDITIONAL ATTRIBUTES TO RECORD VIDEO
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);

        //mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        //mediaRecorder.setOutputFile(getOutPath(MEDIA_TYPE_VIDEO));
        //mediaRecorder.setVideoEncodingBitRate(690000);  //Encription or compression of recorded video.
        //mediaRecorder.setVideoFrameRate(30);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mediaRecorder.setVideoEncoder(profile.videoCodec);
        mediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mediaRecorder.setVideoFrameRate(profile.videoFrameRate);

        try {
            mediaRecorder.setOutputFile(createVideoFile());
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            Toast.makeText(mContext, "Unable to create file.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        mediaRecorder.setVideoEncodingBitRate(3000000);
        //mediaRecorder.setVideoSize(1280, 720);

        mediaRecorder.setVideoSize(640, 480);
        mediaRecorder.setMaxDuration(CameraConfig.MAX_DURATION_RECORD); // Set max duration 10 sec.
        mediaRecorder.setMaxFileSize(CameraConfig.MAX_FILE_SIZE_RECORD); // Set max file size 20M.
        mediaRecorder.setOnInfoListener(this);

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void setFlashMode(String mode) {

        try {
            if (getActivity().getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)
                    && mCamera != null
                    && !cameraFront) {

                mPreview.setFlashMode(mode);
                mPreview.refreshCamera(mCamera);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Exception changing flashLight mode",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void reset() {
        flash = false;
        cameraFront = false;
    }

    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null) {

            Camera.Parameters parameters = mCamera.getParameters();

            if (parameters.getMaxNumMeteringAreas() > 0) {
                Rect rect = calculateFocusArea(event.getX(), event.getY());
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);
                mCamera.setParameters(parameters);
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            } else {
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
            } else {
                // do something...
            }
        }
    };

    private String getOutPath(int mediaType) {
        String outputPath = null;
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), Constants.VIDEO_DIRECTORY);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        if (mediaType == MEDIA_TYPE_IMAGE) {
            outputPath = mediaStorageDir.getPath() + File.separator +
                    Constants.OUTPUT_IMAGE_PREFIX + timeStamp + Constants.IMAGE_EXTENSION;
        } else if (mediaType == MEDIA_TYPE_VIDEO) {
            outputPath = mediaStorageDir.getPath() + File.separator +
                    Constants.OUTPUT_VIDEO_PREFIX + timeStamp + Constants.VIDEO_EXTENSION;
        } else {
            return null;
        }
        storagePath = outputPath;
        return outputPath;
    }

    private String createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "MP4_" + timeStamp + "_";

        File directory = mContext.getCacheDir();

        File videoFile = File.createTempFile(
                videoFileName,
                ".mp4",
                directory
        );
        videoFile.deleteOnExit();
        return storagePath = videoFile.getPath();
    }

    private String videoToBase64(File file) {
        String encodedString = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        encodedString = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return encodedString;
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            Log.e(TAG, "Maximum File size Reached");
            maxLimitReached();
        }

        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.e(TAG, "Maximum Duration Reached");
            maxLimitReached();
        }


        if (what == MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN) {
            Log.e(TAG, "Unknown error has occur.");
        }
    }

    private void maxLimitReached() {
        try {
            mediaRecorder.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        stopCountDownTimer();
        changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        releaseMediaRecorder();
        recording = false;
        String encodedStringBase64 = videoToBase64(new File(storagePath));

        if (videoStateListener != null) {
            videoStateListener.onVideoRecorded(new File(storagePath), encodedStringBase64);
        }
    }
}