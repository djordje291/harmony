package com.djordjeratkovic.harmony.ui.planner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.djordjeratkovic.harmony.MainActivity;
import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.util.PlannerEntity;

public class PlannerDialog extends AppCompatActivity {

    private PlannerViewModel plannerViewModel;

    private EditText title, note;
    private NumberPicker hours, minutes;
    private SwitchCompat notification;
    private ImageButton save;

    private int position;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        setContentView(R.layout.activity_planner_dialog);

        plannerViewModel = new ViewModelProvider(this).get(PlannerViewModel.class);

        context = getApplicationContext();

        title = findViewById(R.id.titleDialog);
        note = findViewById(R.id.noteDialog);
        hours = findViewById(R.id.hoursDialogP);
        minutes = findViewById(R.id.minutesDialogP);
        notification = findViewById(R.id.notificationP);
        save = findViewById(R.id.saveDialog);

        hours.setMinValue(0);
        hours.setMaxValue(23);
        minutes.setMinValue(0);
        minutes.setMaxValue(59);

        if (intent.hasExtra("plan")) {
            PlannerEntity plan = (PlannerEntity) intent.getSerializableExtra("plan");

            title.setText(plan.getTitle());
            note.setText(plan.getNote());
            notification.setChecked(plan.isNotification());

            int h = plan.getDuration() / 60;
            int m = plan.getDuration() % 60;
            hours.setValue(h);
            minutes.setValue(m);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int time = getTime();
                    if (!TextUtils.isEmpty(title.getText().toString().trim()) && time != 0) {
                        plan.setTitle(title.getText().toString().trim());
                        plan.setDuration(time);
                        plan.setNote(note.getText().toString().trim());
                        plan.setNotification(notification.isChecked());

                        plannerViewModel.updatePlannner(plan);

                        Intent intent = new Intent();
                        intent.setClass(PlannerDialog.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int time = getTime();
                    if (!TextUtils.isEmpty(title.getText().toString().trim()) && time != 0) {
                        PlannerEntity plannerEntity = new PlannerEntity(title.getText().toString().trim(), time,
                                !TextUtils.isEmpty(note.getText().toString().trim()) ? note.getText().toString().trim() : null,
                                notification.isChecked(), position);

                        plannerViewModel.insertPlanner(plannerEntity);

                        Intent intent = new Intent();
                        intent.setClass(PlannerDialog.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private int getTime() {
        return hours.getValue() * 60 + minutes.getValue();
    }
}