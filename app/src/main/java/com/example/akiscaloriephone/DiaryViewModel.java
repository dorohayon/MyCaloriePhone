package com.example.akiscaloriephone;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.DiaryEntry;

import java.util.List;

public class DiaryViewModel extends AndroidViewModel {
    private LiveData<List<DiaryEntry>> diaryEntries;

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db=AppDatabase.getInstance(getApplication());
        diaryEntries=db.diaryDao().loadAllById();
    }


    public LiveData<List<DiaryEntry>> getDiaryEntries() {
        return diaryEntries;
    }
}
