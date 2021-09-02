package com.example.tripawy;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripDao {

    @Query("SELECT * FROM trip")
    LiveData<List<Trip>> getAll();

    @Query("SELECT * FROM trip where tripState == 'UPCOMING' ")
    LiveData<List<Trip>> getAllUpcoming();

    @Query("SELECT * FROM trip where tripState != 'UPCOMING' ")
    LiveData<List<Trip>> getAllHistory();

    @Insert
    void insertAll(Trip... trips);

    @Insert
    void insert(Trip trip);

    @Update
    void update(Trip trip);

    @Delete
    void delete(Trip trip);
}
