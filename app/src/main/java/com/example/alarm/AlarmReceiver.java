package com.example.alarm;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.media.MediaPlayer;
import android.net.Uri;

public class AlarmReceiver extends BroadcastReceiver {


    public static final String ACTION_SNOOZE = "com.example.alarm.AlarmReceiver";



    @Override
    public void onReceive(Context context, Intent intent) {

        // Play the custom audio file
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm_sound);
        MediaPlayer mediaPlayer = MediaPlayer.create(context, soundUri);
        mediaPlayer.start();


        Intent nextActivity = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_IMMUTABLE);




        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "androidknowledge")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Good Morning!")
                .setContentText("It's time to wake up")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri) // Set the sound for the notification
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(123, builder.build());
    }
}