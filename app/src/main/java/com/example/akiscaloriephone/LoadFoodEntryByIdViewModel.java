package com.example.akiscaloriephone;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.FoodEntry;

public class LoadFoodEntryByIdViewModel extends ViewModel {
    private LiveData<FoodEntry> food;

    public LoadFoodEntryByIdViewModel(AppDatabase db, int foodId) {
        food=db.foodDao().loadById(foodId);
    }

    public LiveData<FoodEntry> getFoodItem() {
        return food;
    }
}
