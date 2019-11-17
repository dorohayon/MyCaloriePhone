package com.example.akiscaloriephone.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.AppExecutors;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.DiaryEntry;
import com.example.akiscaloriephone.Database.FoodEntry;
import com.example.akiscaloriephone.FavoriteFoods.FavoriteFoodsActivity;
import com.example.akiscaloriephone.LoadDiaryEntryByIdViewModel;
import com.example.akiscaloriephone.LoadDiaryEntryByIdViewModelFactory;
import com.example.akiscaloriephone.LoadFoodEntryByIdViewModel;
import com.example.akiscaloriephone.LoadFoodEntryByIdViewModelFactory;
import com.example.akiscaloriephone.R;

import java.util.Calendar;
import java.util.Date;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class AddToDiaryActivity extends BaseActivity {


    private AppDatabase db;
    private TextView name;
    private TextView calories;
    private TextView size;
    private EditText quantity;
    private int foodID;
    private int oldCalories;
    private String currentMode;
    private Date insertionDateIfUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_add_to_diary, null, false);
        dl.addView(contentView, 0);
        db = AppDatabase.getInstance(getApplicationContext());
        name = findViewById(R.id.foodname_add_to_diary);
        calories = findViewById(R.id.food_calories_add_to_diary);
        size = findViewById(R.id.foodsize_add_to_diary);
        quantity = findViewById(R.id.edit_quantity_add_to_diary);
        currentMode=getIntent().getStringExtra(AppContract.MODE);
        //get the id of the food the user clicked.
        foodID = (getIntent().getIntExtra(AppContract.FOODLIST_ID, -1));
        //load to the textViewes the datails of the food the user clicked.
        loadFoodDetails(foodID);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final FoodEntry temp;
                if (currentMode.equals(AppContract.MODE_ADD_TO_DIARY) ||
                        currentMode.equals(AppContract.MODE_FAVORITE_BREAKFEST) ||
                        currentMode.equals(AppContract.MODE_FAVORITE_LUNCH) ||
                        currentMode.equals(AppContract.MODE_FAVORITE_DINNER)
                )
                    temp  = db.foodDao().loadByIdSync(foodID);
                else //Mode is Edit Food
                    temp = db.diaryDao().loadByIdSync(foodID);
                oldCalories=temp.getCalories();
                quantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (getCurrentFocus() == quantity) {
                            int newCalories = oldCalories;
                            Log.e("tag", "new calories is " + newCalories);
                            //set the calories to match the quantity the user choosed.
                            if (charSequence.length() != 0) {
                                newCalories = (int) (oldCalories * (Double.parseDouble(charSequence.toString().trim())));
                                calories.setText(String.valueOf(newCalories));
                            } else
                                calories.setText("0");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_to_diary, menu);
        final Activity activity=this;
        setInitialInstruction(activity);
        return true;
    }

    private void setInitialInstruction(final Activity activity) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View addtodiary = findViewById(R.id.menu_save_add_to_diary);
                ShowcaseConfig config = new ShowcaseConfig();
                config.setContentTextColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));
                config.setMaskColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimaryDark,null));
                config.setDelay(500); // half second between each showcase view
                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, "84");
                sequence.setConfig(config);
                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(activity)
                                .setTarget(quantity)
                                .setContentText(getResources().getString(R.string.clickheretosetthequantity))
                                .setDismissOnTouch(true)
                                .withRectangleShape()
                                .build()
                );
                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(activity)
                                .setTarget(addtodiary)
                                .setContentText(getResources().getString(R.string.clickheretosavethefood))
                                .setDismissOnTouch(true)
                                .build()
                );

                sequence.start();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_save_add_to_diary) {
            int dateIndicator=0;
            if(currentMode.equals(AppContract.MODE_FAVORITE_BREAKFEST) ||
                    currentMode.equals(AppContract.MODE_FAVORITE_LUNCH) ||
                    currentMode.equals(AppContract.MODE_FAVORITE_DINNER )){
                saveToFoodsFavorite();
                Intent favoriteFoods = new Intent(AddToDiaryActivity.this, FavoriteFoodsActivity.class);
                favoriteFoods.putExtra(AppContract.MODE,currentMode);
                startActivity(favoriteFoods);
                return true;
            }
            else {
                saveToDiary();
                Toast.makeText(this, getResources().getString(R.string.foodaddedtoyourdiary), Toast.LENGTH_LONG).show();
                 dateIndicator= getIntent().getIntExtra(AppContract.DATE_INDICATOR, 0);
                Intent mainActivity = new Intent(AddToDiaryActivity.this, MainActivity.class);
                mainActivity.putExtra(AppContract.DATE_INDICATOR,dateIndicator);
                startActivity(mainActivity);
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToFoodsFavorite() {
        String nameToDiary = name.getText().toString().trim();
        int caloriesToDiary = Integer.parseInt(calories.getText().toString().trim());
        String sizeToDiary = size.getText().toString().trim();
        double quantityToDiary = Double.parseDouble(quantity.getText().toString().trim());
        final DiaryEntry diaryEntry = new DiaryEntry(nameToDiary, caloriesToDiary, sizeToDiary, quantityToDiary);
        switch (currentMode){
            case AppContract.MODE_FAVORITE_BREAKFEST :
                //set 1 (true) to indicate this food record is favorite food record
                diaryEntry.setIsInBreakfastFavorite(1);
                Toast.makeText(this, getResources().getString(R.string.foodaddedfavoritebreakfast), Toast.LENGTH_LONG).show();
                break;
            case AppContract.MODE_FAVORITE_LUNCH :
                //set 1 (true) to indicate this food record is favorite food record
                diaryEntry.setIsInLunchFavorite(1);
                Toast.makeText(this, getResources().getString(R.string.foodaddedfavoritelunch), Toast.LENGTH_LONG).show();
                break;
            case AppContract.MODE_FAVORITE_DINNER :
                //set 1 (true) to indicate this food record is favorite food record
                diaryEntry.setIsInDinnerFavorite(1);
                Toast.makeText(this, getResources().getString(R.string.foodaddedfavoritedinner), Toast.LENGTH_LONG).show();
                break;

        }
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                    db.diaryDao().insert(diaryEntry);
                }
            }
        );
    }

    private void saveToDiary() {
        String nameToDiary = name.getText().toString().trim();
        int caloriesToDiary = Integer.parseInt(calories.getText().toString().trim());
        String sizeToDiary = size.getText().toString().trim();
        double quantityToDiary = Double.parseDouble(quantity.getText().toString().trim());
        Calendar calendar=Calendar.getInstance();
        //calculate date
        int dateIndicator=getIntent().getIntExtra(AppContract.DATE_INDICATOR,0);
        calendar.add(Calendar.DAY_OF_YEAR,dateIndicator);
        Date dateToAdd=calendar.getTime();
        final DiaryEntry diaryEntry = new DiaryEntry(nameToDiary, caloriesToDiary, sizeToDiary, quantityToDiary,dateToAdd);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (currentMode.equals(AppContract.MODE_ADD_TO_DIARY))
                    db.diaryDao().insert(diaryEntry);
                else if (currentMode.equals(AppContract.MODE_EDIT_FROM_DIARY)) {
                    //if on edit mode , set the original date insertion and id.
                    diaryEntry.setId(foodID);
                    diaryEntry.setDate(insertionDateIfUpdate);
                    db.diaryDao().update(diaryEntry);
                }
            }
        });
    }

    private void loadFoodDetails(int idToAdd) {
        if (currentMode.equals(AppContract.MODE_ADD_TO_DIARY) ||
                currentMode.equals(AppContract.MODE_FAVORITE_BREAKFEST) ||
                currentMode.equals(AppContract.MODE_FAVORITE_LUNCH) ||
                currentMode.equals(AppContract.MODE_FAVORITE_DINNER)) {
            LoadFoodEntryByIdViewModelFactory factory = new LoadFoodEntryByIdViewModelFactory(db, idToAdd);
            final LoadFoodEntryByIdViewModel loadFoodEntryByIdViewModel = ViewModelProviders.of(this, factory).get(LoadFoodEntryByIdViewModel.class);
            loadFoodEntryByIdViewModel.getFoodItem().observe(this, new Observer<FoodEntry>() {
                @Override
                public void onChanged(@Nullable FoodEntry foodEntry) {
                    loadFoodEntryByIdViewModel.getFoodItem().removeObserver(this);
                    name.setText(foodEntry.getName());
                    calories.setText(String.valueOf(foodEntry.getCalories()));
                    size.setText(foodEntry.getSize());
                    quantity.setText("1");
                }
            });
        } else {
            // mode edit food
            LoadDiaryEntryByIdViewModelFactory factory = new LoadDiaryEntryByIdViewModelFactory(db, idToAdd);
            final LoadDiaryEntryByIdViewModel loadDiaryEntryByIdViewModel = ViewModelProviders.of(this, factory).get(LoadDiaryEntryByIdViewModel.class);
            LiveData<DiaryEntry> diaryEntry = loadDiaryEntryByIdViewModel.getFoodItem();
            diaryEntry.observe(this, new Observer<DiaryEntry>() {
                @Override
                public void onChanged(DiaryEntry diaryEntry) {
                    name.setText(diaryEntry.getName());
                    calories.setText(String.valueOf(diaryEntry.getCalories()));
                    size.setText(diaryEntry.getSize());
                    quantity.setText(String.valueOf(diaryEntry.getQuantity()));
                    //save the original date of the entry.
                    insertionDateIfUpdate=diaryEntry.getDate();
                }
            });
        }

    }
}
