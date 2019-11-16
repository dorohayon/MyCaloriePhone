package com.example.akiscaloriephone;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.DiaryEntry;

public class LoadDiaryEntryByIdViewModel extends ViewModel {
    private LiveData<DiaryEntry> food;

    public LoadDiaryEntryByIdViewModel(AppDatabase db, int foodId) {
        food=db.diaryDao().loadById(foodId);
    }

    public LiveData<DiaryEntry> getFoodItem() {
        return food;
    }
}
