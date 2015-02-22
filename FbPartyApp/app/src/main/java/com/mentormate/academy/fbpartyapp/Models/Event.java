package com.mentormate.academy.fbpartyapp.Models;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mentormate.academy.fbpartyapp.Utils.Constants;

import java.io.Serializable;

/**
 * Created by Student11 on 2/16/2015.
 */
public class Event implements Serializable {
    private int id;
    private String eventId;
    private String name;
    private String startTime;
    private String Description;
    private String coverSource;
    private String lat, lng;
    private LatLng location;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getCoverSource() {
        return coverSource;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setCover(String cover) {
        this.coverSource = cover;
    }

    public void setLocation(String eventLat, String eventLng) {
        this.lat = eventLat;
        this.lng = eventLng;
    }

    public String getLat()
    {
        return lat;
    }

    public String getLng()
    {
        return lng;
    }
}
