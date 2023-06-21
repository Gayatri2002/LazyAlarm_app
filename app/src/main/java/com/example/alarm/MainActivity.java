package com.example.alarm;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.alarm.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private static final String CHANNEL_ID = "alarm_channel";
    private static final CharSequence CHANNEL_NAME = "Alarm Channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createNotificationChannel();

        binding.selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Alarm Time")
                        .build();

                timePicker.show(getSupportFragmentManager(), "androidknowledge");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (timePicker.getHour() > 12) {
                            binding.selectTime.setText(
                                    String.format("%02d", (timePicker.getHour() - 12)) + ":" + String.format("%02d", timePicker.getMinute()) + "PM"
                            );
                        } else {
                            binding.selectTime.setText(timePicker.getHour() + ":" + timePicker.getMinute() + "AM");
                        }
                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                        calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                    }
                });
            }
        });

        binding.setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleAlarm(calendar.getTimeInMillis());
                Toast.makeText(MainActivity.this, "Alarm Set", Toast.LENGTH_SHORT).show();
            }
        });

        binding.cancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
                Toast.makeText(MainActivity.this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSnooze5Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snoozeAlarm(5);
            }
        });

        binding.btnSnooze15Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snoozeAlarm(15);
            }
        });

        binding.btnSnooze30Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snoozeAlarm(30);
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm_sound);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setDescription("Alarm Channel");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleAlarm(long alarmTime) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }

    private void cancelAlarm() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void snoozeAlarm(int snoozeMinutes) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ACTION_SNOOZE);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        if (alarmManager != null) {
            long snoozeTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
        }
    }
}
