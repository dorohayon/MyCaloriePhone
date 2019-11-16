package com.example.akiscaloriephone.StepCounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.NotificationsUtils;


public class StepCounterService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private SharedPreferences sharedPreferences;


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.e("StepCounter", "step");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //add 1 to the step counter when noticed move.
        editor.putFloat(AppContract.STEP_PREFENCES, (sharedPreferences.getFloat(AppContract.STEP_PREFENCES,0))+1).commit();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("StepCounter", "arrived to service");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.e("StepCounter", "sensor is not null");
        }
        //start the service of step counter.
        startForeground(1, NotificationsUtils.createStepsCounterNotification(getApplicationContext()));
        return START_NOT_STICKY;
    }
}
