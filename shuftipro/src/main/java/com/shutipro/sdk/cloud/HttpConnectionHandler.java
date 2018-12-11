package com.shutipro.sdk.cloud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.shutipro.sdk.activities.ShuftiVerifyActivity;
import com.shutipro.sdk.constants.Constants;
import com.shutipro.sdk.helpers.IntentHelper;
import com.shutipro.sdk.listeners.NetworkListener;
import com.shutipro.sdk.models.ShuftiVerificationRequestModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class HttpConnectionHandler {

    private static HttpConnectionHandler instance = null;
    private boolean errorOccured = true;
    private String TAG = HttpConnectionHandler.class.getSimpleName();
    private static final String SHUFTIPRO_API_URL = "https://shuftipro.com/api/";
    private static final String SDK_STACKTRACE_URL = "https://shuftipro.com/api/v3/sdk/error/report/";
    private String CLIENT_ID;
    private String SECRET_KEY;
    private InputStream inputStream = null;
    private boolean asyncRequest;
    private ShuftiVerificationRequestModel shuftiVerificationRequestModel;
    HashMap<String, String> responseSet;

    public HttpConnectionHandler(String clientId, String secretKey, boolean asyncRequest) {
        this.CLIENT_ID = clientId;
        this.SECRET_KEY = secretKey;
        this.asyncRequest = asyncRequest;
    }

    public static HttpConnectionHandler getInstance(String clientId, String secretKey, boolean asyncRequest) {

        if (instance == null) {
            instance = new HttpConnectionHandler(clientId, secretKey, asyncRequest);
        }
        return instance;
    }

    @SuppressLint("StaticFieldLeak")
    public boolean executeVerificationRequest(final JSONObject requestedObject, final
    NetworkListener networkListener, final Context context) {
        Log.e(TAG, requestedObject.toString());
        if (networkAvailable(context)) {

            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... voids) {
                    String resultResponse = "";
                    try {
                        URL url = new URL(SHUFTIPRO_API_URL);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setAllowUserInteraction(false);
                        connection.setRequestProperty("Connection", "Keep-Alive");
                        connection.setConnectTimeout(900000);
                        connection.setReadTimeout(900000);

                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        connection.setRequestProperty("Accept", "application/json");

                        String cred = basic(CLIENT_ID, SECRET_KEY);
                        connection.setRequestProperty("Authorization", cred);
                        connection.connect();

                        DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                        os.writeBytes(requestedObject.toString());

                        os.flush();
                        os.close();

                        int responseCode = ((HttpURLConnection) connection).getResponseCode();
                        if ((responseCode >= HttpURLConnection.HTTP_OK)
                                && responseCode < 300) {
                            inputStream = connection.getInputStream();
                            errorOccured = false;
                            resultResponse = inputStreamToString(inputStream);
                            Log.d(TAG, "Response : " + resultResponse);
                        } else if (responseCode == 400) {
                            inputStream = connection.getErrorStream();
                            errorOccured = true;
                            resultResponse = inputStreamToString(inputStream);
                            Log.e(TAG, "Response : " + resultResponse);
                        } else if (responseCode == 401) {
                            inputStream = connection.getErrorStream();
                            errorOccured = true;
                            resultResponse = inputStreamToString(inputStream);
                            Log.e(TAG, "Response : " + resultResponse);
                        } else if (responseCode == 402) {
                            inputStream = connection.getErrorStream();
                            errorOccured = true;
                            resultResponse = inputStreamToString(inputStream);
                            Log.e(TAG, "Response : " + resultResponse);
                        } else if (responseCode == 403) {
                            inputStream = connection.getErrorStream();
                            errorOccured = true;
                            resultResponse = inputStreamToString(inputStream);
                            Log.e(TAG, "Response : " + resultResponse);
                        } else if (responseCode == 404) {
                            inputStream = connection.getErrorStream();
                            errorOccured = true;
                            resultResponse = inputStreamToString(inputStream);
                            Log.e(TAG, "Response : " + resultResponse);
                        } else if (responseCode == 409) {
                            inputStream = connection.getErrorStream();
                            errorOccured = true;
                            resultResponse = inputStreamToString(inputStream);
                            Log.e(TAG, "Response : " + resultResponse);
                        } else if (responseCode == 503) {
                            inputStream = connection.getErrorStream();
                            errorOccured = true;
                            resultResponse = inputStreamToString(inputStream);
                            Log.e(TAG, "Response : " + resultResponse);
                        } else {
                            inputStream = connection.getErrorStream();
                            errorOccured = true;
                            resultResponse = inputStreamToString(inputStream);
                            Log.e(TAG, "Response : " + resultResponse);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        resultResponse = e.getMessage();
                        return resultResponse;
                    }
                    return resultResponse;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    //If user has already kill the request do not send him response of previous request.
                    if (ShuftiVerifyActivity.getInstance() == null && !asyncRequest) {
                        return;
                    }
                    if (ShuftiVerifyActivity.getInstance() != null && !asyncRequest) {

                        if (networkListener != null) {
                            if (!errorOccured) {
                                networkListener.successResponse(result);
                            } else {
                                networkListener.errorResponse(result);
                            }
                        }
                    } else {

                        //This is an async request check the VerificationListener class listener and return response
                        if (IntentHelper.getInstance().containsKey(Constants.KEY_DATA_MODEL)) {
                            shuftiVerificationRequestModel = (ShuftiVerificationRequestModel) IntentHelper.getInstance().getObject(Constants.KEY_DATA_MODEL);
                            sendCallbackToCallerActivity(result);
                        }
                    }
                }

            }.execute();
            return true;

        } else {
            return false;
        }
    }

    private void sendCallbackToCallerActivity(String response) {

        responseSet = new HashMap<>();
        responseSet.clear();
        if (response == null) {

            Log.e(TAG, "Response is null");
        }

        populateMap(response);
    }

    private void populateMap(String response) {

        //Putting response in hash map
        try {

            JSONObject jsonObject = new JSONObject(response);

            //Starting api response parsing
            String reference = "";
            String event = "";
            String error = "";
            String verification_url = "";
            String verification_result = "";
            String verification_data = "";
            String declined_reason = "";

            if (jsonObject.has("reference")) {
                try {
                    reference = jsonObject.getString("reference");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("event")) {
                try {
                    event = jsonObject.getString("event");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("error")) {
                try {
                    JSONObject errorObject = new JSONObject(jsonObject.getString("error"));
                    error = errorObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("verification_url")) {
                try {
                    verification_url = jsonObject.getString("verification_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("verification_result")) {
                try {
                    verification_result = jsonObject.getString("verification_result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("verification_data")) {
                try {
                    verification_data = jsonObject.getString("verification_data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonObject.has("declined_reason")) {
                try {
                    declined_reason = jsonObject.getString("declined_reason");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //Putting response in hash map
            responseSet.put("reference", reference);
            responseSet.put("event", event);
            responseSet.put("error", error);
            responseSet.put("verification_url", verification_url);
            responseSet.put("verification_result", verification_result);
            responseSet.put("verification_data", verification_data);
            responseSet.put("declined_reason", declined_reason);

            if (shuftiVerificationRequestModel != null && shuftiVerificationRequestModel.getShuftiVerifyListener() != null) {
                shuftiVerificationRequestModel.getShuftiVerifyListener().verificationStatus(responseSet);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static boolean networkAvailable(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;

            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }

        if (!haveConnectedWifi)
            Log.e("NetworkInfo", "No WIFI Available");
        else
            Log.e("NetworkInfo", "WIFI Available");
        if (!haveConnectedMobile)
            Log.e("NetworkInfo", "No Mobile Network Available");
        else
            Log.e("NetworkInfo", "Mobile Network Available");

        return haveConnectedWifi || haveConnectedMobile;
    }

    private static String basic(String username, String password) {
        String usernameAndPassword = username + ":" + password;
        String encoded = Base64.encodeToString((usernameAndPassword).getBytes(), Base64.NO_WRAP);
        return "Basic " + encoded;
    }

    private static String inputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        reader.close();
        return out.toString();
    }

    @SuppressLint("StaticFieldLeak")
    public void sendStacktraceReport(final Context context, final String clientId, final String threadName, final String stackTrace,
                                     final String message, final String deviceInformation, final String timeStamp) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                String resultResponse = "";
                try {
                    URL url = new URL(SDK_STACKTRACE_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setAllowUserInteraction(false);
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setConnectTimeout(9000);
                    connection.setReadTimeout(9000);

                    connection.setRequestMethod("POST");

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("clientId", clientId)
                            .appendQueryParameter("threadName", threadName)
                            .appendQueryParameter("stackTrace", stackTrace)
                            .appendQueryParameter("message", message)
                            .appendQueryParameter("deviceInformation", deviceInformation)
                            .appendQueryParameter("timeStamp", timeStamp);

                    String query = builder.build().getEncodedQuery();

                    byte[] outputBytes = query.getBytes("UTF-8");

                    OutputStream os = connection.getOutputStream();
                    os.write(outputBytes);
                    os.close();

                    connection.connect();
                    int responseCode = ((HttpURLConnection) connection).getResponseCode();
                    if ((responseCode >= HttpURLConnection.HTTP_OK)
                            && responseCode < 300) {
                        inputStream = connection.getInputStream();
                        ((Activity)context).finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return resultResponse;
                }
                return resultResponse;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ShuftiVerifyActivity.getInstance().finish();
            }

        }.execute();
    }
}
