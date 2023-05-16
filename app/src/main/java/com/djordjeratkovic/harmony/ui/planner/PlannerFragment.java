package com.djordjeratkovic.harmony.ui.planner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.util.PlannerEntity;
import com.djordjeratkovic.harmony.util.SwipeToDeleteCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlannerFragment extends Fragment {

    private PlannerViewModel plannerViewModel;

    private RecyclerView recyclerView;
    private TextView emptyPlanner;
    private FloatingActionButton fab;
    private ConstraintLayout conLayPlan;

    private List<PlannerEntity> plannerEntityList = new ArrayList<>();

    private Context context;

    private PlannerAdapter plannerAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        plannerViewModel =
                new ViewModelProvider(this).get(PlannerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_planner, container, false);

        context = getContext();

        recyclerView = root.findViewById(R.id.recyclerViewPlanner);
        emptyPlanner = root.findViewById(R.id.emptyPlanner);
        fab = root.findViewById(R.id.fabPlanner);
        conLayPlan = root.findViewById(R.id.conLayPlan);

        emptyPlanner.setVisibility(View.INVISIBLE);

        plannerViewModel.getAllPlans().observe(getViewLifecycleOwner(), new Observer<List<PlannerEntity>>() {
            @Override
            public void onChanged(List<PlannerEntity> plannerEntities) {
                if (!plannerEntities.isEmpty()) {
                    if (!plannerEntityList.isEmpty()) {
                        plannerEntityList.clear();
                    }
                    setHasOptionsMenu(true);
                    emptyPlanner.setVisibility(View.INVISIBLE);

                    plannerEntityList.addAll(plannerEntities);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    plannerAdapter = new PlannerAdapter(getContext(), plannerEntityList, PreferenceManager.getDefaultSharedPreferences(context), plannerViewModel);
                    ItemTouchHelper.Callback callback = new SwipeToDeleteCallback(context, plannerAdapter);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                    plannerAdapter.setItemTouchHelper(itemTouchHelper);
                    itemTouchHelper.attachToRecyclerView(recyclerView);

                    recyclerView.setAdapter(plannerAdapter);
                    plannerAdapter.notifyDataSetChanged();
                } else {
                    setHasOptionsMenu(false);
                    plannerEntityList.clear();
                    if (recyclerView.getAdapter() != null) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                    emptyPlanner.setVisibility(View.VISIBLE);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PlannerDialog.class);
                if (!plannerEntityList.isEmpty()) {
                    intent.putExtra("position", plannerEntityList.get(plannerEntityList.size() - 1).getPosition() + 1);
                }
                getActivity().startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
            inflater.inflate(R.menu.planner_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.deleteAllPlans) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Are you sure you want to delete all plans?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    plannerViewModel.deleteAllPlanners();
                    Toast.makeText(context, "All plans deleted", Toast.LENGTH_SHORT).show();
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

}