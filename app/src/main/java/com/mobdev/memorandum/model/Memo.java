package com.mobdev.memorandum.model;

import androidx.annotation.NonNull;

import java.text.DateFormat;

import io.realm.Realm;
import io.realm.RealmObject;

public class Memo extends RealmObject {
    private String title;
    private String content;
    private long createdTime;
    private String status;

    public Memo() {
    }

    public Memo(String title, String content, long time, String status) {
        this.title = title;
        this.content = content;
        this.createdTime = time;
        this.status = status;
    }

    // getters
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public long getCreatedTime() {
        return createdTime;
    }
    public String getFormattedTime() {return DateFormat.getDateTimeInstance().format(createdTime); }
    public String getStatus() {return status; }

    //setters
    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    //set status
    public void setStatus(String status) { this.status = status; }
    public void setAsActive() { this.status = "ACTIVE"; }
    public void setAsExpired() { this.status = "EXPIRED"; }
    public void setAsCompleted() { this.status = "COMPLETED"; }

    //check status
    public boolean isActive() {
        return this.status == "ACTIVE";
    }
    public boolean isExpired() {
        return this.status == "EXPIRED";
    }
    public boolean isCompleted() {
        return this.status == "COMPLETED";
    }

    @NonNull
    @Override
    public String toString() {
        return "Memo{ created = " + createdTime + '}';
    }
}