package com.djordjeratkovic.harmony.ui.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.ui.alarm.listener.ToRefreshFragment;
import com.djordjeratkovic.harmony.ui.alarm.receiver.AlarmBroadcastReceiver;
import com.djordjeratkovic.harmony.ui.planner.PlannerAdapter;
import com.djordjeratkovic.harmony.ui.planner.listener.AlarmCreated;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AlarmFragment extends Fragment {

    private FloatingActionButton fab;
    private TextView timeLeft;

    SharedPreferences sharedPref;

    private int alarmTime;

    CountDownTimer cTimer = null;
    private static final String FORMAT = "%02d:%02d:%02d";

    Context context;
    Activity activity;

    FragmentManager fragmentManager;

    private AlarmCreated alarmCreated;

    private ToRefreshFragment toRefreshFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        context = getContext();
        activity = getActivity();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);


        alarmCreated = PlannerAdapter.alarmCreated;

        toRefreshFragment = new ToRefreshFragment() {
            @Override
            public void refresh() {
                refreshFragment();
            }
        };

        View root;
        if (!checkForSavedAlarm(sharedPref)) {
            root = inflater.inflate(R.layout.fragment_no_alarm, container, false);

            fab = root.findViewById(R.id.fabAlarm);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewAlarmDialog newAlarmDialog = new NewAlarmDialog(context, toRefreshFragment);
                    fragmentManager = ((FragmentActivity) requireContext()).getSupportFragmentManager();
                    newAlarmDialog.show(fragmentManager, "alarmTag");
                }
            });
        } else {
            root = inflater.inflate(R.layout.fragment_alarm, container, false);
            alarmTime = sharedPref.getInt(getString(R.string.alarm_time), 0);

            timeLeft = root.findViewById(R.id.timeLeft);
        }
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkForSavedAlarm(sharedPref)) {
            startTimer();
        }
    }

    private void startTimer() {
        cTimer = new CountDownTimer(getTheTime(), 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeft.setText(""+String.format(FORMAT,
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }
            public void onFinish() {
                timeLeft.setText(getString(R.string._000000));
            }
        };
        cTimer.start();
    }
    private void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }
    @Override
    public void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        cancelTimer();
        super.onDestroyView();
    }

    private long getTheTime(){
        Calendar calendar = Calendar.getInstance();
        long hours24 = 86400000;
        calendar.setTime(new Date());
        int amPm = calendar.get(Calendar.AM_PM);
        long currentTime = calendar.get(Calendar.MILLISECOND) +
                calendar.get(Calendar.SECOND) * 1000 +
                calendar.get(Calendar.MINUTE) * 60000 +
                calendar.get(Calendar.HOUR) * 3600000;
        if (Objects.equals(amPm, 1)) {
            currentTime += hours24 / 2;
        }
        long theTime;
        if  (currentTime > alarmTime * 60000) {
            theTime =hours24 - currentTime + alarmTime * 60000;
        } else {
            theTime = alarmTime * 60000 - currentTime;
        }
        return theTime;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (checkForSavedAlarm(PreferenceManager.getDefaultSharedPreferences(context))) {
            inflater.inflate(R.menu.alarm_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteAlarm) {
            if (sharedPref.contains(getString(R.string.alarm_time))) {
                cancelAlarm();
                if (alarmCreated != null) {
                    alarmCreated.deleteAllPlannerReceivers();
                }
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(getString(R.string.alarm_time));
                editor.apply();
                cancelTimer();
                refreshFragment();
                Toast.makeText(context, "Alarm deleted", Toast.LENGTH_SHORT).show();
            }
        }
        if (item.getItemId() == R.id.qrCodeDownload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Are you sure you want to download the QR image?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveToGallery();
                    Toast.makeText(context, "QR image saved to gallery", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //canceled
                }
            }).create();
            builder.show();

        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkForSavedAlarm(SharedPreferences sharedPreferences) {
        return sharedPreferences.contains(getString(R.string.alarm_time));
    }

    //da bi se cancelovao receiver i ostalo da ne cekaju i slusaju
    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
    }

    private void refreshFragment() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    private void saveToGallery() {
        Drawable drawable = context.getDrawable(R.drawable.qr_code_image);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        String savedImageURL = MediaStore.Images.Media.insertImage(
                context.getContentResolver(),
                bitmap,
                "QR",
                "qr code for harmony alarm"
        );

        Uri.parse(savedImageURL);
    }
}