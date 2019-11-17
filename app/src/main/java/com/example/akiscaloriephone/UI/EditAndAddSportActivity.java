package com.example.akiscaloriephone.UI;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.AppExecutors;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.SportDiaryEntry;
import com.example.akiscaloriephone.Database.SportEntry;
import com.example.akiscaloriephone.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class EditAndAddSportActivity extends BaseActivity {

    private TextView nameTextView;
    private TextView caloriesTextView;
    private Spinner intensitySpinner;
    private EditText timeEditText;
    private SharedPreferences sharedPreferences;
    private AppDatabase db;
    private String mode;
    private String sportName;
    private LinkedHashMap<String, Double> levelAndMETList;
    private TextView enterDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_edit_and_add_sport, null, false);
        dl.addView(contentView, 0);
        mode = getIntent().getStringExtra(AppContract.MODE);
        db = AppDatabase.getInstance(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        nameTextView = findViewById(R.id.AddSportName);
        caloriesTextView = findViewById(R.id.AddSportCalories);
        intensitySpinner = (Spinner) findViewById(R.id.AddSportIntensity);
        timeEditText = (EditText) findViewById(R.id.AddSportTime);
        SportEntry entry = (SportEntry) getIntent().getSerializableExtra(AppContract.SPORT_OBJECT);
        sportName = entry.getName();
        levelAndMETList = entry.getLevelAndMET();
        setupIntensitySpinner(levelAndMETList);
        nameTextView.setText(sportName);
        enterDetails=findViewById(R.id.enterDetailsSportTextView);
        String weightString = (sharedPreferences.getString("weight", "0").trim());
        //if the user enter weight, remove the message to enter weight.
        if(!weightString.equals("0"))
            enterDetails.setVisibility(View.INVISIBLE);
        enterDetails.setClickable(true);
        enterDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditAndAddSportActivity.this,SettingsActivity.class));
            }
        });

        timeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int calories = calculateBurnedCalories(levelAndMETList, charSequence.toString());
                caloriesTextView.setText(String.valueOf(calories));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        intensitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String timeString = timeEditText.getText().toString();
                int calories = 0;
                if (timeString != null && timeString.length() > 0)
                    calories = calculateBurnedCalories(levelAndMETList, timeString);
                if (calories != -1)
                    caloriesTextView.setText(String.valueOf(calories));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initFirstInstructions();

    }

    private void initFirstInstructions() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setContentTextColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));
        config.setMaskColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimaryDark,null));
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "99");
        sequence.setConfig(config);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(intensitySpinner)
                        .setContentText(getResources().getString(R.string.clicktochooseintensity))
                        .setDismissOnTouch(true)
                        .withRectangleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(timeEditText)
                        .setContentText(getResources().getString(R.string.clicktochoosehowlong))
                        .setDismissOnTouch(true)
                        .withRectangleShape()
                        .build()
        );

        sequence.start();
    }

    private void setupIntensitySpinner(LinkedHashMap<String, Double> levelAndMETList) {
        List<String> spinnerOptions = new ArrayList<String>();
        for (String key : levelAndMETList.keySet()) {
            spinnerOptions.add(key);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerOptions);
        intensitySpinner.setAdapter(dataAdapter);
    }

    private int calculateBurnedCalories(HashMap<String, Double> levelAndMETList, String timeString) {
        String intensity = intensitySpinner.getSelectedItem().toString();
        double MET = 0;
        //get met value by the intensity
        MET = levelAndMETList.get(intensity);
        int weight = 0;
        weight = Integer.parseInt(sharedPreferences.getString("weight", "0").trim());
        int caloriesBurned = 0;
        int time = 0;
        if (timeString != null && timeString.length() > 0)
            time = Integer.parseInt(timeString);
        if (time != 0 && weight != 0) {
            //function to calculate burned calories.
            caloriesBurned = (int) ((MET * (double) weight) * ((double) time / 60.0));
            return caloriesBurned;
        } else
            return 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_to_diary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_save_add_to_diary) {
            saveToDB();
            Toast.makeText(this, getResources().getString(R.string.sportadded), Toast.LENGTH_LONG).show();
            int dateIndicator= getIntent().getIntExtra(AppContract.DATE_INDICATOR, 0);
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.putExtra(AppContract.DATE_INDICATOR,dateIndicator);
            mainActivity.putExtra(AppContract.MODE,AppContract.MODE_ADD_SPORT);
            startActivity(mainActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToDB() {
        String name = nameTextView.getText().toString();
        int calories = Integer.parseInt(caloriesTextView.getText().toString().trim());
        int time = 0;
        String timeString = timeEditText.getText().toString().trim();
        if (timeString != null && timeString.length() > 0)
            time = Integer.parseInt(timeString);
        String intensity = intensitySpinner.getSelectedItem().toString();
        Calendar calendar=Calendar.getInstance();
        //add to date the number of times the user click on prev/next day.
        int dateIndicator=getIntent().getIntExtra(AppContract.DATE_INDICATOR,0);
        calendar.add(Calendar.DAY_OF_YEAR,dateIndicator);
        Date date = calendar.getTime();

        final SportDiaryEntry entry = new SportDiaryEntry(name, calories, time, intensity, date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.mySportDao().insert(entry);
            }
        });
    }
}
