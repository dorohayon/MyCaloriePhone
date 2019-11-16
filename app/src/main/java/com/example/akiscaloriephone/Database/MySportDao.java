package com.example.akiscaloriephone.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dao
public interface MySportDao {
    @Insert
    void insert(SportDiaryEntry sportEntry);

    @Insert
    void insertAll(ArrayList<SportDiaryEntry> sportEntries);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(SportDiaryEntry sportEntry);

    @Delete
    void delete(SportDiaryEntry sportEntry);

    @Query("SELECT * FROM mysport ORDER BY id")
    LiveData<List<SportDiaryEntry>> loadAll();

    @Query("SELECT * FROM mysport WHERE id = :id")
    LiveData<SportDiaryEntry> loadById(int id);

    @Query("SELECT * FROM mysport WHERE date BETWEEN :from AND :to")
    LiveData<List<SportDiaryEntry>> findSportsBetweenDates(Date from, Date to);

}
