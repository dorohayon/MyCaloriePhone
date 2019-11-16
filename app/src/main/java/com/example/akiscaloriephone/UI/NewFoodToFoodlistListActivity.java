package com.example.akiscaloriephone.UI;

import androidx.annotation.NonNull;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.akiscaloriephone.AppExecutors;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.FoodEntry;
import com.example.akiscaloriephone.R;

public class NewFoodToFoodlistListActivity extends BaseActivity {
    private EditText name;
    private EditText calories;
    private Spinner size;
    private AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.editor_activity, null, false);
        dl.addView(contentView, 0);
        name=findViewById(R.id.edit_name_editor);
        calories=findViewById(R.id.edit_calories_editor);
        size=findViewById(R.id.edit_serving_size_editor);
        db=AppDatabase.getInstance(getApplicationContext());
    }

    private void saveToDB() {
        String nameToSave=name.getText().toString().trim();
        int caloriesToSave=Integer.parseInt(calories.getText().toString().trim());
        String sizeToSave=size.getSelectedItem().toString();
        final FoodEntry foodEntry=new FoodEntry(nameToSave,caloriesToSave,sizeToSave);
        AppExecutors appExecutors=AppExecutors.getInstance();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.foodDao().insert(foodEntry);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.editor_menu_save){
            saveToDB();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
