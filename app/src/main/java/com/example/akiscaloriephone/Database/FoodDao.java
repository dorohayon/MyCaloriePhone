package com.example.akiscaloriephone.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;
@Dao
public interface FoodDao {
    @Query("SELECT * FROM food_list ORDER BY id")
    LiveData<List<FoodEntry>> loadAllById();

    @Query("SELECT * FROM food_list ORDER BY name")
    LiveData<List<FoodEntry>> loadAllByName();

    @Query("SELECT * FROM food_list ORDER BY calories")
    LiveData<List<FoodEntry>> loadAllByCalories();

    @Insert
    void insert(FoodEntry foodEntry);

    @Insert
    void insertAll(ArrayList<FoodEntry> foodEntries);

    @Update
    void update(FoodEntry foodEntry);

    @Delete
    void delete(FoodEntry foodEntry);

    @Query("SELECT * FROM food_list WHERE id = :id")
    LiveData<FoodEntry> loadById(int id);

    @Query("SELECT * FROM food_list WHERE id = :id")
    FoodEntry loadByIdSync(int id);
}
