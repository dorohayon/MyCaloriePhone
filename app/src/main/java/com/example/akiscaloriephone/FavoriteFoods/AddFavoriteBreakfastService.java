package com.example.akiscaloriephone.FavoriteFoods;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.akiscaloriephone.UI.MainActivity;
import com.example.akiscaloriephone.NotificationsUtils;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.DiaryEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddFavoriteBreakfastService extends IntentService {
    public static final String ADD_FAVORITE_BREAKFAST_SERVICE_NAME="addfavoritebreakfastsarvice";
    private AppDatabase db;
    public AddFavoriteBreakfastService() {
        super(ADD_FAVORITE_BREAKFAST_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("check intent", "arrived to the service class");
        db=AppDatabase.getInstance(getApplicationContext());
        List<DiaryEntry> FavFoods=new ArrayList<>();
        int notificationTag=intent.getIntExtra(NotificationsUtils.NOTIFICATION_TYPE_TAG,0);
        //check type of favorite food.
        switch (notificationTag){
            case NotificationsUtils.BREAKFAST_NOTIFICATION_ID :
                FavFoods= db.diaryDao().loadBreakfastFaviritesSync();
                break;
            case NotificationsUtils.LUNCH_NOTIFICATION_ID :
                FavFoods= db.diaryDao().loadLunchFaviritesSync();
                break;
            case NotificationsUtils.DINNER_NOTIFICATION_ID :
                FavFoods= db.diaryDao().loadDinnerFaviritesSync();
                break;
            default:
                break;
        }
        ArrayList<DiaryEntry> allToInsert=new ArrayList<>();
        for(DiaryEntry entry : FavFoods){
            DiaryEntry insertEntry=new DiaryEntry(entry.getName(),entry.getCalories(),entry.getSize(),entry.getQuantity(),Calendar.getInstance().getTime());
            allToInsert.add(insertEntry);
        }
        db.diaryDao().insertAll(allToInsert);
        Intent mainActivity = new Intent(getBaseContext(), MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(mainActivity);
    }
}
