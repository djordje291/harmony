package com.djordjeratkovic.harmony.ui.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.ui.alarm.sensor.ShakeDetector;
import com.djordjeratkovic.harmony.ui.alarm.service.AlarmService;

//activity koji alarm service aktivira
public class RingActivity extends AppCompatActivity {
    //TODO: set alarm time

    private static final String FORMAT = "%02d:%02d";

    ImageView clock;
    Button dismiss;
    TextView theTime,shakeText;

    Context context;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

//    private ResetPlanners resetPlanners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);

        context = getApplicationContext();

        //todo; activate
//        resetPlanners = new ResetPlanners(context);
//        resetPlanners.addPlans();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int time = preferences.getInt(getString(R.string.alarm_time), 0);


        clock = findViewById(R.id.clockRing);
        dismiss = findViewById(R.id.dismissRing);
        theTime = findViewById(R.id.theTime);
        shakeText = findViewById(R.id.shakeText);

        Animation shakeAnimation = AnimationUtils.loadAnimation(this,R.anim.little_shake);
        shakeText.startAnimation(shakeAnimation);
        dismiss.startAnimation(shakeAnimation);


        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAlarm();
                //TODO: set planner notf
//                resetPlanners.addPlans();
                //TODO: schedule another alarm
//                resetPlanners.scheduleAnotherAlarm();
                //
//                finish();
            }
        });
        theTime.setText("" + String.format(FORMAT,time / 60, time % 60));

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                dismissAlarm();
                //TODO: set planner notf
//                resetPlanners.addPlans();
                //TODO: schedule another alarm
//                resetPlanners.scheduleAnotherAlarm();
                Toast.makeText(context, "STOP IT I'M GONNA VOMIT!", Toast.LENGTH_SHORT).show();
                //
//                finish();
            }
        });
    }

    private void dismissAlarm() {
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}