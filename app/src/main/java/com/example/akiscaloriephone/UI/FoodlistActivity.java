package com.example.akiscaloriephone.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.Dialogs.FoodlistSortByDialog;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.FoodEntry;
import com.example.akiscaloriephone.FoodListViewModel;
import com.example.akiscaloriephone.R;

import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class FoodlistActivity extends BaseActivity implements FoodListAdapter.ItemClickListener {



    private FoodListAdapter foodListAdapter;
    private AppDatabase db;
    private TextView nameTextView;
    private TextView caloriesTextView;
    private TextView sizeTextView;
    private SearchView searchView;
    private int dateIndicator;
    private String currentMode;
    private Button sortByButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_foodlist, null, false);
        dl.addView(contentView, 0);

        currentMode=getIntent().getStringExtra(AppContract.MODE);
        dateIndicator=getIntent().getIntExtra(AppContract.DATE_INDICATOR,0);
        nameTextView = findViewById(R.id.list_item_name);
        caloriesTextView = findViewById(R.id.list_item_calories);
        db = AppDatabase.getInstance(getApplicationContext());
        final RecyclerView recyclerViewFoodList = findViewById(R.id.recyclerViewFoodList);
        recyclerViewFoodList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewFoodList.getContext(), VERTICAL);
        recyclerViewFoodList.addItemDecoration(dividerItemDecoration);
        foodListAdapter = new FoodListAdapter(this, this);
        recyclerViewFoodList.setAdapter(foodListAdapter);
        sortByButton=findViewById(R.id.sortByButton);
        sortByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodlistSortByDialog dialog=new FoodlistSortByDialog(foodListAdapter);
                dialog.show(getSupportFragmentManager(),"foodOrder");
            }
        });
        setupViewModel();
        searchView=(SearchView)findViewById(R.id.search_foodlist);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                foodListAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    private void setupViewModel() {
        FoodListViewModel viewModel = ViewModelProviders.of(this).get(FoodListViewModel.class);
        viewModel.getFoodList().observe(this, new Observer<List<FoodEntry>>() {
            @Override
            public void onChanged(@Nullable List<FoodEntry> foodEntries) {
                foodListAdapter.setFoods(foodEntries);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent addIntent = new Intent(FoodlistActivity.this, AddToDiaryActivity.class);
        addIntent.putExtra(AppContract.MODE,currentMode);
        addIntent.putExtra(AppContract.FOODLIST_ID, itemId);
        addIntent.putExtra(AppContract.DATE_INDICATOR,dateIndicator);
        Log.e("bla","Mode is "+currentMode);
        startActivity(addIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_foodlist, menu);
        final Activity activity=this;
        initFirstInstruction(activity);
        return true;
    }

    private void initFirstInstruction(final Activity activity) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View addnewfood = findViewById(R.id.add_new_food_foodlist);
                ShowcaseConfig config = new ShowcaseConfig();
                config.setContentTextColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));
                config.setMaskColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimaryDark,null));
                config.setDelay(500); // half second between each showcase view
                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, "80");
                sequence.setConfig(config);
                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(activity)
                                .setTarget(addnewfood)
                                .setContentText(getResources().getString(R.string.herecreatenewfood))
                                .setDismissOnTouch(true)
                                .build()
                );
                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(activity)
                                .setTarget(sortByButton)
                                .setContentText(getResources().getString(R.string.herechoosehowtosort))
                                .setDismissOnTouch(true)
                                .build()
                );

                sequence.addSequenceItem(
                        new MaterialShowcaseView.Builder(activity)
                                .setTarget(searchView)
                                .setContentText(getResources().getString(R.string.heresearch))
                                .setDismissOnTouch(true)
                                .withRectangleShape()
                                .build()
                );

                sequence.start();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_new_food_foodlist) {
            Intent intent = new Intent(this, NewFoodToFoodlistListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
