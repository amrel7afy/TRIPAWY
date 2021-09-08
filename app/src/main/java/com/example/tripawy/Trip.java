package com.example.tripawy;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum TripState {
    UPCOMING,
    DONE,
    CANCELED
}

enum TripType {
    ONE_WAY,
    ROUND
}

enum TripRepeat {
    NO,
    DAILY,
    WEEKLY,
    MONTHLY
}

@Entity
@IgnoreExtraProperties
public class Trip implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private long date;
    private long time;
    private String tripState;
    private String tripType;
    private String from;
    private String to;
    @TypeConverters({Converters.class})
    private ArrayList<String> notes;

    public Trip() {
    }

    public Trip(int id, String name, long date, long time, String tripState, String tripType, String from, String to, ArrayList<String> notes) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.tripState = tripState;
        this.tripType = tripType;
        this.from = from;
        this.to = to;
        this.notes = notes;
    }

    @Ignore
    public Trip(String name, long date, long time, String tripState, String tripType, String from, String to, ArrayList<String> notes) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.tripState = tripState;
        this.tripType = tripType;
        this.from = from;
        this.to = to;
        this.notes = notes;
    }

    public String getTripState() {
        return tripState;
    }

    public void setTripState(String tripState) {
        this.tripState = tripState;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }

}
