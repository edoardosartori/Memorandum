package com.mobdev.memorandum.model;

import android.icu.text.Transliterator;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Memo extends RealmObject {
    @PrimaryKey
    public String id;
    private String title;
    private String content;
    private long createdTime;
    private String status;
    private String locality;
    private double latitude;
    private double longitude;

    public Memo() {
    }

    public Memo(String title, String content, long time, String status, String locality, double latitude, double longitude) {
        this.title = title;
        this.content = content;
        this.createdTime = time;
        this.status = status;
        this.locality = locality;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // getters
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getFormattedTime() { return DateFormat.getDateTimeInstance().format(createdTime); }
    public String getLocality() { return locality; }
    public LatLng getLatLng() { return new LatLng(latitude, longitude); }
    public double lat() { return latitude; }
    public double lng() { return longitude; }

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
    public void setLocality(String locality) {
        this.locality = locality;
    }
    public void setLatitude(double lat) {
        this.latitude = lat;
    }
    public void setLongitude(double lng) {
        this.longitude = lng;
    }
    public void setLatLng(double lat, double lnt) {
        this.latitude = lat;
        this.longitude = lnt;
    }

    //set status
    public void setStatus(String status) { this.status = status; }
    public void setAsActive() { this.status = "ACTIVE"; }
    public void setAsExpired() { this.status = "EXPIRED"; }
    public void setAsCompleted() { this.status = "COMPLETED"; }

    //check status
    public boolean isActive() { return Objects.equals(this.status, "ACTIVE"); }
    public boolean isExpired() { return Objects.equals(this.status, "EXPIRED"); }
    public boolean isCompleted() { return Objects.equals(this.status, "COMPLETED"); }
}