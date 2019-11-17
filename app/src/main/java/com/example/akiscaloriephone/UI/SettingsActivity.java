package com.example.akiscaloriephone.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;


import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.work.WorkManager;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.FavoriteFoods.NotificationReciever;
import com.example.akiscaloriephone.FavoriteFoods.FavoriteFoodsActivity;
import com.example.akiscaloriephone.R;
import com.example.akiscaloriephone.StepCounter.ResetStepCounterReciever;
import com.example.akiscaloriephone.StepCounter.StepCounterService;


import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class SettingsActivity extends BaseActivity {

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.settings_activity, null, false);
        dl.addView(contentView, 0);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        new MaterialShowcaseView.Builder(this)
                .setTarget(contentView)
                .setContentText("Hello, this is the settings screen. Here you can add your details and determine your targets. " +
                        "Use the favorite foods options to select foods that you get a reminder about them everyday and insert them automatic to your diary.")
                .setDismissOnTouch(true)
                .withoutShape()
                .singleUse("8876")
                .show();


    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private WorkManager workManager;

        private boolean numberCheck(Object newValue) {
            if (!newValue.toString().equals("") && newValue.toString().matches("\\d*")) {
                return true;
            } else {
                Toast.makeText(getContext(), newValue + " Is not a valid number", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            EditTextPreference age = (EditTextPreference) findPreference("age");
            age.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return numberCheck(newValue);
                }
            });

            EditTextPreference weight = (EditTextPreference) findPreference("weight");
            weight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return numberCheck(newValue);
                }
            });

            EditTextPreference height = (EditTextPreference) findPreference("height");
            height.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return numberCheck(newValue);
                }
            });

            EditTextPreference targetWeight = (EditTextPreference) findPreference("weightTarget");
            targetWeight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return numberCheck(newValue);
                }
            });


            final NotificationReciever notificationReciever = new NotificationReciever();
            final SwitchPreference breakfestSwitch = findPreference("favoriteBreakfestSwitch");
            workManager = WorkManager.getInstance(getContext());
            breakfestSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (breakfestSwitch.isChecked()) {
                        Log.e("log", "checked breakfast");
                        notificationReciever.setAlarmBreakfast(getContext());
                        Toast.makeText(getContext(), "Favorite breakfast Notification is Enable", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("log", "not checked breakfast");
                        notificationReciever.cancelAlarmBreakfast(getContext());
                        Toast.makeText(getContext(), "Favorite breakfast Notification is disable", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                }
            });

            final SwitchPreference lunchSwitch = findPreference("favoriteLunchSwitch");
            lunchSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (lunchSwitch.isChecked()) {
                        Log.e("log", "checked lunch");
                        notificationReciever.setAlarmLunch(getContext());
                        Toast.makeText(getContext(), "Favorite lunch Notification is Enable", Toast.LENGTH_SHORT).show();
                    } else {
                        notificationReciever.cancelAlarmLunch(getContext());
                        Toast.makeText(getContext(), "Favorite lunch Notification is disable", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                }
            });


            final SwitchPreference dinnerSwitch = findPreference("favoriteDinnerSwitch");
            dinnerSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (dinnerSwitch.isChecked()) {
                        Log.e("log", "checked dinner");
                        notificationReciever.setAlarmDinner(getContext());
                        Toast.makeText(getContext(), "Favorite dinner Notification is Enable", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("log", "not checked dinner");
                        notificationReciever.cancelAlarmDinner(getContext());
                        Toast.makeText(getContext(), "Favorite dinner Notification is disable", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            Preference editBreakfest = findPreference("editFavBreakfestFoodsButton");
            editBreakfest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent favoriteFoods = new Intent(getActivity(), FavoriteFoodsActivity.class);
                    favoriteFoods.putExtra(AppContract.MODE, AppContract.MODE_FAVORITE_BREAKFEST);
                    startActivity(favoriteFoods);
                    return true;
                }
            });

            Preference editLunch = findPreference("editFavLunchFoodsButton");
            editLunch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent favoriteFoods = new Intent(getActivity(), FavoriteFoodsActivity.class);
                    favoriteFoods.putExtra(AppContract.MODE, AppContract.MODE_FAVORITE_LUNCH);
                    startActivity(favoriteFoods);
                    return true;
                }
            });

            Preference editDinner = findPreference("editFavDinnerFoodsButton");
            editDinner.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent favoriteFoods = new Intent(getActivity(), FavoriteFoodsActivity.class);
                    favoriteFoods.putExtra(AppContract.MODE, AppContract.MODE_FAVORITE_DINNER);
                    startActivity(favoriteFoods);
                    return true;
                }
            });

            final SwitchPreference stepCounter = findPreference("stepCounterSwitch");
            if (sharedPreferences.getBoolean("stepCounterSwitch", false))
                stepCounter.setChecked(true);
            else
                stepCounter.setChecked(false);
            stepCounter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (stepCounter.isChecked()) {
                        ResetStepCounterReciever resetStepCounterReciever = new ResetStepCounterReciever();
                        resetStepCounterReciever.resetStepCount(getContext());
                        Intent stepCounterService = new Intent(getContext(), StepCounterService.class);
                        getActivity().startService(stepCounterService);
                        Toast.makeText(getContext(), "Step counter is enable", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent stepCounterService = new Intent(getContext(), StepCounterService.class);
                        getActivity().stopService(stepCounterService);
                        Toast.makeText(getContext(), "Step counter is disable", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
    }
}