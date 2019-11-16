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
public interface DiaryDao {
    @Query("SELECT * FROM diary WHERE date IS NOT NULL ORDER BY id")
    LiveData<List<DiaryEntry>> loadAllById();

    @Query("SELECT * FROM diary WHERE isInBreakfastFavorite = 1 ORDER BY id")
    List<DiaryEntry> loadBreakfastFaviritesSync();

    @Query("SELECT * FROM diary WHERE isInLunchFavorite = 1 ORDER BY id")
    List<DiaryEntry> loadLunchFaviritesSync();

    @Query("SELECT * FROM diary WHERE isInDinnerFavorite = 1 ORDER BY id")
    List<DiaryEntry> loadDinnerFaviritesSync();

    @Query("SELECT * FROM diary WHERE isInBreakfastFavorite = 1 ORDER BY id")
    LiveData<List<DiaryEntry>> loadBreakfastFavirites();

    @Query("SELECT * FROM diary WHERE isInLunchFavorite = 1 ORDER BY id")
    LiveData<List<DiaryEntry>> loadLunchFavirites();

    @Query("SELECT * FROM diary WHERE isInDinnerFavorite = 1 ORDER BY id")
    LiveData<List<DiaryEntry>> loadDinnerFavirites();

    @Insert
    void insert(DiaryEntry diaryEntry);

    @Insert
    void insertAll(ArrayList<DiaryEntry> diaryEntries);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(DiaryEntry diaryEntry);

    @Delete
    void delete(DiaryEntry diaryEntry);

    @Query("SELECT * FROM diary WHERE id = :id")
    LiveData<DiaryEntry> loadById(int id);

    @Query("SELECT * FROM diary WHERE id = :id")
    FoodEntry loadByIdSync(int id);

    @Query("SELECT * FROM diary WHERE date BETWEEN :from AND :to")
    LiveData<List<DiaryEntry>> findDiaryFoodsBetweenDates(Date from, Date to);
}
