package com.example.akiscaloriephone;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.akiscaloriephone.Database.AppDatabase;

import java.util.Date;

public class LoadSportBetweenDatesViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private AppDatabase db;
    private Date startingDate;
    private Date finishDate;

    public LoadSportBetweenDatesViewModelFactory(AppDatabase db, Date startingDate, Date finishDate) {
        this.db = db;
        this.startingDate = startingDate;
        this.finishDate = finishDate;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoadSportBetweenDatesViewModel(db,startingDate,finishDate);
    }
}
