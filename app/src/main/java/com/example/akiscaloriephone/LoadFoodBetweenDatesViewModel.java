package com.example.akiscaloriephone;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.DiaryEntry;

import java.util.Date;
import java.util.List;

public class LoadFoodBetweenDatesViewModel extends ViewModel {
    private LiveData<List<DiaryEntry>> food;

    public LoadFoodBetweenDatesViewModel(AppDatabase db, Date startingDate,Date finishDate) {
        this.food = db.diaryDao().findDiaryFoodsBetweenDates(startingDate,finishDate);
    }

    public LiveData<List<DiaryEntry>> getFood() {
        return food;
    }
}
