package com.djordjeratkovic.harmony.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

//clasa za ceo app da bude, doda se u manifest kao android:name=".application.App"
public class App extends Application {

    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
    public static final String PLANNER_CHANNEL_ID = "PLANNER_SERVICE_CHANNEL";
    public static final String TASK_CHANNEL_ID = "TASK_SERVICE_CHANNEL";

    public static boolean QR_ALARM = false;

    List<NotificationChannel> notificationChannels = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannnels();
    }

    private void createNotificationChannnels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationChannel plannerChannel = new NotificationChannel(
                    PLANNER_CHANNEL_ID,
                    "Planner Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationChannel taskChannel = new NotificationChannel(
                    TASK_CHANNEL_ID,
                    "TASK Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannels.add(serviceChannel);
            notificationChannels.add(plannerChannel);
            notificationChannels.add(taskChannel);

            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
            manager.createNotificationChannels(notificationChannels);
        }
    }
}
