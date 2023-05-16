package com.djordjeratkovic.harmony.ui.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.ui.alarm.receiver.AlarmBroadcastReceiver;
import com.djordjeratkovic.harmony.ui.planner.PlannerAdapter;
import com.djordjeratkovic.harmony.ui.planner.listener.AlarmCreated;

import java.util.Calendar;

public class ResetPlanners {

    private AlarmCreated alarmCreated;
    private Context context;
    private int i;

    public ResetPlanners(Context context) {
        this.alarmCreated = PlannerAdapter.alarmCreated;
        this.context = context;

        //TODO: get shared pref time
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int i = preferences.getInt(context.getString(R.string.alarm_time), 0);
    }

    private void deletePlans() {
        alarmCreated.deleteAllPlannerReceivers();
    }

    public void addPlans() {
        //todo: get the time
        //todo: delete frist old ones
//        deletePlans();
        alarmCreated.schedulePlanners(i);
    }

    public void scheduleAnotherAlarm() {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        int h = i / 60;
        int m = i % 60;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmPendingIntent
        );
    }
}
