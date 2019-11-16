package com.example.akiscaloriephone.Database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
@Entity(tableName = "mysport")
public class SportDiaryEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private double caloriesBurned;
    private int time;
    private String intensity;
    private Date date;

    public SportDiaryEntry(int id, String name, double caloriesBurned, int time, String intensity, Date date) {
        this.id = id;
        this.name = name;
        this.caloriesBurned = caloriesBurned;
        this.time = time;
        this.intensity = intensity;
        this.date = date;

    }
    @Ignore
    public SportDiaryEntry(String name, double caloriesBurned, int time, String intensity, Date date) {
        this.name = name;
        this.caloriesBurned = caloriesBurned;
        this.time = time;
        this.intensity = intensity;
        this.date = date;
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

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
