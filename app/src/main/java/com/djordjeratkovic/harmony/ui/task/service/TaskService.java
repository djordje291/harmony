package com.djordjeratkovic.harmony.ui.task.service;

import static com.djordjeratkovic.harmony.application.App.TASK_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.djordjeratkovic.harmony.MainActivity;
import com.djordjeratkovic.harmony.R;

public class TaskService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                intent.getIntExtra(getString(R.string.id),2) + 1000, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, TASK_CHANNEL_ID)
                .setContentTitle(intent.getStringExtra(getString(R.string.title)))
                .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notifyMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMgr.notify(2, notification);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
