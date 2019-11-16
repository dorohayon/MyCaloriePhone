package com.example.akiscaloriephone.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {FoodEntry.class,DiaryEntry.class, SportDiaryEntry.class},version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "AkisDatabase";
    private static AppDatabase dbInstance;

    public static AppDatabase getInstance(final Context context) {
        if (dbInstance == null) {
            synchronized (LOCK) {
                dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();

            }
        }
        return dbInstance;
    }

    public abstract FoodDao foodDao();

    public abstract DiaryDao diaryDao();

    public abstract MySportDao mySportDao();






}
