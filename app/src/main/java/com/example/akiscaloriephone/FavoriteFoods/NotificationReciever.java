package com.example.akiscaloriephone.FavoriteFoods;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.example.akiscaloriephone.NotificationsUtils;

import java.util.Calendar;
import java.util.Date;

public class NotificationReciever extends BroadcastReceiver {
    public static final int REQUEST_CODE_ALARM_BREAKFAST=7629;
    public static final int REQUEST_CODE_ALARM_LUNCH=8520;
    public static final int REQUEST_CODE_ALARM_DINNER=963;
    public static final String REQUEST_CODE_INDICATOR="requestcodeindicator";

    private AlarmManager mAlarmManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getIntExtra(REQUEST_CODE_INDICATOR, 0);
        Log.e("test", "Received notification broadcast of " + requestCode);
        Calendar calendar = Calendar.getInstance();
        Intent nextIntent;
        PendingIntent nextAlarmIntent;
        Date date;
        //check favorite food type
        switch (requestCode) {
            case REQUEST_CODE_ALARM_BREAKFAST:
                NotificationsUtils.createBreakfestNotification(context);
                mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                nextIntent = new Intent(context, NotificationReciever.class);
                nextIntent.putExtra(REQUEST_CODE_INDICATOR,REQUEST_CODE_ALARM_BREAKFAST);
                nextAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_BREAKFAST, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                //set next alarm to next day after recieve the alarm.
                calendar.add(Calendar.DAY_OF_YEAR,1);
                calendar.set(Calendar.HOUR_OF_DAY,10);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                date = calendar.getTime();
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), nextAlarmIntent);
                break;
            case REQUEST_CODE_ALARM_DINNER:
                NotificationsUtils.createDinnerNotification(context);
                mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                nextIntent = new Intent(context, NotificationReciever.class);
                nextIntent.putExtra(REQUEST_CODE_INDICATOR,REQUEST_CODE_ALARM_DINNER);
                nextAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_DINNER, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                calendar.add(Calendar.DAY_OF_YEAR,1);
                calendar.set(Calendar.HOUR_OF_DAY,22);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                date = calendar.getTime();
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), nextAlarmIntent);
                break;
            case REQUEST_CODE_ALARM_LUNCH:
                NotificationsUtils.createLunchNotification(context);
                mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                nextIntent = new Intent(context, NotificationReciever.class);
                nextIntent.putExtra(REQUEST_CODE_INDICATOR,REQUEST_CODE_ALARM_LUNCH);
                nextAlarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_LUNCH, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                calendar.add(Calendar.DAY_OF_YEAR,1);
                calendar.set(Calendar.HOUR_OF_DAY,16);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                date = calendar.getTime();
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), nextAlarmIntent);
                break;
        }


    }

    public void setAlarmBreakfast(Context context) {
        cancelAlarmBreakfast(context);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReciever.class);
        intent.putExtra(REQUEST_CODE_INDICATOR,REQUEST_CODE_ALARM_BREAKFAST);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_BREAKFAST, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);



  }

    /**
     * Cancels the next alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     * @param context the context of the app's Activity
     */
    public void cancelAlarmBreakfast(Context context) {
        Log.d("WakefulAlarmReceiver", "{cancelAlarm}");
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_BREAKFAST, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(alarmIntent);

    }


    public void setAlarmLunch(Context context) {
        cancelAlarmLunch(context);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReciever.class);
        intent.putExtra(REQUEST_CODE_INDICATOR,REQUEST_CODE_ALARM_LUNCH);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_LUNCH, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);




    }

    /**
     * Cancels the next alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     * @param context the context of the app's Activity
     */
    public void cancelAlarmLunch(Context context) {
        Log.d("WakefulAlarmReceiver", "{cancelAlarm}");
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_LUNCH, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(alarmIntent);

    }


    public void setAlarmDinner(Context context) {
        cancelAlarmDinner(context);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReciever.class);
        intent.putExtra(REQUEST_CODE_INDICATOR,REQUEST_CODE_ALARM_DINNER);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_DINNER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);




    }

    /**
     * Cancels the next alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     * @param context the context of the app's Activity
     */
    public void cancelAlarmDinner(Context context) {
        Log.d("WakefulAlarmReceiver", "{cancelAlarm}");
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM_DINNER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(alarmIntent);

    }
}
