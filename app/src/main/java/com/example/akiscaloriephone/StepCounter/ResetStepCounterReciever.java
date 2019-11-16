package com.example.akiscaloriephone.StepCounter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.akiscaloriephone.AppContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ResetStepCounterReciever extends BroadcastReceiver {
    public static final int REQUEST_CODE_RESET_STEPS = 14874;
    private AlarmManager mAlarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ResetStepCounter","arrived to reset step counter reciever");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = calender.getTime();
        String totalStepsDate = simpleDateFormat.format(date);
        // save last day calories
        editor.putFloat(totalStepsDate + "steps", sharedPreferences.getFloat(AppContract.STEP_PREFENCES, 0)).commit();
        //reset calories
        editor.putFloat(AppContract.STEP_PREFENCES, 0).commit();
        //set next day alarm of reset calories if the step counter prefs is on
        if (sharedPreferences.getBoolean("stepCounterSwitch", false)) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent nextIntent = new Intent(context, ResetStepCounterReciever.class);
            PendingIntent nextAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_RESET_STEPS, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            calender.add(Calendar.DAY_OF_YEAR, 1);
            calender.set(Calendar.HOUR_OF_DAY, 23);
            calender.set(Calendar.MINUTE, 58);
            calender.set(Calendar.SECOND, 0);
            calender.set(Calendar.MILLISECOND, 0);
            date = calender.getTime();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                //windowsLength-115  seconds . the time window from the date.
                mAlarmManager.setWindow(AlarmManager.RTC_WAKEUP, date.getTime(), 115000, nextAlarmIntent);
            else
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), nextAlarmIntent);
        }
    }


    public void resetStepCount(Context context) {
        cancelresetStepCount(context);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ResetStepCounterReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_RESET_STEPS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.HOUR_OF_DAY, 23);
        calender.set(Calendar.MINUTE, 58);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
        Date date = calender.getTime();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mAlarmManager.setWindow(AlarmManager.RTC_WAKEUP, date.getTime(), 115000, alarmIntent);
        else
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);
    }

    public void cancelresetStepCount(Context context) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ResetStepCounterReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_RESET_STEPS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(alarmIntent);

    }
}
