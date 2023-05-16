package com.djordjeratkovic.harmony.ui.planner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.djordjeratkovic.harmony.data.HarmonyRepository;
import com.djordjeratkovic.harmony.util.PlannerEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlannerViewModel extends AndroidViewModel {

    private HarmonyRepository harmonyRepository;
    private LiveData<List<PlannerEntity>> allPlans;

    public PlannerViewModel(@NonNull @NotNull Application application) {
        super(application);
        harmonyRepository = new HarmonyRepository(application);
        allPlans = harmonyRepository.getAllPlanners();
    }

    public LiveData<List<PlannerEntity>> getAllPlans() {
        return allPlans;
    }

    public void insertPlanner(PlannerEntity plannerEntity) {
        harmonyRepository.insertPlanner(plannerEntity);
    }

    public void deletePlanner(int position) {
        harmonyRepository.deletePlanner(position);
    }

    public void updatePlannner(PlannerEntity plannerEntity) {
        harmonyRepository.updatePlanner(plannerEntity);
    }

    public void deleteAllPlanners() {
        harmonyRepository.deleteAllPlanners();
    }

}