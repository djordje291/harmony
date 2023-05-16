package com.djordjeratkovic.harmony.ui.alarm;

import static com.djordjeratkovic.harmony.application.App.QR_ALARM;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.ui.alarm.listener.ToRefreshFragment;
import com.djordjeratkovic.harmony.ui.alarm.receiver.AlarmBroadcastReceiver;
import com.djordjeratkovic.harmony.ui.planner.PlannerAdapter;
import com.djordjeratkovic.harmony.ui.planner.listener.AlarmCreated;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class NewAlarmDialog extends DialogFragment implements View.OnClickListener {

    private ImageButton cancel, add;
    private NumberPicker hours, minutes;
    private SwitchCompat qr;

    private Context context;

    private AlarmCreated alarmCreated;

    private ToRefreshFragment toRefreshFragment;

    public NewAlarmDialog(Context context, ToRefreshFragment toRefreshFragment) {
        this.context = context;
        this.toRefreshFragment = toRefreshFragment;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_alarm, null);

        alarmCreated = PlannerAdapter.alarmCreated;

        cancel = view.findViewById(R.id.cancelNewAlarm);
        add = view.findViewById(R.id.addNewAlarm);
        hours = view.findViewById(R.id.hoursNewAlarm);
        minutes = view.findViewById(R.id.minutesNewAlarm);
        qr = view.findViewById(R.id.qrSwitch);

        cancel.setOnClickListener(this);
        add.setOnClickListener(this);

        hours.setMinValue(0);
        minutes.setMinValue(0);
        hours.setMaxValue(23);
        minutes.setMaxValue(59);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        //dajes ga dialogu da bi dialog mogao tastaturu da dobije
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewAlarm:
                int h = hours.getValue();
                int m = minutes.getValue();
                int time = h * 60 + m;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.alarm_time), time);
                QR_ALARM = qr.isChecked();
                editor.apply();
                scheduleAlarm(h, m);
                //TODO: delete this
                if (alarmCreated != null) {
                    alarmCreated.schedulePlanners(time);
                }
                dismiss();
                if (toRefreshFragment != null) {
                    toRefreshFragment.refresh();
                }
                break;
            case R.id.cancelNewAlarm:
                dismiss();
                break;
        }
    }

    //za pravljenje intenta za alarm
    private void scheduleAlarm(int h, int m) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        //TODO: nece repeating da radi jebeno, jer je automatski inexact sto znaci da ceka jos neki proces,
        //moraces da setExact pozivas svaki put kad se alarm aktivira i da napravis nov pomocu listenera nekog
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmPendingIntent
        );
//        final long RUN_DAILY = 24 * 60 * 60 * 1000;
//        alarmManager.setInexactRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY,
//                alarmPendingIntent
//        );
    }
}
