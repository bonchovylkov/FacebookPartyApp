package com.mentormate.academy.fbpartyapp.Models;

import java.util.Date;

/**
 * Created by Student11 on 2/16/2015.
 */
public class Event {
    private int id;
    private int eventId;
    private String name;
    private String startTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
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
