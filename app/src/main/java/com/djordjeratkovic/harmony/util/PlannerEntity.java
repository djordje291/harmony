package com.djordjeratkovic.harmony.util;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "planner")
public class PlannerEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NotNull
    @ColumnInfo(name = "title")
    private String title;

    @NotNull
    @ColumnInfo(name = "duration")
    private int duration;

    @ColumnInfo(name = "note")
    private String note;

    @NotNull
    @ColumnInfo(name = "notification")
    private boolean notification;

    @NotNull
    @ColumnInfo(name = "position")
    private int position;

    @Ignore
    private long startTime;


    public PlannerEntity(int id, String title, int duration, String note, boolean notification, int position) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.note = note;
        this.notification = notification;
        this.position = position;
    }

    @Ignore
    public PlannerEntity(String title, int duration, String note, boolean notification, int position) {
        this.title = title;
        this.duration = duration;
        this.note = note;
        this.notification = notification;
        this.position = position;
    }

    @Ignore
    public PlannerEntity(String title, long startTime, int duration, String note, boolean notification, int position) {
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
        this.note = note;
        this.notification = notification;
        this.position = position;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Ignore
    public long getStartTime() {
        return startTime;
    }

    @Ignore
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
