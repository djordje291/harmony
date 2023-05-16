package com.djordjeratkovic.harmony.ui.task.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.ui.task.service.TaskService;

public class TaskBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startTaskService(context, intent);
    }

    private void startTaskService(Context context, Intent intent) {
        Intent intentService = new Intent(context, TaskService.class);

        intentService.putExtra(context.getString(R.string.title),
                intent.getStringExtra(context.getString(R.string.title)));
        intentService.putExtra(context.getString(R.string.id),
                intent.getIntExtra(context.getString(R.string.id),2));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
