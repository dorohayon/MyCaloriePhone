package com.example.akiscaloriephone;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.akiscaloriephone.Dialogs.FoodlistSortByDialog;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.FoodEntry;

import java.util.List;

public class FoodListViewModel extends AndroidViewModel {
    private LiveData<List<FoodEntry>> foodList;

    public FoodListViewModel(@NonNull Application application) {
        super(application);
        SharedPreferences foodListPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
        String orderBy = foodListPrefs.getString(FoodlistSortByDialog.KEY_ORDER_BY_PREFS, "Name");
        switch (orderBy) {
            case "Name":
                foodList = AppDatabase.getInstance(getApplication()).foodDao().loadAllByName();
                break;
            case "Calories":
                foodList = AppDatabase.getInstance(getApplication()).foodDao().loadAllByCalories();
                break;
            case "Recently added":
                foodList = AppDatabase.getInstance(getApplication()).foodDao().loadAllById();
                break;
            default:
                foodList= AppDatabase.getInstance(getApplication()).foodDao().loadAllByName();
        }
    }

    public LiveData<List<FoodEntry>> getFoodList() {
        return foodList;
    }
}
