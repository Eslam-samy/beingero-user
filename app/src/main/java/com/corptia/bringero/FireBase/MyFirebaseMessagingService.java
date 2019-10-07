package com.corptia.bringero.FireBase;
/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.corptia.bringero.R;
import com.corptia.bringero.view.splash.SplashActivity;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 * <p>
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 * <p>
 * <intent-filter>
 * <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String USER_TOKEN = "user_token";

    private static final String TAG = "MyFirebaseMsgService";

    public static final String MODEL_NOT = "model_not";


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String messageBody = "Hello!";
        Log.e(TAG, "onMessageReceived: YesssssssssssSSSSSSSSSSS" );
        sendNotification(messageBody);
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
    }

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        //     Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        //TODO send token to server
        Log.i(TAG, "sendRegistrationToServer: " + token);
        editSharedPref(USER_TOKEN, token);

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        PendingIntent pendingIntent;
        Intent newIntent = new Intent(this, SplashActivity.class);

        NotificationChannel notificationChannel;

        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, newIntent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_app);

//        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.beyond_doupt);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "CH_ID")
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.ic_notifications_none_black_24dp)
                .setContentTitle(getString(R.string.fcm_message))
                .setContentText(messageBody)
                .setAutoCancel(true)
                //               .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //          if (soundUri != null) {
            // Changing Default mode of notification
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            // Creating an Audio Attribute
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            // Creating Channel
            notificationChannel = new NotificationChannel("channel_0", "channel_0", NotificationManager.IMPORTANCE_HIGH);
            notificationBuilder.setChannelId("channel_0");

            notificationChannel.shouldShowLights();
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
//                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            //              notificationChannel.setSound(soundUri, audioAttributes);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        mNotificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    // }

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int not_id;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private void definePreferences() {
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }/////////////////

    private void editSharedPref(String name, String check) {
        definePreferences();
        editor.putString(name, check);
        editor.commit();
    }/////////////////

    private SharedPreferences getSharedPreferences() {
        definePreferences();
        return sharedPreferences;
    }/////////////////

    private SharedPreferences.Editor getEditor() {
        return editor;
    }/////////////////


}