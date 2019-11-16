package com.example.akiscaloriephone.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numOfPages;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior, int numOfPages) {
        super(fm, behavior);
        this.numOfPages = numOfPages;
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
                return "Food";
            case 1:
                return "Sport";
            default:
                return "Food";
        }
    }
}
