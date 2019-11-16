package com.example.akiscaloriephone;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.FoodEntry;

import java.util.List;

public class LoadFoodsByNameViewModel extends AndroidViewModel {
    private LiveData<List<FoodEntry>> entries;

    public LoadFoodsByNameViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db=AppDatabase.getInstance(getApplication());
        entries=db.foodDao().loadAllByName();
    }


    public LiveData<List<FoodEntry>> getFoodEntries() {
        return entries;
    }
}
