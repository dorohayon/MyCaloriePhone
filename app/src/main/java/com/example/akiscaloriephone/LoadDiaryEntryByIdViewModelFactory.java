package com.example.akiscaloriephone;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.akiscaloriephone.Database.AppDatabase;

public class LoadDiaryEntryByIdViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private AppDatabase db;
    private int foodID;

    public LoadDiaryEntryByIdViewModelFactory(AppDatabase db, int foodID) {
        this.db = db;
        this.foodID = foodID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoadDiaryEntryByIdViewModel(db,foodID);
        }
        }
