package com.djordjeratkovic.harmony.ui.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.util.TaskEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TaskFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyTask;
    private FloatingActionButton fab;

    private TaskViewModel taskViewModel;

    private List<TaskEntity> taskEntityList = new ArrayList<>();

    private Context context;

    private TaskAdapter taskAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        View root = inflater.inflate(R.layout.fragment_task, container, false);


        context = getContext();

        recyclerView = root.findViewById(R.id.recyclerViewTask);
        emptyTask = root.findViewById(R.id.emptyTask);
        fab = root.findViewById(R.id.fabTask);

        emptyTask.setVisibility(View.INVISIBLE);

        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<TaskEntity>>() {
            @Override
            public void onChanged(List<TaskEntity> taskEntities) {
                if (!taskEntities.isEmpty()) {
                    if (!taskEntityList.isEmpty()) {
                        taskEntityList.clear();
                    }
                    setHasOptionsMenu(true);
                    emptyTask.setVisibility(View.INVISIBLE);

                    taskEntityList.addAll(taskEntities);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    taskAdapter = new TaskAdapter(getContext(), taskEntityList, taskViewModel);
                    new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
                    recyclerView.setAdapter(taskAdapter);
                    taskAdapter.notifyDataSetChanged();

                } else {
                    setHasOptionsMenu(false);
                    taskEntityList.clear();
                    if (recyclerView.getAdapter() != null) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                    emptyTask.setVisibility(View.VISIBLE);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskDialog newTaskDialog = new NewTaskDialog(context);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                newTaskDialog.show(fragmentManager, "tag");
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //TODO: first it does this, then you get the list, switch it
            inflater.inflate(R.menu.task_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteAllTasks) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Are you sure you want to delete all tasks?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    taskViewModel.deleteAllTasks();
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
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

    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            taskViewModel.deleteTask(taskEntityList.get(viewHolder.getAdapterPosition()).getId());
            taskAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
        }
    };
}