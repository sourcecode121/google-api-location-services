package com.example.location;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by Anand on 18/11/2016.
 */

public class DetectedActivitiesIntentService extends IntentService {

    private static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent sendIntent = new Intent(Constants.BROADCAST_ACTION);
        ArrayList<DetectedActivity> activities = (ArrayList) result.getProbableActivities();
        sendIntent.putExtra(Constants.ACTIVITY_EXTRA, activities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
    }
}
