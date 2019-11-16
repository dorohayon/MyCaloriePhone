package com.example.akiscaloriephone.Database;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_list")
public class FoodEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int calories;
    private String size;


    public FoodEntry(int id, String name, int calories,String size) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.size=size;
    }

    @Ignore
    public FoodEntry(String name,int calories,String size) {
        this.name = name;
        this.calories = calories;
        this.size=size;
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

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }



}
