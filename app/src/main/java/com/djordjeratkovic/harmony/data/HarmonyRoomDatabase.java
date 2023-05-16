package com.djordjeratkovic.harmony.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.djordjeratkovic.harmony.util.PlannerEntity;
import com.djordjeratkovic.harmony.util.TaskEntity;

@Database(entities = {PlannerEntity.class, TaskEntity.class}, version = 1)
public abstract class HarmonyRoomDatabase extends RoomDatabase {

    public static volatile HarmonyRoomDatabase INSTANCE;

    public abstract HarmonyDao harmonyDao();

    public static HarmonyRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HarmonyRoomDatabase.class) {
                if (INSTANCE == null) {
                    //create our db
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HarmonyRoomDatabase.class, "harmony_db").build();
                }
            }
        }
        return INSTANCE;
    }

}
