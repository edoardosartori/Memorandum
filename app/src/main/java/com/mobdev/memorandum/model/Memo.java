package com.mobdev.memorandum.model;

import androidx.annotation.NonNull;

public class Memo {
    private int id;
    private String title;
    private String content;
    private long date;
    private String status;

    public Memo() {
    }

    public Memo(String title, String content, long date, String status) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.status = status;
    }

    // getters
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public long getDate() {
        return date;
    }
    public String getStatus() {return status; }

    //setters
    public void setId(int id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setDate(long date) {
        this.date = date;
    }

    //set status
    public void setStatus(String status) { this.status = status; }
    public void setActive() { this.status = "ACTIVE"; }
    public void setExpired() { this.status = "EXPIRED"; }
    public void setCompleted() { this.status = "COMPLETED"; }

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
        return "Note{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }
}