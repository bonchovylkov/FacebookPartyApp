package com.mentormate.academy.fbpartyapp.Models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Student11 on 2/16/2015.
 */
public class Event implements Serializable {
    private int id;
    private String eventId;
    private String name;
    private String startTime;
    private String Description;

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

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
