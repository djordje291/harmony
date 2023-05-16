package com.djordjeratkovic.harmony.ui.task;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.djordjeratkovic.harmony.data.HarmonyRepository;
import com.djordjeratkovic.harmony.util.TaskEntity;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private HarmonyRepository harmonyRepository;
    private LiveData<List<TaskEntity>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        harmonyRepository = new HarmonyRepository(application);
        allTasks = harmonyRepository.getAllTasks();
    }

    public LiveData<List<TaskEntity>> getAllTasks() {
        return allTasks;
    }

    public void insertNewTask(TaskEntity taskEntity) {
        harmonyRepository.insertNewTask(taskEntity);
    }

    public void deleteTask(int id) {
        harmonyRepository.deleteTask(id);
    }

    public void updateTask(TaskEntity taskEntity) {
        harmonyRepository.updateTask(taskEntity);
    }

    public void deleteAllTasks() {
        harmonyRepository.deleteAllTasks();
    }
}