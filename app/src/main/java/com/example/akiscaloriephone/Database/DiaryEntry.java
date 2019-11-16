package com.example.akiscaloriephone.Database;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "diary")
public class DiaryEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int calories;
    private String size;
    private double quantity=1;
    private Date date;
    private int isInBreakfastFavorite=0;
    private int isInLunchFavorite=0;
    private int isInDinnerFavorite=0;


    //    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//    private byte[] image;


    public DiaryEntry(int id, String name, int calories, String size, double quantity, Date date) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.size = size;
        this.quantity = quantity;
        this.date=date;
    }

    @Ignore
    public DiaryEntry( String name, int calories, String size, double quantity) {
        this.name = name;
        this.calories = calories;
        this.size = size;
        this.quantity = quantity;
    }

    @Ignore
    public DiaryEntry(String name, int calories, String size, double quantity, Date date) {
        this.name = name;
        this.calories = calories;
        this.size = size;
        this.quantity = quantity;
        this.date=date;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIsInBreakfastFavorite() {
        return isInBreakfastFavorite;
    }

    public void setIsInBreakfastFavorite(int isInBreakfastFavorite) {
        this.isInBreakfastFavorite = isInBreakfastFavorite;
    }

    public int getIsInLunchFavorite() {
        return isInLunchFavorite;
    }

    public void setIsInLunchFavorite(int isInLunchFavorite) {
        this.isInLunchFavorite = isInLunchFavorite;
    }

    public int getIsInDinnerFavorite() {
        return isInDinnerFavorite;
    }

    public void setIsInDinnerFavorite(int isInDinnerFavorite) {
        this.isInDinnerFavorite = isInDinnerFavorite;
    }
}
