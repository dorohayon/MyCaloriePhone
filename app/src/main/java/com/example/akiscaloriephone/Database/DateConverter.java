package com.example.akiscaloriephone.Database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp){
        return timestamp==null? null : new Date(timestamp);
    }
    @TypeConverter
    public static Long toTimesStamp(Date date){
        return date==null ? null: date.getTime();
    }
}
