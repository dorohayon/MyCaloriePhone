package com.example.akiscaloriephone.UI;

import androidx.core.app.ActivityCompat;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;

import androidx.viewpager.widget.ViewPager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.Locale;


public class MainActivity extends BaseActivity {

    private TextView dateText;
    private ImageView nextDay;
    private ImageView prevDay;
    private Calendar diaryCalender;
    private SimpleDateFormat formatter;
    private OnPrevDayFoodClickListener prevFoodListener;
    private OnNextDayFoodClickListener nextFoodListener;
    private OnPrevDaySportClickListener prevSportListener;
    private OnNextDaySportClickListener nextSportListener;
    private OnBackToTodayDiaryListener onBackToTodayDiaryListener;
    private OnBackToTodaySportListener onBackToTodaySportListener;
    private int dateIndicator;


    public  int getDateIndicator(){
        return dateIndicator;
    }


    public void setPrevDayFoodListener(OnPrevDayFoodClickListener listener) {
        this.prevFoodListener = listener;
    }

    public void setNextDayFoodListener(OnNextDayFoodClickListener listener) {
        this.nextFoodListener = listener;
    }

    public void setPrevDaySportListener(OnPrevDaySportClickListener listener) {
        this.prevSportListener = listener;
    }

    public void setNextDaySportListener(OnNextDaySportClickListener listener) {
        this.nextSportListener = listener;
    }

    public void setBackToTodayDiaryListener(OnBackToTodayDiaryListener listener) {
        this.onBackToTodayDiaryListener = listener;
    }

    public void setBackToTodaySportListener(OnBackToTodaySportListener listener) {
        this.onBackToTodaySportListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        dl.addView(contentView, 0);
        dateIndicator = getIntent().getIntExtra(AppContract.DATE_INDICATOR, 0);
        final ViewPager viewPager = findViewById(R.id.mainViewPager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, 2,this);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        //check if I got to here after Inserted Sport
        String mode = getIntent().getStringExtra(AppContract.MODE);
        if (mode != null && mode.length() > 0 && mode.equals(AppContract.MODE_ADD_SPORT))
            viewPager.setCurrentItem(1);
        formatter = new SimpleDateFormat("EEEE\ndd/MM/yyyy");
        diaryCalender = Calendar.getInstance();



        prevDay = findViewById(R.id.prevDay);
        nextDay = findViewById(R.id.nextDay);
        //to correct rtl viewing
        boolean isLeftToRight = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_LTR;
        if(!isLeftToRight) {
            prevDay.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_arrow));
            nextDay.setImageDrawable(getResources().getDrawable(R.drawable.ic_left_arrow));
        }
        //set date text
        dateText = findViewById(R.id.dateText);
        diaryCalender.add(Calendar.DAY_OF_YEAR, dateIndicator);
        Date foodSavedDate = diaryCalender.getTime();
        String todayString = formatter.format(foodSavedDate);
        dateText.setText(todayString);


        prevDay.setClickable(true);
        prevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dateIndocator reduced by one because pushed on prev day button.
                dateIndicator--;
                setCalenderToMidnight(diaryCalender);
                //set the dates between 00:00 to 23:59
                diaryCalender.add(Calendar.SECOND,-1);
                final Date prevFinishTime = diaryCalender.getTime();
                diaryCalender.add(Calendar.DAY_OF_YEAR, -1);
                diaryCalender.add(Calendar.SECOND,1);
                final Date prevStartingTime = diaryCalender.getTime();
                dateText.setText(formatter.format(prevStartingTime));
                //set the listeners for the food and sport fragments.
                prevFoodListener.OnPrevDayClick(prevStartingTime, prevFinishTime);
                prevSportListener.OnPrevDayClick(prevStartingTime, prevFinishTime);
                Log.e("Test","start:"+prevStartingTime+" finish "+prevFinishTime);


            }
        });


        nextDay.setClickable(true);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dateIndocator increased by one because pushed on next day button.
                dateIndicator++;
                //set the dates between 00:00 to 23:59
                diaryCalender.add(Calendar.DAY_OF_YEAR, 1);
                setCalenderToMidnight(diaryCalender);
                final Date nextStartingTime = diaryCalender.getTime();
                diaryCalender.add(Calendar.DAY_OF_YEAR, 1);
                diaryCalender.add(Calendar.SECOND,-1);
                final Date nextFinishTime = diaryCalender.getTime();
                dateText.setText(formatter.format(nextStartingTime));
                //set the listeners for the food and sport fragments.
                nextFoodListener.OnNextDayClick(nextStartingTime, nextFinishTime);
                nextSportListener.OnNextDayClick(nextStartingTime, nextFinishTime);
                Log.e("Test","start:"+nextStartingTime+" finish "+nextFinishTime);

            }
        });


    }


    public interface OnPrevDayFoodClickListener {
        public void OnPrevDayClick(Date startingTime,Date finishTime);
    }

    public interface OnNextDayFoodClickListener {
        public void OnNextDayClick(Date startingTime,Date finishTime);
    }
    public interface OnPrevDaySportClickListener {
        public void OnPrevDayClick(Date startingTime,Date finishTime);
    }

    public interface OnNextDaySportClickListener {
        public void OnNextDayClick(Date startingTime,Date finishTime);
    }

    public interface OnBackToTodayDiaryListener {
        public void OnBackToTodayClick(Date startingTime,Date finishTime);
    }

    public interface OnBackToTodaySportListener {
        public void OnBackToTodayClick(Date startingTime,Date finishTime);
    }

    private void setCalenderToMidnight(Calendar calender) {
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_return_to_today) {
            //return to today's date
            dateIndicator = 0;
            diaryCalender = Calendar.getInstance();
            dateText.setText(formatter.format(diaryCalender.getTime()));
            setCalenderToMidnight(diaryCalender);
            Date startingTime = diaryCalender.getTime();
            diaryCalender.add(Calendar.DAY_OF_YEAR, 1);
            diaryCalender.add(Calendar.SECOND,-1);
            Date finishTime = diaryCalender.getTime();
            //set the sport and food listeners.
            onBackToTodaySportListener.OnBackToTodayClick(startingTime,finishTime);
            onBackToTodayDiaryListener.OnBackToTodayClick(startingTime,finishTime);
            Log.e("Test","start:"+startingTime+" finish "+finishTime);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    //exit the app when back pressed from main activity.
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
    }




}
