package com.example.akiscaloriephone.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.example.akiscaloriephone.UI.FoodListAdapter;
import com.example.akiscaloriephone.LoadFoodsByCaloriesViewModel;
import com.example.akiscaloriephone.LoadFoodsByIdViewModel;
import com.example.akiscaloriephone.LoadFoodsByNameViewModel;
import com.example.akiscaloriephone.R;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.FoodEntry;

import java.util.List;

public class FoodlistSortByDialog extends DialogFragment {
    public static final String KEY_ORDER_BY_PREFS = "order_by_foodlist";
    public FoodListAdapter adapter;

    public FoodlistSortByDialog(FoodListAdapter adapter) {
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        int checked=0;
        switch (sharedPreferences.getString(KEY_ORDER_BY_PREFS,"Name")){
            case "Name":
                checked=0;
                break;
            case "Calories":
                checked=1;
                break;
            case "Recently added":
                checked=2;
                break;
            default:
                checked=0;
        }
// 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle("Please choose foods order")
                .setSingleChoiceItems(R.array.foodsOrderArray,checked, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppDatabase db=AppDatabase.getInstance(getContext());
                        switch (i) {
                            case 0:
                                //save order by prefs
                                editor.putString(KEY_ORDER_BY_PREFS, "Name").commit();
                                dialogInterface.dismiss();
                                final LoadFoodsByNameViewModel loadFoodsByNameViewModel = ViewModelProviders.of(getActivity()).get(LoadFoodsByNameViewModel.class);

                                loadFoodsByNameViewModel.getFoodEntries().observe(getActivity(), new Observer<List<FoodEntry>>() {
                                    @Override
                                    public void onChanged(List<FoodEntry> foodEntries) {
                                        adapter.setFoods(foodEntries);
                                    }
                                });
                                break;
                            case 1:
                                editor.putString(KEY_ORDER_BY_PREFS, "Calories").commit();
                                dialogInterface.dismiss();
                                final LoadFoodsByCaloriesViewModel loadFoodsByCaloriesViewModel = ViewModelProviders.of(getActivity()).get(LoadFoodsByCaloriesViewModel.class);
                                loadFoodsByCaloriesViewModel.getFoodEntries().observe(getActivity(), new Observer<List<FoodEntry>>() {
                                    @Override
                                    public void onChanged(List<FoodEntry> foodEntries) {
                                        adapter.setFoods(foodEntries);
                                    }
                                });
                                break;
                            case 2:
                                editor.putString(KEY_ORDER_BY_PREFS, "Recently added").commit();
                                dialogInterface.dismiss();
                                final LoadFoodsByIdViewModel loadFoodsByIdViewModel = ViewModelProviders.of(getActivity()).get(LoadFoodsByIdViewModel.class);
                              loadFoodsByIdViewModel.getFoodEntries().observe(getActivity(), new Observer<List<FoodEntry>>() {
                                    @Override
                                    public void onChanged(List<FoodEntry> foodEntries) {
                                        adapter.setFoods(foodEntries);
                                    }
                                });
                                break;
                            default:
                                editor.putString(KEY_ORDER_BY_PREFS, "Name").commit();
                                dialogInterface.dismiss();
                                final LoadFoodsByNameViewModel loadFoodsDefaultViewModel = ViewModelProviders.of(getActivity()).get(LoadFoodsByNameViewModel.class);
                                loadFoodsDefaultViewModel.getFoodEntries().observe(getActivity(), new Observer<List<FoodEntry>>() {
                                    @Override
                                    public void onChanged(List<FoodEntry> foodEntries) {
                                        adapter.setFoods(foodEntries);
                                    }
                                });
                        }
                    }
                });


// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        return builder.create();
    }
}
