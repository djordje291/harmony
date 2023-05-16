package com.djordjeratkovic.harmony.ui.planner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.ui.planner.service.PlannerService;

public class PlannerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startPlannerService(context, intent);
    }

    private void startPlannerService(Context context, Intent intent) {
        Intent intentService = new Intent(context, PlannerService.class);

        Bundle bundle = intent.getExtras();

        Log.d("PlannerTAG", "receiver: " + intent.getStringExtra(context.getString(R.string.title)));
        Log.d("PlannerTAG", "receiver: " + bundle.getString(context.getString(R.string.title)));

        intentService.putExtra(context.getString(R.string.title), intent.getStringExtra(context.getString(R.string.title)));
        intentService.putExtra(context.getString(R.string.id), intent.getIntExtra(context.getString(R.string.id), 7));
        intentService.putExtra(context.getString(R.string.duration), intent.getIntExtra(context.getString(R.string.duration), 0));
        intentService.putExtra(context.getString(R.string.note), intent.getStringExtra(context.getString(R.string.note)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
