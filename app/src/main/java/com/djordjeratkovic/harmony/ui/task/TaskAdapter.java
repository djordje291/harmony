package com.djordjeratkovic.harmony.ui.task;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.util.TaskEntity;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private List<TaskEntity> taskList;
    private TaskViewModel taskViewModel;

    private boolean isMax = false;

    public TaskAdapter(Context context, List<TaskEntity> taskList, TaskViewModel taskViewModel) {
        this.context = context;
        this.taskList = taskList;
        this.taskViewModel = taskViewModel;
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_card, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskEntity task = taskList.get(position);

        holder.edit.setVisibility(View.GONE);
        holder.markAsImportant.setVisibility(View.GONE);
        holder.reminder.setVisibility(View.GONE);
        holder.minimizeMaximize.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);

        holder.title.setText(task.getTitle());
        holder.markAsImportant.setChecked(task.isImportant());
        if (task.getReminder() != null) {
            if (!task.getReminder().isEmpty()) {
                for (Drawable drawable : holder.reminder.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.red), PorterDuff.Mode.SRC_IN));
                    }
                }
                holder.reminder.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        }

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minMax(holder);
            }
        });
        holder.minimizeMaximize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minMax(holder);
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskDialog newTaskDialog = new NewTaskDialog(context, task);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                newTaskDialog.show(fragmentManager, "tag");
            }
        });
        holder.checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.title.setTextColor(Color.GRAY);
                } else {
                    holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.title.setTextColor(Color.BLACK);
                }
            }
        });
        holder.markAsImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setImportant(isChecked);
                taskViewModel.updateTask(task);
                notifyDataSetChanged();
            }
        });
        holder.reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: on reminder click opet again scroll

            }
        });
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private void minMax(ViewHolder holder) {
        if (isMax) {
            holder.edit.setVisibility(View.GONE);
            holder.markAsImportant.setVisibility(View.GONE);
            holder.reminder.setVisibility(View.GONE);
            holder.minimizeMaximize.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
            isMax = false;
        } else {
            holder.edit.setVisibility(View.VISIBLE);
            holder.markAsImportant.setVisibility(View.VISIBLE);
            holder.reminder.setVisibility(View.VISIBLE);
            holder.minimizeMaximize.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
            isMax = true;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageButton edit;
        private ImageView minimizeMaximize;
        private CheckBox checked, markAsImportant;
        private TextView title, reminder;

        private ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView, Context cnt) {
            super(itemView);
            context = cnt;
            minimizeMaximize = itemView.findViewById(R.id.minimizeMaximize);
            edit = itemView.findViewById(R.id.editTask);
            checked = itemView.findViewById(R.id.checked);
            markAsImportant = itemView.findViewById(R.id.markAsImportant);
            title = itemView.findViewById(R.id.titleTask);
            reminder = itemView.findViewById(R.id.reminder);
            constraintLayout = itemView.findViewById(R.id.constraintLayoutTask);
        }
    }
}
