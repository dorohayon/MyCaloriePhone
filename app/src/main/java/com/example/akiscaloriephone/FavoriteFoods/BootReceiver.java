package com.example.akiscaloriephone.FavoriteFoods;

import android.content.BroadcastReceiver;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.preference.PreferenceManager;
import androidx.work.WorkManager;

import com.example.akiscaloriephone.StepCounter.ResetStepCounterReciever;
import com.example.akiscaloriephone.StepCounter.StepCounterService;


public class BootReceiver extends BroadcastReceiver {
    private WorkManager workManager;


    @Override
    public void onReceive(Context context, Intent intent) {
        //reset all services after phone reboot.
        workManager = WorkManager.getInstance(context);
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            NotificationReciever notificationReciever = new NotificationReciever();
            ResetStepCounterReciever resetStepCounterReciever = new ResetStepCounterReciever();
            if (sharedPreferences.getBoolean("favoriteBreakfestSwitch", false)) {
                Log.e("Reboot", "arrived to intent breakfast");
                notificationReciever.setAlarmBreakfast(context);
            }
            if (sharedPreferences.getBoolean("favoriteLunchSwitch", false)) {
                Log.e("Reboot", "arrived to intent lunch");
                notificationReciever.setAlarmLunch(context);
            }

            if (sharedPreferences.getBoolean("favoriteDinnerSwitch", false)) {
                Log.e("Reboot", "arrived to intent dinner");
                notificationReciever.setAlarmDinner(context);
            }
            if (sharedPreferences.getBoolean("stepCounterSwitch", false)) {
                Log.e("StepCounter", "arrived to step counter on boot reciever");
                resetStepCounterReciever.resetStepCount(context);
                Intent stepCounterService = new Intent(context, StepCounterService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context.startForegroundService(stepCounterService);
                else
                    context.startService(stepCounterService);

            }
        }
    }
}