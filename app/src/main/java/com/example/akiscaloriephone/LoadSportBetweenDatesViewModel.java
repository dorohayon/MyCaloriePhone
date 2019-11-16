package com.example.akiscaloriephone;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.SportDiaryEntry;

import java.util.Date;
import java.util.List;

public class LoadSportBetweenDatesViewModel extends ViewModel {
    private LiveData<List<SportDiaryEntry>> sport;

    public LoadSportBetweenDatesViewModel(AppDatabase db, Date startingDate,Date finishDate) {
        this.sport = db.mySportDao().findSportsBetweenDates(startingDate,finishDate);
    }

    public LiveData<List<SportDiaryEntry>> getSport(){
        return sport;
    }
}
