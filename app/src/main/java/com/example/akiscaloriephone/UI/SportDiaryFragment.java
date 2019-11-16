package com.example.akiscaloriephone.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.SportDiaryEntry;
import com.example.akiscaloriephone.LoadSportBetweenDatesViewModel;
import com.example.akiscaloriephone.LoadSportBetweenDatesViewModelFactory;
import com.example.akiscaloriephone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;



public class SportDiaryFragment extends Fragment implements MainActivity.OnNextDaySportClickListener, MainActivity.OnPrevDaySportClickListener ,MainActivity.OnBackToTodaySportListener, SensorEventListener {

    private FloatingActionButton addSportButton;
    private MySportsAdapter mySportsAdapter;
    private Calendar diaryCalender;
    private AppDatabase db;
    private int dateIndicator;
    private SensorManager sensorManager;
    private Sensor countSensor;
    private SharedPreferences sharedPreferences;
    private TextView stepsCount;






    public SportDiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).setPrevDaySportListener(this);
        ((MainActivity) getActivity()).setNextDaySportListener(this);
        ((MainActivity) getActivity()).setBackToTodaySportListener(this);

    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_diary, menu);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sport_diary, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //register to show steps counter
        if (sharedPreferences.getBoolean("stepCounterSwitch", false)) {
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        //set the steps counter
        SharedPreferences.Editor editor = sharedPreferences.edit();
        stepsCount = (TextView) view.findViewById(R.id.totalSteps);
        stepsCount.setText("Total steps: " + sharedPreferences.getFloat(AppContract.STEP_PREFENCES, 0));
        db=AppDatabase.getInstance(getContext());

        // set up the RecyclerView of sports
        RecyclerView recyclerViewSports = view.findViewById(R.id.recyclerViewMySports);
        recyclerViewSports.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration sportsdividerItemDecoration = new DividerItemDecoration(recyclerViewSports.getContext(), VERTICAL);
        recyclerViewSports.addItemDecoration(sportsdividerItemDecoration);
        mySportsAdapter = new MySportsAdapter(getContext());
        recyclerViewSports.setAdapter(mySportsAdapter);
        dateIndicator = ((MainActivity)getActivity()).getDateIndicator();

        //set add sport button
        addSportButton=view.findViewById(R.id.addSportFloatButton);
        addSportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateIndicator = ((MainActivity)getActivity()).getDateIndicator();
                Intent intent=new Intent(getActivity(),SportsListActivity.class);
                intent.putExtra(AppContract.DATE_INDICATOR,dateIndicator);
                startActivity(intent);
            }
        });

        diaryCalender = Calendar.getInstance();
        diaryCalender.add(Calendar.DAY_OF_YEAR, dateIndicator);
        //statring time is 00:00 and end time is 23:59 day after
        setCalenderToMidnight(diaryCalender);
        Date startingTime = diaryCalender.getTime();
        diaryCalender.add(Calendar.DAY_OF_YEAR, 1);
        diaryCalender.add(Calendar.SECOND, -1);
        Date finishTime = diaryCalender.getTime();
        Log.e("Test","start:"+startingTime+" finish "+finishTime);


        //show today's sports
        LoadSportBetweenDatesViewModelFactory factory = new LoadSportBetweenDatesViewModelFactory( db, startingTime,finishTime);
        final LoadSportBetweenDatesViewModel loadSportBetweenDatesViewModel = ViewModelProviders.of(this, factory).get(LoadSportBetweenDatesViewModel.class);
        LiveData<List<SportDiaryEntry>> todaysport = loadSportBetweenDatesViewModel.getSport();
        todaysport.observe(this, new Observer<List<SportDiaryEntry>>() {
            @Override
            public void onChanged(List<SportDiaryEntry> mySportEntries) {
                mySportsAdapter.setMySport(mySportEntries);
            }


        });


        ViewPager viewPager=getActivity().findViewById(R.id.mainViewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //if im on sport fragment on first time - run instruction.
                if(position==1)
                    instractionFirstTime();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return view;
    }
    public void instractionFirstTime() {
            new MaterialShowcaseView.Builder(getActivity())
                    .setTarget(addSportButton)
                    .setDismissOnTouch(true)
                    .setDelay(500)
                    .singleUse("8")
                    .withCircleShape()
                    .setContentText("here you choose sport to add")
                    .show();

    }

    private void setCalenderToMidnight(Calendar calender) {
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
    }


    @Override
    public void OnPrevDayClick(Date startingTime, Date finishTime) {
        LiveData<List<SportDiaryEntry>> todaysport = db.mySportDao().findSportsBetweenDates(startingTime,finishTime);
        todaysport.observe(this, new Observer<List<SportDiaryEntry>>() {
            @Override
            public void onChanged(List<SportDiaryEntry> mySportEntries) {
                mySportsAdapter.setMySport(mySportEntries);
            }


        });
        setStepCounter(startingTime);
    }

    @Override
    public void OnNextDayClick(Date startingTime, Date finishTime) {
        LiveData<List<SportDiaryEntry>> todaysport =db.mySportDao().findSportsBetweenDates(startingTime,finishTime);
        todaysport.observe(this, new Observer<List<SportDiaryEntry>>() {
            @Override
            public void onChanged(List<SportDiaryEntry> mySportEntries) {
                mySportsAdapter.setMySport(mySportEntries);
            }


        });
        setStepCounter(startingTime);

    }

    @Override
    public void OnBackToTodayClick(Date startingTime, Date finishTime) {
        LiveData<List<SportDiaryEntry>> todaySport = db.mySportDao().findSportsBetweenDates(startingTime,finishTime);
        todaySport.observe(this, new Observer<List<SportDiaryEntry>>() {
            @Override
            public void onChanged(List<SportDiaryEntry> mySportEntries) {
                mySportsAdapter.setMySport(mySportEntries);
            }
        });
        setStepCounter(startingTime);

    }

    private void setStepCounter(Date startingTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String todayTimeString = simpleDateFormat.format(today);
        String startingTimeString = simpleDateFormat.format(startingTime);
        float steps = 0;
        if (startingTimeString.equals(todayTimeString))
            steps = sharedPreferences.getFloat(AppContract.STEP_PREFENCES, 0);
        else
            steps = sharedPreferences.getFloat("steps" + startingTimeString, 0);
        stepsCount.setText("Total steps: " + steps);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sharedPreferences.getBoolean("stepCounterSwitch", false))
            stepsCount.setText("Total steps: " + sharedPreferences.getFloat(AppContract.STEP_PREFENCES, 0));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



}
