package com.example.location;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by Anand on 18/11/2016.
 */

public class RecognitionActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private static final String TAG = RecognitionActivity.class.getSimpleName();

    private TextView details;
    private Button requestButton;
    private Button removeButton;
    private GoogleApiClient googleApiClient;
    private ActivityDetectionBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        setTitle(R.string.activity_recognition);

        details = (TextView) findViewById(R.id.activities);
        requestButton = (Button) findViewById(R.id.request_activities_button);
        removeButton = (Button) findViewById(R.id.remove_activities_button);

        broadcastReceiver = new ActivityDetectionBroadcastReceiver();

        googleApiClient = new GoogleApiClient.Builder(this)
                                .addApi(ActivityRecognition.API)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .build();

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!googleApiClient.isConnected()) {
                    Toast.makeText(RecognitionActivity.this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                    return;
                }
                ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                        googleApiClient,
                        Constants.DETECTION_INTERVAL,
                        getActivityDetectionPendingIntent()
                ).setResultCallback(RecognitionActivity.this);
                requestButton.setEnabled(false);
                removeButton.setEnabled(true);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!googleApiClient.isConnected()) {
                    Toast.makeText(RecognitionActivity.this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                    return;
                }
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                        googleApiClient,
                        getActivityDetectionPendingIntent()
                ).setResultCallback(RecognitionActivity.this);
                requestButton.setEnabled(true);
                removeButton.setEnabled(false);
                details.setText(R.string.recognized_activities);
            }
        });
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected() || googleApiClient.isConnecting()) {
            googleApiClient.disconnect();
        }
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected successfully");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error code " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.i(TAG, "Successfully added/removed activity detection");
        }
        else {
            Log.i(TAG, "Error in adding or removing activity detection: " + status.getStatusCode());
        }
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities = intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            String activityDetails = "";
            for (DetectedActivity thisActivity : updatedActivities) {
                activityDetails += getActivityString(thisActivity.getType()) + " " + thisActivity.getConfidence() + "%\n";
            }
            details.setText(activityDetails);
        }
    }

    public String getActivityString(int detectedActivityType) {
        Resources resources = this.getResources();
        switch (detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown_activity);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity);
        }
    }
}
