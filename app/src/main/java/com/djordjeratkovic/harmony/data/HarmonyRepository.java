package com.djordjeratkovic.harmony.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.djordjeratkovic.harmony.util.PlannerEntity;
import com.djordjeratkovic.harmony.util.TaskEntity;

import java.util.List;

public class HarmonyRepository {

    private HarmonyDao harmonyDao;
    private LiveData<List<PlannerEntity>> allPlanners;
    private LiveData<List<TaskEntity>> allTasks;

    public HarmonyRepository(Application application) {
        HarmonyRoomDatabase db = HarmonyRoomDatabase.getDatabase(application);
        harmonyDao = db.harmonyDao();
    }

    public LiveData<List<PlannerEntity>> getAllPlanners() {
        return harmonyDao.getAllPlanners();
    }

    public LiveData<List<TaskEntity>> getAllTasks() {
        return harmonyDao.getAllTasks();
    }

    //mozes ostale operacije sa kotlinom ili java.util.concurrent, to ti je jos jedna opcija

    public void insertNewTask(TaskEntity taskEntity) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                harmonyDao.insertTask(taskEntity);
            }
        };
        thread.start();
    }

    public void deleteTask(int id) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                harmonyDao.deleteTask(id);
            }
        };
        thread.start();
    }

    public void updateTask(TaskEntity taskEntity) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                harmonyDao.updateTask(taskEntity);
            }
        };
        thread.start();
    }

    public void deleteAllTasks() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                harmonyDao.deleteAllTasks();
            }
        };
        thread.start();
    }

    public void insertPlanner(PlannerEntity plannerEntity) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                harmonyDao.insertPlanner(plannerEntity);
            }
        };
        thread.start();
    }

    public void deletePlanner(int id) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                harmonyDao.deletePlanner(id);
            }
        };
        thread.start();
    }

    public void updatePlanner(PlannerEntity plannerEntity) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                harmonyDao.updatePlanner(plannerEntity);
            }
        };
        thread.start();
    }

    public void deleteAllPlanners() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                harmonyDao.deleteAllPlanners();
            }
        };
        thread.start();
    }
}
