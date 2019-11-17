package com.example.akiscaloriephone.FavoriteFoods;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.UI.BaseActivity;
import com.example.akiscaloriephone.UI.DiaryAdapter;
import com.example.akiscaloriephone.UI.FoodlistActivity;
import com.example.akiscaloriephone.R;
import com.example.akiscaloriephone.UI.SettingsActivity;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.DiaryEntry;

import java.util.List;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class FavoriteFoodsActivity extends BaseActivity implements DiaryAdapter.ItemClickListener {
    private Button addFavoriteButton;
    private AppDatabase db;
    private RecyclerView recyclerView;
    private String favoriteFoodMode; //breakfast OR Lunch OR Dinner
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_favorite_foods, null, false);
        dl.addView(contentView, 0);
        //check the type of favorite food the user pushed.
        favoriteFoodMode=getIntent().getStringExtra(AppContract.MODE);
        db = AppDatabase.getInstance(getApplicationContext());
        addFavoriteButton = findViewById(R.id.add_favorite_button);
        recyclerView=findViewById(R.id.recycler_view_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        final DiaryAdapter diaryAdapter = new DiaryAdapter(this, this);
        recyclerView.setAdapter(diaryAdapter);
        LiveData<List<DiaryEntry>> favFoods=new LiveData<List<DiaryEntry>>() {};
        switch (favoriteFoodMode){
            case AppContract.MODE_FAVORITE_BREAKFEST :
                favFoods= db.diaryDao().loadBreakfastFavirites();
                addFavoriteButton.setText(getResources().getString(R.string.addnewfoodbreakfastfavorite));
                addFavoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent addFavoriteBreakfast = new Intent(FavoriteFoodsActivity.this, FoodlistActivity.class);
                        addFavoriteBreakfast.putExtra(AppContract.MODE, AppContract.MODE_FAVORITE_BREAKFEST);
                        startActivity(addFavoriteBreakfast);
                    }
                });
                break;
            case AppContract.MODE_FAVORITE_LUNCH :
                favFoods= db.diaryDao().loadLunchFavirites();
                addFavoriteButton.setText(getResources().getString(R.string.addnewfoodlunchfavorite));
                addFavoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent addFavoriteBreakfast = new Intent(FavoriteFoodsActivity.this, FoodlistActivity.class);
                        addFavoriteBreakfast.putExtra(AppContract.MODE, AppContract.MODE_FAVORITE_LUNCH);
                        startActivity(addFavoriteBreakfast);
                    }
                });

                break;
            case AppContract.MODE_FAVORITE_DINNER :
                favFoods= db.diaryDao().loadDinnerFavirites();
                addFavoriteButton.setText(getResources().getString(R.string.addnewfooddinnerfavorite));
                addFavoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent addFavoriteBreakfast = new Intent(FavoriteFoodsActivity.this, FoodlistActivity.class);
                        addFavoriteBreakfast.putExtra(AppContract.MODE, AppContract.MODE_FAVORITE_DINNER);
                        startActivity(addFavoriteBreakfast);
                    }
                });

                break;
        }


        favFoods.observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(List<DiaryEntry> diaryEntries) {
                diaryAdapter.updateData(diaryEntries);
            }
        });



    }



    @Override
    public void onItemClickListener(int itemId) {

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
