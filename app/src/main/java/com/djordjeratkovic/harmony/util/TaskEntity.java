package com.djordjeratkovic.harmony.util;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task")
public class TaskEntity {

    @PrimaryKey(autoGenerate = true)
    int id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "reminder")
    private String reminder;

    @NonNull
    @ColumnInfo(name = "isImportant")
    private boolean isImportant;

    public TaskEntity(int id,@NonNull String title,String reminder, @NonNull boolean isImportant) {
        this.id = id;
        this.title = title;
        this.reminder = reminder;
        this.isImportant = isImportant;
    }

    @Ignore
    public TaskEntity(@NonNull String title,String reminder, @NonNull boolean isImportant) {
        this.title = title;
        this.reminder = reminder;
        this.isImportant = isImportant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(@NonNull boolean important) {
        isImportant = important;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }
}
