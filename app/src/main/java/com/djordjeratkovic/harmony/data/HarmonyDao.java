package com.djordjeratkovic.harmony.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.djordjeratkovic.harmony.util.PlannerEntity;
import com.djordjeratkovic.harmony.util.TaskEntity;

import java.util.List;

@Dao
public interface HarmonyDao {

    @Insert
    void insertPlanner(PlannerEntity planner);

    @Insert
    void insertTask(TaskEntity task);

    @Query("DELETE FROM planner")
    void deleteAllPlanners();

    @Query("DELETE FROM task")
    void deleteAllTasks();

    @Query("DELETE FROM planner WHERE id = :id")
    int deletePlanner(int id);

    @Query("DELETE FROM task WHERE id = :id")
    int deleteTask(int id);

    @Query("SELECT * FROM planner ORDER BY `position` ASC")
    LiveData<List<PlannerEntity>> getAllPlanners();

    @Query("SELECT * FROM task ORDER BY isImportant DESC, id ASC")
    LiveData<List<TaskEntity>> getAllTasks();

    @Update
    void updatePlanner(PlannerEntity plannerEntity);

    @Update
    void updateTask(TaskEntity taskEntity);

}
