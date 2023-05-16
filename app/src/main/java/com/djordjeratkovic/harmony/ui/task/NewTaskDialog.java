package com.djordjeratkovic.harmony.ui.task;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.ui.task.receiver.TaskBroadcastReceiver;
import com.djordjeratkovic.harmony.util.TaskEntity;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewTaskDialog extends DialogFragment implements View.OnClickListener {
    private static final String dateFormat = "EEE MMM dd HH:mm:ss z yyyy";
//    Fri Aug 20 23:34:00 GMT+02:00 2021

    private TaskViewModel taskViewModel;

    private EditText title;
    private ImageButton reminder, important;
    private Button add;

    private boolean isImportant = false;

    Dialog dialog ;
    SingleDateAndTimePickerDialog.Builder timePicker;
    String dateReminderTask;
    Date dateForBroadcast;

    Context context;
    View view;

    private TaskEntity taskEntity;

    public NewTaskDialog(Context context) {
        this.context = context;
    }

    public NewTaskDialog(Context context, TaskEntity taskEntity) {
        this.context = context;
        this.taskEntity = taskEntity;
        isImportant = taskEntity.isImportant();
        if (taskEntity.getReminder() != null) {
            cancelReminder();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.new_task_dialog, null);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        title = view.findViewById(R.id.titleNewTask);
        reminder = view.findViewById(R.id.reminderNewTask);
        important = view.findViewById(R.id.importantNewTask);
        add = view.findViewById(R.id.addNewTask);

        reminder.setOnClickListener(this);
        important.setOnClickListener(this);
        add.setOnClickListener(this);

        if (taskEntity != null) {
            populateDialog();
        }

        //da se fokusira na title kad se otvori
        title.requestFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        //dajes ga dialogu da bi dialog mogao tastaturu da dobije
        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewTask:
                    if (!TextUtils.isEmpty(title.getText().toString().trim())) {
                        if (taskEntity != null) {
                            taskEntity.setTitle(title.getText().toString().trim());
                            taskEntity.setReminder(dateReminderTask);
                            taskEntity.setImportant(isImportant);

                            taskViewModel.updateTask(taskEntity);

                            if (dateReminderTask != null && !dateReminderTask.isEmpty() && dateForBroadcast != null) {
                                scheduleTask(taskEntity);
                            }
                        } else {
                            TaskEntity newTask = new TaskEntity(title.getText().toString().trim(),
                                    dateReminderTask,
                                    isImportant);
                            taskViewModel.insertNewTask(newTask);

                            if (dateReminderTask != null && !dateReminderTask.isEmpty() && dateForBroadcast != null) {
                                scheduleTask(newTask);
                            }
                        }

                        dialog.dismiss();
                    }
                break;
            case R.id.reminderNewTask:
                //TODO: when you dismiss it it loses context, maybe attach fragment manager again
                dialog.dismiss();
                timePicker = new SingleDateAndTimePickerDialog.Builder(getContext())
                        .mustBeOnFuture()
                        .bottomSheet()
                        .backgroundColor(ContextCompat.getColor(context, R.color.white))
                        .mainColor(ContextCompat.getColor(context, R.color.orange))
                        .titleTextColor(ContextCompat.getColor(context, R.color.blue_dark))
                        .mustBeOnFuture()
                        .minutesStep(1)
                        .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                            @Override
                            public void onDisplayed(SingleDateAndTimePicker picker) {
                                // Retrieve the SingleDateAndTimePicker
                            }
                        })
                        .title("Set time and date for the reminder")
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                dateReminderTask = date.toString();
                                dateForBroadcast = date;
                                reminder.setColorFilter(ContextCompat.getColor(context, R.color.red));
                                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                                dialog.show();
                            }
                        });
                timePicker.display();

                break;
            case R.id.importantNewTask:
                if (isImportant) {
                    important.setColorFilter(ContextCompat.getColor(context, R.color.black));
                    isImportant = false;
                } else {
                    important.setColorFilter(ContextCompat.getColor(context, R.color.red));
                    isImportant = true;
                }
                break;
        }
    }

    private void scheduleTask(TaskEntity taskEntity) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TaskBroadcastReceiver.class);

        intent.putExtra(getString(R.string.id), taskEntity.getId());
        intent.putExtra(context.getString(R.string.title), taskEntity.getTitle());

        PendingIntent taskPendingIntent = PendingIntent.getBroadcast(context, taskEntity.getId() + 1000, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTime(dateForBroadcast);

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                taskPendingIntent);
    }

    private void populateDialog() {
        title.setText(taskEntity.getTitle());
        if (taskEntity.isImportant()) {
            important.setColorFilter(ContextCompat.getColor(context, R.color.red));
            isImportant = true;
        } else {
            important.setColorFilter(ContextCompat.getColor(context, R.color.black));
            isImportant = false;
        }
        if (taskEntity.getReminder() != null) {
            dateReminderTask = taskEntity.getReminder();
//            Date date = taskEntity.getReminder();
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            try {
                dateForBroadcast = formatter.parse(taskEntity.getReminder());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            reminder.setColorFilter(ContextCompat.getColor(context, R.color.red));
        }
    }

    private void cancelReminder() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TaskBroadcastReceiver.class);
        PendingIntent plannerPendingIntent = PendingIntent.getBroadcast(context, taskEntity.getId(), intent, 0);
        alarmManager.cancel(plannerPendingIntent);
    }

    //TODO: figure out why dismiss is not working
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        view.setOnKeyListener(new View.OnKeyListener()
//        {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
//                {
//                    //This is the filter
//                    if (event.getAction()!= KeyEvent.ACTION_DOWN)
//                        return true;
//                    else
//                    {
//                        if (dialog.isShowing()) {
//                            dialog.dismiss();
//                        } else {
//                            timePicker.dismiss();
//                        }
//                        //Hide your keyboard here!!!!!!
//                        return true; // pretend we've processed it
//                    }
//                }
//                else {
//                    return false; // pass on to be processed as normal
//                }
//            }
//        });
//    }

}
