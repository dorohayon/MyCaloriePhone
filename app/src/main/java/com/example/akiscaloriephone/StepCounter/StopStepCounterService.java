package com.example.akiscaloriephone.StepCounter;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.akiscaloriephone.UI.SettingsActivity;

public class StopStepCounterService extends IntentService {
    public StopStepCounterService() {
        super("stopstepcounterservice");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("test","stopping step counter");
        Intent settingsActivity=new Intent(getBaseContext(), SettingsActivity.class);
        settingsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(settingsActivity);
    }
}
