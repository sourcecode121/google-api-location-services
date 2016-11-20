package com.example.location;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anand on 19/11/2016.
 */

public class GeofenceIntentService extends IntentService {

    private static final String TAG = GeofenceIntentService.class.getSimpleName();

    public GeofenceIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Check for errors
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Error code: " + geofencingEvent.getErrorCode());
            return;
        }

        // Get geofence transition type
        int transitionType = geofencingEvent.getGeofenceTransition();

        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ||
                transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get list of triggering geofences
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Use helper method to get the notification string
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    transitionType,
                    triggeringGeofences
            );

            // Use helper method to show notification
            sendNotification(geofenceTransitionDetails);
        }
        else {
            Log.e(TAG, "Invalid transition type");
        }
    }

    private String getGeofenceTransitionDetails(int transitionType, List<Geofence> triggeringGeofences) {
        String transitionString = getTransitionString(transitionType);
        ArrayList triggeringGeofencesIds = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIds.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIds);
        return transitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) return "Enter";
        else return "Exit";
    }

    private void sendNotification(String notificationDetails) {
        Intent intent = new Intent(this, GeofenceActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(GeofenceActivity.class);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Click here to open Geofence Activity")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
