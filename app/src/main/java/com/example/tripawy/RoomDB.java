package com.example.tripawy;

import android.content.Context;

import androidx.room.Room;

public class RoomDB {

    static AppDatabase db = null;
    public static TripDao getTrips(Context application) {
        if (db == null) {
            db = Room.databaseBuilder(application,
                    AppDatabase.class, "database").build();
            return db.tripDao();
        } else return db.tripDao();
    }


    }
