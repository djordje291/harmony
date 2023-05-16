package com.djordjeratkovic.harmony.ui.planner.service;

import static com.djordjeratkovic.harmony.application.App.PLANNER_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.djordjeratkovic.harmony.MainActivity;
import com.djordjeratkovic.harmony.R;

public class PlannerService extends Service {

    private final String FORMAT = "%02d:%02d";
    private Context context;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                intent.getIntExtra(getString(R.string.id), 7), notificationIntent, 0);

        Log.d("PlannerTAG", "service: " + intent.getStringExtra(context.getString(R.string.title)));


        int i = intent.getIntExtra(getString(R.string.duration),0);
//        String contentText =String.format(FORMAT, i / 60, i % 60) + "\n" + intent.getStringExtra(getString(R.string.note));

        String title = intent.getStringExtra(getString(R.string.title)) + " (" + i + "min)";

        Notification notification = new NotificationCompat.Builder(this, PLANNER_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(intent.getStringExtra(getString(R.string.note)))
                .setSmallIcon(R.drawable.ic_baseline_today_24)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager notifyMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMgr.notify(7, notification);

//        scheduleAnotherAlarm(intent);
        return START_STICKY;
    }

//    private void scheduleAnotherAlarm(Intent intent) {
//        Intent broadcastIntent = new Intent(context, PlannerBroadcastReceiver.class);
//        PendingIntent pIntent = PendingIntent.getBroadcast(context,
//                intent.getIntExtra(getString(R.string.id), 7),
//                broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        schedulePlanNotification(intent, pIntent);
//    }
//
//    private void schedulePlanNotification(Intent i, PendingIntent p) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        alarmManager.cancel(p);
//
//        Intent intent = new Intent(context, PlannerBroadcastReceiver.class);
//        intent.putExtra(context.getString(R.string.id), i.getIntExtra(getString(R.string.id), 7));
//        intent.putExtra(context.getString(R.string.title),i.getStringExtra(getString(R.string.title)));
//        intent.putExtra(context.getString(R.string.duration), i.getIntExtra(getString(R.string.duration), 0));
//        intent.putExtra(context.getString(R.string.note), i.getStringExtra(getString(R.string.note)));
//
//
//        PendingIntent plannerPendingIntent = PendingIntent.getBroadcast(context, i.getIntExtra(getString(R.string.id), 7), intent, 0);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
//
//        alarmManager.setExact(
//                AlarmManager.RTC_WAKEUP,
//                calendar.getTimeInMillis(),
//                plannerPendingIntent
//        );
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
