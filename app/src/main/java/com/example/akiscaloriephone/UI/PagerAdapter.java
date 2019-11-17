package com.example.akiscaloriephone.UI;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.akiscaloriephone.R;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private  int numOfPages;
    private Context context;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior, int numOfPages,Context context) {
        super(fm, behavior);
        this.numOfPages = numOfPages;
        this.context=context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FoodDiaryFragment foodDiaryFragment = new FoodDiaryFragment();
                return foodDiaryFragment;
            case 1:
                SportDiaryFragment sportDiaryFragment = new SportDiaryFragment();
                return sportDiaryFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfPages;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.food);
            case 1:
                return context.getResources().getString(R.string.sport);
            default:
                return context.getResources().getString(R.string.food);
        }
    }
}
