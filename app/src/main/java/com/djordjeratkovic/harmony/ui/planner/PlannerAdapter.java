package com.djordjeratkovic.harmony.ui.planner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.ui.planner.listener.AlarmCreated;
import com.djordjeratkovic.harmony.ui.planner.receiver.PlannerBroadcastReceiver;
import com.djordjeratkovic.harmony.util.ItemTouchHelperAdapter;
import com.djordjeratkovic.harmony.util.PlannerEntity;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.ViewHolder> implements ItemTouchHelperAdapter, AlarmCreated {

    private Context context;
    private List<PlannerEntity> plannerList;
    private PlannerViewModel plannerViewModel;

    private int globalTime;
    private SharedPreferences sharedPrefs;

    public static AlarmCreated alarmCreated;

    private ItemTouchHelper touchHelper;

    public PlannerAdapter(Context context, List<PlannerEntity> plannerList, SharedPreferences sharedPreferences, PlannerViewModel plannerViewModel) {
        this.context = context;
        this.plannerList = plannerList;
        this.sharedPrefs = sharedPreferences;
        this.plannerViewModel = plannerViewModel;
        globalTime = sharedPrefs.getInt(context.getString(R.string.alarm_time), 0);

        //giving it the methods, you can also instanciate it here and the methods would be here
        alarmCreated = this;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.planner_card, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        PlannerEntity plan = plannerList.get(position);

        holder.title.setText(plan.getTitle());
        holder.duration.setText(Integer.toString(plan.getDuration()) + "min");
        if (plan.getNote() == null || plan.getNote().equals("")) {
            holder.note.setVisibility(View.GONE);
            holder.lottieAnimationView.setAnimation(R.raw.planner_card_medium);
        } else {
            holder.note.setVisibility(View.VISIBLE);
            holder.lottieAnimationView.setAnimation(R.raw.planner_card_higher);
            holder.note.setText(plan.getNote());
        }

        holder.startTime.setText(returnFormat());
        if (!Objects.equals(globalTime, 0)) {
//            schedulePlanNotification(globalTime / 60, globalTime % 60);
            globalTime += plan.getDuration();
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, PlannerDialog.class);
                intent.putExtra("plan", plan);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return plannerList.size();
    }

    ///////////////////////
    @Override
    public void schedulePlanners(long startTime) {
        long time = startTime;
        if (!plannerList.isEmpty()) {
            for (PlannerEntity entity:
                 plannerList) {
                if (entity.isNotification()) {
                    schedulePlanNotification(time, entity);
                }
                time += (long) entity.getDuration();
            }
        }
    }

    @Override
    public void deleteAllPlannerReceivers() {
        if (!plannerList.isEmpty()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, PlannerBroadcastReceiver.class);
            for (PlannerEntity entity:
                 plannerList) {
                PendingIntent plannerPendingIntent = PendingIntent.getBroadcast(context, entity.getId(), intent, 0);
                alarmManager.cancel(plannerPendingIntent);
            }
        }
    }

    public void restoreItem(PlannerEntity plan, int position) {
//        data.add(position, item);
        plannerViewModel.insertPlanner(plan);
        notifyItemInserted(position);
    }

    private String returnFormat() {
        String timeInFormat;
        if (Objects.equals(globalTime, 0)) {
            timeInFormat = "N/A";
        } else {
            int hours = globalTime / 60;
            int minutes = globalTime % 60;
            timeInFormat = String.format("%02d:%02d",hours,minutes);
        }
        return timeInFormat;
    }

    @Override
    public void onItemDismiss(int position) {
        plannerViewModel.deletePlanner(plannerList.get(position).getId());
        notifyItemRemoved(position);

//        Snackbar snackbar = Snackbar
//                .make(conLayPlan, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//        snackbar.setAction("UNDO", new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                plannerAdapter.restoreItem(plan, position);
//                recyclerView.scrollToPosition(position);
//            }
//        });
//
//        snackbar.setActionTextColor(Color.YELLOW);
//        snackbar.show();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //TODO: figure out why its stops after 1
        PlannerEntity p;
        PlannerEntity p2;
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                p = plannerList.get(i);
                p2 = plannerList.get(i+1);
                p.setPosition(i+1);
                p2.setPosition(i);
                plannerViewModel.updatePlannner(p);
                plannerViewModel.updatePlannner(p2);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                p = plannerList.get(i);
                p2 = plannerList.get(i-1);
                p.setPosition(i-1);
                p2.setPosition(i);
                plannerViewModel.updatePlannner(p);
                plannerViewModel.updatePlannner(p2);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void setItemTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    private void schedulePlanNotification(long startTime, PlannerEntity plannerEntity) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, PlannerBroadcastReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putInt(context.getString(R.string.id), plannerEntity.getId());
        bundle.putString(context.getString(R.string.title), plannerEntity.getTitle());
        bundle.putInt(context.getString(R.string.duration), plannerEntity.getDuration());
        bundle.putString(context.getString(R.string.note), plannerEntity.getNote());

//        intent.putExtra(context.getString(R.string.id), plannerEntity.getId());
//        intent.putExtra(context.getString(R.string.title), plannerEntity.getTitle());
//        intent.putExtra(context.getString(R.string.duration), plannerEntity.getDuration());
//        intent.putExtra(context.getString(R.string.note), plannerEntity.getNote());

        intent.putExtras(bundle);

        Log.d("PlannerTAG", "adapter: " + plannerEntity.getTitle());

        PendingIntent plannerPendingIntent = PendingIntent.getBroadcast(context, plannerEntity.getId(), intent, 0);

        int h = (int) (startTime / 60);
        int m = (int) (startTime % 60);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                plannerPendingIntent
        );
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

        private TextView startTime, duration, title, note;
        private CardView cardView;
        private LottieAnimationView lottieAnimationView;

        GestureDetector gestureDetector;

        public ViewHolder(@NonNull @NotNull View itemView, Context cnt) {
            super(itemView);
            context = cnt;

            startTime = itemView.findViewById(R.id.plannerStartTime);
            duration = itemView.findViewById(R.id.plannerMinutesP);
            title = itemView.findViewById(R.id.plannerText);
            note = itemView.findViewById(R.id.OptionPlannerNote);

            cardView = itemView.findViewById(R.id.plannerCard);

            lottieAnimationView = itemView.findViewById(R.id.lottiePlannerCard);

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //click event
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            touchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return true;
        }
    }
}
