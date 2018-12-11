package com.shutipro.sdk.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shuftipro.R;
import com.shutipro.sdk.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialFragment extends Fragment {
    private Button btnTakePicture;
    private ImageView ivTutorialImage;
    private Context mContext;
    private TutorialStateFragment tutorialStateFragment;
    private int curentState = 0;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private CountDownTimer countDownTimer;
    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 100;

    //Adding new views to handle the supported types of selection
    private TextView headingTextView, supportedTypesTextView, instructionHeadingTextView, instructionTextView;
    private boolean docIdCard = false, docPassport = false, docDrivingLicense = false, docCreditCard = false, addressIdCard = false,
            addressUtilityBills = false, addressBankStatement = false;

    private boolean printed = false, handwritten = false;
    //Adding runtime permissions
    private static final int REQUEST_CAMERA_PERMISSION = 110, REQUEST_WRITE_STORAGE = 111, REQUEST_AUDIO_RECORDING = 112;

    public static TutorialFragment newInstance(int currentState, boolean docIdCard, boolean docPassport, boolean docDrivingLicense, boolean docCreditCard,
                                               boolean addressIdCard, boolean addressUtilityBills, boolean addressBankStatement, boolean printed, boolean handwritten) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("currentState", currentState);
        bundle.putBoolean("docIdCard", docIdCard);
        bundle.putBoolean("docPassport", docPassport);
        bundle.putBoolean("docDrivingLicense", docDrivingLicense);
        bundle.putBoolean("docCreditCard", docCreditCard);
        bundle.putBoolean("addressIdCard", addressIdCard);
        bundle.putBoolean("addressUtilityBills", addressUtilityBills);
        bundle.putBoolean("addressBankStatement", addressBankStatement);
        bundle.putBoolean("printed", printed);
        bundle.putBoolean("handwritten", handwritten);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        mContext = getActivity();
        btnTakePicture = view.findViewById(R.id.btn_capture);
        ivTutorialImage = view.findViewById(R.id.iv_tut_image);

        //Handling newly added views to display supported types
        headingTextView = view.findViewById(R.id.headingTextView);
        supportedTypesTextView = view.findViewById(R.id.detailsTextView);
        instructionHeadingTextView = view.findViewById(R.id.instructionHeadingTextView);
        instructionTextView = view.findViewById(R.id.instructionTextView);


        //Get current state of tutorial
        Bundle bundle = getArguments();
        if (bundle != null) {
            curentState = bundle.getInt("currentState", 0);
            docIdCard = bundle.getBoolean("docIdCard", false);
            docPassport = bundle.getBoolean("docPassport", false);
            docDrivingLicense = bundle.getBoolean("docDrivingLicense", false);
            docCreditCard = bundle.getBoolean("docCreditCard", false);
            addressIdCard = bundle.getBoolean("addressIdCard", addressIdCard);
            addressUtilityBills = bundle.getBoolean("addressUtilityBills", false);
            addressBankStatement = bundle.getBoolean("addressBankStatement", false);
            printed = bundle.getBoolean("printed", false);
            handwritten = bundle.getBoolean("handwritten", false);
        }

        //Start the count down to open camera after seconds
        //startCountDown();

        //Handle take picture button click listener
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tutorialStateFragment != null) {

                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    tutorialStateFragment.captureVideo(curentState);
                }

            }
        });

        if (tutorialStateFragment != null) {
            curentState = tutorialStateFragment.getTypeofTutorial();
        }

        playTutorialSound();
        checkPermissions();

        /* if (checkPermissions()){
            playTutorialSound();
        }*/

        return view;
    }

    private boolean checkPermissions() {

        int permissionCamera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int permissionStorage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionRecording = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionRecording != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void setTutorialFragmentCB(TutorialStateFragment tutorialStateFragment) {
        this.tutorialStateFragment = tutorialStateFragment;
    }

    public void playTutorialSound() {
        switch (curentState) {
            case Constants.CODE_SELFIE:
                ivTutorialImage.setImageResource(R.drawable.face_image);
                headingTextView.setText("Face Verification");
                supportedTypesTextView.setVisibility(View.GONE);
                instructionHeadingTextView.setText("Capture your Face Video");
                instructionTextView.setText("Remove all accessories (e.g. glasses)");
                break;

            case Constants.CODE_DOCUMENT:
                ivTutorialImage.setImageResource(R.drawable.document_image);
                headingTextView.setText("Documentation Verification");
                supportedTypesTextView.setVisibility(View.VISIBLE);
                supportedTypesTextView.setText("Supported Types");
                instructionHeadingTextView.setText("Capture Document Video");
                instructionTextView.setText("Ensure the text is visible (hold document steady)");

                //Handling supported types selected
                supportedTypesTextView.setVisibility(View.VISIBLE);
                String supported_doc_types = getSelectedDocumentSupportedTypes();
                supportedTypesTextView.setText(supported_doc_types);
                break;

            case Constants.CODE_ADDRESS:
                ivTutorialImage.setImageResource(R.drawable.address_image);
                headingTextView.setText("Address Verification");
                supportedTypesTextView.setVisibility(View.VISIBLE);
                supportedTypesTextView.setText("Supported Types");
                instructionHeadingTextView.setText("Capture Address Document Video");
                instructionTextView.setText("Ensure the address is visible");

                //Handling supported types selected
                supportedTypesTextView.setVisibility(View.VISIBLE);
                String supported_add_types = getSelectedAddressSupportedTypes();
                supportedTypesTextView.setText(supported_add_types);
                break;

            case Constants.CODE_CONSENT:
                supportedTypesTextView.setVisibility(View.GONE);
                ivTutorialImage.setImageResource(R.drawable.consent_image);
                headingTextView.setText("Consent Verification");
                instructionHeadingTextView.setText("Capture Consent Document Video");
                instructionTextView.setText("Ensure the text is visible (hold document steady)");

                //Handling supported types selected
                supportedTypesTextView.setVisibility(View.VISIBLE);
                String supported_consent_types = getSelectedConsentSupportedTypes();
                supportedTypesTextView.setText(supported_consent_types);
                break;
        }

    }

    private String getSelectedDocumentSupportedTypes() {
        ArrayList<String> document_supported_types = new ArrayList<String>();
        if (docIdCard) {
            document_supported_types.add("ID Card");
        }
        if (docPassport) {
            document_supported_types.add("Passport");
        }
        if (docDrivingLicense) {
            document_supported_types.add("Driving Licence");
        }
        if (docCreditCard) {
            document_supported_types.add("Credit/Debit");
        }
        return "(" + TextUtils.join(",", document_supported_types) + ")";
    }


    private String getSelectedAddressSupportedTypes() {
        ArrayList<String> address_supported_types = new ArrayList<String>();

        String types = "(";
        if (addressIdCard) {
            address_supported_types.add("ID Card");
        }
        if (addressUtilityBills) {
            address_supported_types.add("Utility Bills");
        }
        if (addressBankStatement) {
            address_supported_types.add("Bank Statements");
        }
        return "(" + TextUtils.join(",", address_supported_types) + ")";
    }

    private String getSelectedConsentSupportedTypes() {
        ArrayList<String> consent_supported_types = new ArrayList<String>();

        String types = "(";
        if (printed) {
            consent_supported_types.add("Printed");
        }
        if (handwritten) {
            consent_supported_types.add("Handwritten");
        }
        return "(" + TextUtils.join(",", consent_supported_types) + ")";
    }


    private void startCountDown() {

        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(1300, 500) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {

                    if (TutorialFragment.this != null && tutorialStateFragment != null) {
                        tutorialStateFragment.captureVideo(curentState);
                    }
                }
            };

            countDownTimer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public interface TutorialStateFragment {
        int getTypeofTutorial();

        void captureVideo(int state);
    }
}
