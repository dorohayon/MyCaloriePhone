package com.example.akiscaloriephone.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.AppExecutors;
import com.example.akiscaloriephone.Database.AppDatabase;
import com.example.akiscaloriephone.Database.DiaryEntry;
import com.example.akiscaloriephone.Database.FoodEntry;
import com.example.akiscaloriephone.LoadFoodBetweenDatesViewModel;
import com.example.akiscaloriephone.LoadFoodBetweenDatesViewModelFactory;
import com.example.akiscaloriephone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;


public class FoodDiaryFragment extends Fragment implements DiaryAdapter.ItemClickListener , MainActivity.OnPrevDayFoodClickListener, MainActivity.OnNextDayFoodClickListener,MainActivity.OnBackToTodayDiaryListener {



    private AppDatabase db;
    private DiaryAdapter diaryAdapter;
    private TextView totalCalories;
    private Calendar diaryCalender;
    private ArrayList<FoodEntry> firstTimeFoods;
    private int dateIndicator;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBarCalories;
    private TextView destinationCalories;
    private TextView enterDatailsTextView;
    private MySportsAdapter mySportsAdapter;




    public FoodDiaryFragment() {
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
        ((MainActivity) getActivity()).setPrevDayFoodListener(this);
        ((MainActivity) getActivity()).setNextDayFoodListener(this);
        ((MainActivity) getActivity()).setBackToTodayDiaryListener(this);

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_diary, menu);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_food_diary, container, false);
        totalCalories=view.findViewById(R.id.totalCalories);
        progressBarCalories = view.findViewById(R.id.totalCaloriesProgressBar);
        destinationCalories = view.findViewById(R.id.destinationCalories);
        totalCalories.setVisibility(View.INVISIBLE);
        destinationCalories.setVisibility(View.INVISIBLE);
        progressBarCalories.setVisibility(View.INVISIBLE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        enterDatailsTextView=view.findViewById(R.id.enterDetailsTextView);
        enterDatailsTextView.setClickable(true);
        enterDatailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SettingsActivity.class));
            }
        });
        db=AppDatabase.getInstance(getContext());
        calculateTargetCaloriesPerDay();



        //Only on first run!! add the foods to the foodlist
        if (sharedPreferences.getBoolean("firstTimeInitDatabase", true)) {
            Log.d("Comments", "First time");
            firstTimeFoods = new ArrayList<>();
            makeFirstFoods();
            sharedPreferences.edit().putBoolean("firstTimeInitDatabase", false).commit();

        }


        // set up the RecyclerView of foods
        final RecyclerView recyclerViewMain = view.findViewById(R.id.recyclerViewMain);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewMain.getContext(), VERTICAL);
        recyclerViewMain.addItemDecoration(dividerItemDecoration);
        diaryAdapter = new DiaryAdapter(getContext(), this);
        recyclerViewMain.setAdapter(diaryAdapter);
        dateIndicator = ((MainActivity)getActivity()).getDateIndicator();

        //set the add food button
        final FloatingActionButton plusButton = view.findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateIndicator = ((MainActivity)getActivity()).getDateIndicator();
                Intent foodListIntent = new Intent(getActivity(), FoodlistActivity.class);
                //date indicator to know to which date to add the food.
                foodListIntent.putExtra(AppContract.DATE_INDICATOR, dateIndicator);
                foodListIntent.putExtra(AppContract.MODE, AppContract.MODE_ADD_TO_DIARY);
                startActivity(foodListIntent);
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

        LoadFoodBetweenDatesViewModelFactory factory = new LoadFoodBetweenDatesViewModelFactory( db, startingTime,finishTime);
        LoadFoodBetweenDatesViewModel loadFoodBetweenDatesViewModel = ViewModelProviders.of(this, factory).get(LoadFoodBetweenDatesViewModel.class);

        //show today's foods
        final LiveData<List<DiaryEntry>> todayFoods = loadFoodBetweenDatesViewModel.getFood();
        todayFoods.observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(List<DiaryEntry> diaryEntries) {
                diaryAdapter.setDiaryEntries(diaryEntries);
                int total = getTotalCalories(diaryEntries);
                totalCalories.setText(getResources().getString(R.string.current)+":\n" + String.valueOf(total)+" "+getResources().getString(R.string.calories));
                progressBarCalories.setProgress(total);
                //show instruction when first food added to diary.
                if(diaryEntries.size()>0)
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(recyclerViewMain)
                            .setContentText(getResources().getString(R.string.Clicktomodifyfoodlongclicktodelete))
                            .setDismissOnTouch(true)
                            .setShapePadding(50)
                            .withRectangleShape()
                            .singleUse("15")
                            .show();
            }
        });


        instructionFirstTime(recyclerViewMain, plusButton);

        return view;

    }

    private void instructionFirstTime(RecyclerView recyclerViewMain, FloatingActionButton plusButton) {
        ImageView prevDay=getActivity().findViewById(R.id.prevDay);
        ImageView nextDay=getActivity().findViewById(R.id.nextDay);
        ShowcaseConfig config = new ShowcaseConfig();
        config.setContentTextColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));
        config.setMaskColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimaryDark,null));
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "146");
        sequence.setConfig(config);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(enterDatailsTextView)
                        .withRectangleShape()
                        .setContentText("Hello, Welcome to My CaloriePhone !\nIn order to calculate recommended values, You need to set your details.\nYou can perform it by this button or from settings.")
                        .setDismissOnTouch(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(plusButton)
                        .setContentText("here you Choose food to add to your diary")
                        .setDismissOnTouch(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(prevDay)
                        .setContentText("here you switch to previous day")
                        .setDismissOnTouch(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(nextDay)
                        .setContentText("here you switch to next day")
                        .setDismissOnTouch(true)
                        .build()
        );
        sequence.start();
    }


    private void calculateTargetCaloriesPerDay() {
        String gender = sharedPreferences.getString("Gender", "0");
        String ageString = (sharedPreferences.getString("age", "0").trim());
        String heightString = (sharedPreferences.getString("height", "0").trim());
        String weightString = (sharedPreferences.getString("weight", "0").trim());
        String sportLevelString = (sharedPreferences.getString("LevelOfPhysicalActivity", "0").trim());
        String destinationWeightString = (sharedPreferences.getString("weightTarget", "0").trim());
        String changeSpeedString = sharedPreferences.getString("WeightChangeSpeed", "-1").trim();
        //check the user entered all details
        if      (gender.length()<1 | gender.equals("0") |
                ageString.length() < 1 | ageString.equals("0") |
                heightString.length() < 1 | heightString .equals("0")|
                weightString.length() < 1 | weightString .equals("0")|
                sportLevelString.length() < 1 | sportLevelString .equals("0") |
                destinationWeightString.length() < 1 | destinationWeightString .equals("0") |
                changeSpeedString.length() < 1 | changeSpeedString .equals("-1")) {
                return;
        }
        int age = Integer.parseInt(ageString);
        int height = Integer.parseInt(heightString);
        int weight = Integer.parseInt(weightString);
        int sportLevel = Integer.parseInt(sportLevelString);
        int destinationWeight = Integer.parseInt(destinationWeightString);
        int caloriesToReduceOrGainPerDay = 0;
        switch (changeSpeedString) {
            case "0":
                caloriesToReduceOrGainPerDay = 0;
                break;
            case "0.25":
                caloriesToReduceOrGainPerDay = (int) (8000 * 0.25 / 7.0);
                break;
            case "0.5":
                caloriesToReduceOrGainPerDay = (int) (8000 * 0.5 / 7.0);
                break;
            case "0.75":
                caloriesToReduceOrGainPerDay = (int) (8000 * 0.75 / 7.0);
                break;
            case "1":
                caloriesToReduceOrGainPerDay = (int) (8000 * 1.0 / 7.0);
                break;
        }
        double BMR = 0;
        if (gender.equals(getResources().getString(R.string.male)))
            BMR =  (66.0 + weight * 13.8 + height * 5.0 - age * 6.8);
        if (gender.equals(getResources().getString(R.string.female)))
            BMR = (655.0 + weight * 9.6 + height * 1.8 - age * 4.7);
        double dailyCaloriesExpense = 0;
        switch (sportLevel) {
            case 1:
                dailyCaloriesExpense =  (BMR * 1.2);
                break;
            case 2:
                dailyCaloriesExpense = (BMR * 1.375);
                break;
            case 3:
                dailyCaloriesExpense = (BMR * 1.55);
                break;
            case 4:
                dailyCaloriesExpense = (BMR * 1.729);
                break;
            case 5:
                dailyCaloriesExpense =  (BMR * 1.9);
                break;
        }
        dailyCaloriesExpense =  (dailyCaloriesExpense * 1.1);
        //check if the user want to loss or gain weight
        boolean toReduce = false;
        if (destinationWeight < weight)
            toReduce = true;
        double caloriesToEatPerDay = 0;
        if (toReduce) {
            caloriesToEatPerDay = dailyCaloriesExpense - caloriesToReduceOrGainPerDay;
        } else {
            caloriesToEatPerDay = dailyCaloriesExpense + caloriesToReduceOrGainPerDay;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("caloriesToEatPerDay", (int)caloriesToEatPerDay);
        progressBarCalories.setVisibility(View.VISIBLE);
        progressBarCalories.setMax((int)caloriesToEatPerDay);
        destinationCalories.setVisibility(View.VISIBLE);
        destinationCalories.setText(getResources().getString(R.string.destination)+":\n" + (int) caloriesToEatPerDay+" "+getResources().getString(R.string.calories));
        totalCalories.setVisibility(View.VISIBLE);
        enterDatailsTextView.setVisibility(View.INVISIBLE);
    }


    private void setCalenderToMidnight(Calendar calender) {
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
    }

    private int getTotalCalories(List<DiaryEntry> diaryEntries) {
        int total = 0;
        for (DiaryEntry entry : diaryEntries) {
            total = total + entry.getCalories();
        }
        return total;
    }




    private int gramsToUnit(int caloriesTo100Gram,int unitWeight){
        double oneGramCalories=caloriesTo100Gram/100.0;
        double result=oneGramCalories*unitWeight;
        return (int) result;
    }



    @Override
    public void onItemClickListener(int itemId) {
        Intent editOnDiary = new Intent(getActivity(), AddToDiaryActivity.class);
        editOnDiary.putExtra(AppContract.MODE, AppContract.MODE_EDIT_FROM_DIARY);
        editOnDiary.putExtra(AppContract.FOODLIST_ID, itemId);
        editOnDiary.putExtra(AppContract.DATE_INDICATOR, dateIndicator);
        startActivity(editOnDiary);
    }

    @Override
    public void OnPrevDayClick(Date startingTime, Date finishTime) {
        Log.e("PrevDayClick","start:"+startingTime+" finish "+finishTime);
//        LoadFoodBetweenDatesViewModelFactory factory = new LoadFoodBetweenDatesViewModelFactory( db, startingTime,finishTime);
//        LoadFoodBetweenDatesViewModel loadFoodBetweenDatesViewModel = ViewModelProviders.of(this, factory).get(LoadFoodBetweenDatesViewModel.class);
        final LiveData<List<DiaryEntry>> prevFoods =db.diaryDao().findDiaryFoodsBetweenDates(startingTime,finishTime);
        prevFoods.observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(List<DiaryEntry> diaryEntries) {
                diaryAdapter.updateData(diaryEntries);
                int total = getTotalCalories(diaryEntries);
                totalCalories.setText(getResources().getString(R.string.current)+":\n" + String.valueOf(total)+" "+getResources().getString(R.string.calories));
                progressBarCalories.setProgress(total);

            }
        });

    }

    @Override
    public void OnNextDayClick(Date startingTime, Date finishTime) {
        Log.e("NextDayClick","start:"+startingTime+" finish "+finishTime);
//        LoadFoodBetweenDatesViewModelFactory factory = new LoadFoodBetweenDatesViewModelFactory( db, startingTime,finishTime);
//        LoadFoodBetweenDatesViewModel loadFoodBetweenDatesViewModel = ViewModelProviders.of(this, factory).get(LoadFoodBetweenDatesViewModel.class);
        final LiveData<List<DiaryEntry>> nextFoods = db.diaryDao().findDiaryFoodsBetweenDates(startingTime,finishTime);
        nextFoods.observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(List<DiaryEntry> diaryEntries) {
                diaryAdapter.updateData(diaryEntries);
                int total = getTotalCalories(diaryEntries);
                totalCalories.setText(getResources().getString(R.string.current)+":\n" + String.valueOf(total)+" "+getResources().getString(R.string.calories));
                progressBarCalories.setProgress(total);
            }
        });
    }

    @Override
    public void OnBackToTodayClick(Date startingTime, Date finishTime) {
//        LoadFoodBetweenDatesViewModelFactory factory = new LoadFoodBetweenDatesViewModelFactory( db, startingTime,finishTime);
//        LoadFoodBetweenDatesViewModel loadFoodBetweenDatesViewModel = ViewModelProviders.of(this, factory).get(LoadFoodBetweenDatesViewModel.class);
        final LiveData<List<DiaryEntry>> todaysfoods = db.diaryDao().findDiaryFoodsBetweenDates(startingTime,finishTime);
        todaysfoods.observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(List<DiaryEntry> diaryEntries) {
                diaryAdapter.setDiaryEntries(diaryEntries);
                int total = getTotalCalories(diaryEntries);
                totalCalories.setText(getResources().getString(R.string.current)+":\n" + String.valueOf(total)+" "+getResources().getString(R.string.calories));
                progressBarCalories.setProgress(total);
            }
        });
    }

    public void makeFirstFoods() {
        firstTimeFoods.add(new FoodEntry("Avocado",gramsToUnit(160,174) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("אבוקדו",gramsToUnit(160,174) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Pear",gramsToUnit(58,186) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("אגס",gramsToUnit(58,186) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Pineapple",gramsToUnit(50,650) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("אננס",gramsToUnit(50,650) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Watermelon",gramsToUnit(30,250) ,"Slice"));
        firstTimeFoods.add(new FoodEntry("אבטיח",gramsToUnit(30,250) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("אפרסמון",gramsToUnit(70,157) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Persimmon",gramsToUnit(70,157) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Peach",gramsToUnit(39,173) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("אפרסק",gramsToUnit(39,173) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Grapefruit",gramsToUnit(42,250) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("אשכוליות",gramsToUnit(42,250) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Banana",gramsToUnit(39,101) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("בננה",gramsToUnit(39,101) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Guava",68 ,"100 gram"));
        firstTimeFoods.add(new FoodEntry("גויאבה",68 ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("Quince",57 ,"100 gram"));
        firstTimeFoods.add(new FoodEntry("חבוש",57 ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("Lychee",gramsToUnit(66,8) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("ליצ'י",gramsToUnit(66,8) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("מלון",gramsToUnit(34,57) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("Melon",gramsToUnit(34,57) ,"Slice"));
        firstTimeFoods.add(new FoodEntry("מנגו",gramsToUnit(65,312) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Mango",gramsToUnit(65,312) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("משמש",gramsToUnit(48,34) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Apricot",gramsToUnit(48,34) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("Nactarine",gramsToUnit(44,100) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("נקטרינה",gramsToUnit(44,100) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("ענבים",gramsToUnit(69,6) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Grape",gramsToUnit(69,6) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("Raspberry",gramsToUnit(52,5) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("פטל",gramsToUnit(52,5) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("קיווי",gramsToUnit(61,62) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Kiwi",gramsToUnit(61,62) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("Klementine",gramsToUnit(47,103) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("קלמנטינה",gramsToUnit(47,103) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Pomegranate",gramsToUnit(83,29) ,"Spoon"));
        firstTimeFoods.add(new FoodEntry("רימון",gramsToUnit(83,29) ,"Spoon"));
        firstTimeFoods.add(new FoodEntry("שזיף",gramsToUnit(46,135) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Plum",gramsToUnit(46,135) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("Loquat",gramsToUnit(47,24) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("שסק",gramsToUnit(47,24) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Fig",gramsToUnit(74,63) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("תאנה",gramsToUnit(74,63) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Strawberry",gramsToUnit(32,20) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("תות",gramsToUnit(32,20) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Orange",gramsToUnit(47,209) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("תפוז",gramsToUnit(47,209) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Apple",gramsToUnit(52,185) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("אבטיח",gramsToUnit(52,185) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("Passion fruit",gramsToUnit(97,60) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("פסיפלורה",gramsToUnit(97,60) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("אוכמניות",gramsToUnit(57,3) ,"Spoon"));
        firstTimeFoods.add(new FoodEntry("Blueberry",gramsToUnit(57,3) ,"Spoon"));
        firstTimeFoods.add(new FoodEntry("Coconut",gramsToUnit(354,325) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("קוקוס",gramsToUnit(354,325) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Cherry",gramsToUnit(63,8) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("דובדבן",gramsToUnit(63,8) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Pomelo",gramsToUnit(38,250) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("פומלה",gramsToUnit(38,250) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Palm",gramsToUnit(284,17) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("Olive",gramsToUnit(145,2) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("זית",gramsToUnit(145,2) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Lemon",gramsToUnit(20,129) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("לימון",gramsToUnit(20,129) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Potato",gramsToUnit(75,175) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("תפוח אדמה",gramsToUnit(75,175) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Carrot",gramsToUnit(41,136) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("גזר",gramsToUnit(41,136) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Asparagus",gramsToUnit(20,38) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("אספרגוס",gramsToUnit(20,38) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Pea",gramsToUnit(81,128) ,"Cup"));
        firstTimeFoods.add(new FoodEntry("אפונה",gramsToUnit(81,128) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("Sweet potato",gramsToUnit(66,378) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("בטטה",gramsToUnit(66,378) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Bamiah",gramsToUnit(31,113) ,"Cup"));
        firstTimeFoods.add(new FoodEntry("במיה",gramsToUnit(31,113) ,"Cup"));
        firstTimeFoods.add(new FoodEntry("Broccoli",gramsToUnit(34,354) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("ברוקולי",gramsToUnit(34,354) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Pumpkin",gramsToUnit(26,120) ,"Cup"));
        firstTimeFoods.add(new FoodEntry("דלעת",gramsToUnit(26,120) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("Eggplant",gramsToUnit(24,330) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("חציל",gramsToUnit(24,330) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Coriander",gramsToUnit(23,40) ,"Cup"));
        firstTimeFoods.add(new FoodEntry("כוסברה",gramsToUnit(23,40) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("Cabbage",gramsToUnit(25,1539) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("כרוב",gramsToUnit(25,1539) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Red cannage",gramsToUnit(31,1539) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("כרוב אדום",gramsToUnit(31,1539) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Cucumber",gramsToUnit(15,96) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("מלפפון",gramsToUnit(15,96) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Beet",gramsToUnit(43,120) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("סלק",gramsToUnit(43,120) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Tomato",gramsToUnit(18,172) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("עגבניה",gramsToUnit(18,172) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Parsley",gramsToUnit(36,50) ,"Cup"));
        firstTimeFoods.add(new FoodEntry("פטרוזיליה",gramsToUnit(36,50) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("Mushroom",gramsToUnit(22,25) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("פטריה",gramsToUnit(22,25) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Chili pepper",gramsToUnit(40,28) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("פלפל חריף",gramsToUnit(40,28) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Pepper",gramsToUnit(31,185) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("גמבה",gramsToUnit(31,185) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Radish",gramsToUnit(16,32) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("צנון",gramsToUnit(16,32) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Kohlrabi",gramsToUnit(27,268) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("קולורבי",gramsToUnit(27,268) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Zucchini",gramsToUnit(20,218) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("קישוא",gramsToUnit(20,218) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Boiled corn",gramsToUnit(108,185) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("תירס חם",gramsToUnit(108,185) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Boiled potato",gramsToUnit(86,169) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("תפוח אדמה מבושל",gramsToUnit(86,169) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Spinach",gramsToUnit(23,40) ,"Cup"));
        firstTimeFoods.add(new FoodEntry("תרד",gramsToUnit(23,40) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("Boiled batata",gramsToUnit(76,58) ,"Dish"));
        firstTimeFoods.add(new FoodEntry("בטטה מבושלת",gramsToUnit(76,58) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("Pastrami",gramsToUnit(93,22) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("פסטרמה",gramsToUnit(93,22) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Chicken sausage",gramsToUnit(222,29) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("נקניקיית עוף",gramsToUnit(222,29) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("שניצל מאמא עוף",gramsToUnit(240,100) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("סטייקבורגר טיבון ויל",gramsToUnit(152,100) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("עוף שלם טרי",gramsToUnit(162,1750) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Beef Liver ",gramsToUnit(116,64) ,"Unit"));
        firstTimeFoods.add(new FoodEntry("כבד בקר",gramsToUnit(116,64) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("Beef brain",gramsToUnit(143,100) ,"100 Gram"));
        firstTimeFoods.add(new FoodEntry("מוח בקר",gramsToUnit(143,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("Beef tongue",gramsToUnit(224,100) ,"100 Gram"));
        firstTimeFoods.add(new FoodEntry("לשון בקר",gramsToUnit(224,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("Beef heart",gramsToUnit(112,100) ,"100 Gram"));
        firstTimeFoods.add(new FoodEntry("לבבות בקר",gramsToUnit(112,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("בשר בקר טחון 5%",gramsToUnit(137,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("בשר בקר טחון 15%",gramsToUnit(215,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("בשר בקר טחון 30%",gramsToUnit(273,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("נפוליאון בטעם שום שמיר",gramsToUnit(257,20) ,"1 כף"));
        firstTimeFoods.add(new FoodEntry("גבינת לאבנה 5% שומן",gramsToUnit(109,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("גוש חלב צהובה",gramsToUnit(249,29) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("צפתית 5%",gramsToUnit(129,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("קוטג` 5%",gramsToUnit(100,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("גבינת חלומי",gramsToUnit(292,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("נפוליאון בטעם זיתים ירוקים",gramsToUnit(243,22) ,"כף"));
        firstTimeFoods.add(new FoodEntry("סקי 5%",gramsToUnit(99,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("פיראוס בולגרית עזים",gramsToUnit(205,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("קוטג` 3%",gramsToUnit(80,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("פטה כבשים",gramsToUnit(240,15) ,"כף"));
        firstTimeFoods.add(new FoodEntry("גבינת קשקבל",gramsToUnit(303,10) ,"כף"));
        firstTimeFoods.add(new FoodEntry("שום שמיר גבינת שמנת 3%",gramsToUnit(94,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("קוטג` תנובה 5% עם זיתים",gramsToUnit(91,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("גד עז",gramsToUnit(240,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("גבינה צהובה לייט 9% שומן",gramsToUnit(202,25) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("קוטג` תנובה 1%",gramsToUnit(62,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("עמק 28%",gramsToUnit(345,25) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("בולגרית מעודנת 5%",gramsToUnit(129,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("פיראוס צפתית 5%",gramsToUnit(160,174) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("צהובה עמק 5% שומן",gramsToUnit(178,25) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("קשקבל טחון 22% שומן",gramsToUnit(310,10) ,"כף"));
        firstTimeFoods.add(new FoodEntry("גבינת פטה",gramsToUnit(264,15) ,"כף"));
        firstTimeFoods.add(new FoodEntry("גבינת צ`דר",gramsToUnit(403,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("גבינת מוצרלה",gramsToUnit(300,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry(" גבינת רוקפור",gramsToUnit(369,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("גבינת ריקוטה",gramsToUnit(174,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("גבינה שוויצרית",gramsToUnit(380,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("גבינת עזים קשה",gramsToUnit(452,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("גגבינת פרמזן מגורדת",gramsToUnit(431,8) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("גבינת פרמזן קשה",gramsToUnit(392,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("XO משקה אנרגיה",gramsToUnit(45,330) ,"פחית"));
        firstTimeFoods.add(new FoodEntry("ספרינג tea אפרסק",gramsToUnit(31,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("דיאט תפוזים",gramsToUnit(14,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("נקטר תפוז גזר",gramsToUnit(20,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("נקטר פומלית",gramsToUnit(17,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("נקטר תפוח בננה",gramsToUnit(46,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("ספרינג מנגו",gramsToUnit(42,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("ספרינג תות בננה",gramsToUnit(43,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("משקה קל ענבים",gramsToUnit(40,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("Sprite - ספרייט",gramsToUnit(39,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("ספרינג נקטר פירות טרופיים",gramsToUnit(43,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("MiRiNDA - תפוזים",gramsToUnit(54,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("ספרינג tea לימון לואיזה",gramsToUnit(33,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("תפוח-ליים מוגז עדין",gramsToUnit(24,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("קוקה-קולה",gramsToUnit(42,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("ג`אמפ תות-בננה",gramsToUnit(45,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("משקה נשר מאלט",gramsToUnit(34,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("משקה קל אשכוליות",gramsToUnit(38,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("פאנטה אורנג`",gramsToUnit(50,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("דיאט תפוזים פריגת",gramsToUnit(14,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("משקה קל ענבים פריגת",gramsToUnit(40,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("משקה לימון נענע",gramsToUnit(40,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("נקטר תות בננה",gramsToUnit(46,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("משקה קל מנגו פריגת",gramsToUnit(41,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("נקטר אפרסקים",gramsToUnit(49,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("אננס צלול",gramsToUnit(29,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("משקה קל ענבים מוסקט",gramsToUnit(38,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("לימונדה - משקה קל לימונים פריגת",gramsToUnit(44,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("סחוט קלמנטינות",gramsToUnit(44,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("סחוט אשכולית אדומה",gramsToUnit(44,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("ענבים מרלו - משקה קל ענבים בניחוח מרלו",gramsToUnit(31,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("נקטר מנגו",gramsToUnit(44,500) ,"בקבוק חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("קציצות בקר מתובלות",gramsToUnit(190,35) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("קציצות עוף מתובלות",gramsToUnit(190,40) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry(" קציצות דג מבושלות ברוטב עגבניות",gramsToUnit(122,44) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("אורז פרסי מבושל",gramsToUnit(123,100) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("אורז בסמטי מובחר",gramsToUnit(123,100) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("פסטה ברוטב עגבניות",gramsToUnit(139,339) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("פסטה שמנת בטטה",572 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("פסטה בשמנת ופטריות",302 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("פתיתים עם בצל, שום ופטריות",310 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("פתיתים עם גזר ובצל",142 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("פתיתים מתובלים",136 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("אורז עם בשר קצוץ",347 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("אורז עם אפונה ושמיר",210 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("אורז עם ערמונים ופטריות",265 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("אורז בסמטי עם ירקות",173 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("חזה עוף וירקות בתנור",250 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("חזה עוף עם כרוב לבן ובצל",392 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("חזה עוף מוקפץ עם אורז וירקות",440 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("קציצות בקר עסיסיות",64 ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("קוסקוס ביתי",371 ,"מנה"));
        firstTimeFoods.add(new FoodEntry("סלט קולסלאו במיונז",gramsToUnit(123,100) ,"100 Gram"));
        firstTimeFoods.add(new FoodEntry("סלט פלפלים חריף",gramsToUnit(80,100) ,"100 Gram"));
        firstTimeFoods.add(new FoodEntry("סלט חומוס",gramsToUnit(286,42) ,"כף"));
        firstTimeFoods.add(new FoodEntry("סלט אבוקדו עם ביצים",gramsToUnit(199,50) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("סלט ביצים",gramsToUnit(186,50) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("סלט כרוב",gramsToUnit(148,50) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("סלט טאקו",gramsToUnit(186,50) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("סלט חציל במיונז",gramsToUnit(209,50) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("סלט מטבוחה",gramsToUnit(48,15) ,"כף"));
        firstTimeFoods.add(new FoodEntry("סלט חומוס מסבחה חריף",gramsToUnit(251,15) ,"כף"));
        firstTimeFoods.add(new FoodEntry("סלט טחינה",gramsToUnit(251,15) ,"כף"));
        firstTimeFoods.add(new FoodEntry("סלט עדשים וגזר עם בצל מטוגן",gramsToUnit(189,15) ,"כף"));
        firstTimeFoods.add(new FoodEntry("סלט אבוקדו",gramsToUnit(141,50) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("סלט ארומה",gramsToUnit(41,650) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("סלט טורקי",gramsToUnit(141,15) ,"כף"));
        firstTimeFoods.add(new FoodEntry("סלט תפוחי אדמה",gramsToUnit(135,15) ,"כף"));
        firstTimeFoods.add(new FoodEntry("עגבניות מגורדות",gramsToUnit(19,300) ,"300 גרם"));
        firstTimeFoods.add(new FoodEntry("זיתים ירוקים ללא גלעין",gramsToUnit(188,2) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("זיתים ירוקים טבעות",gramsToUnit(191,2) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("מלפפונים במלח בינוניים",gramsToUnit(14,28) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("טונה בהירה בשמן",gramsToUnit(194,20) ,"כף"));
        firstTimeFoods.add(new FoodEntry("גרעיני תירס מתוק",gramsToUnit(103,18) ,"כף"));
        firstTimeFoods.add(new FoodEntry("פרוסות אננס במיץ טבעי",gramsToUnit(53,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("רסק עגבניות",gramsToUnit(98,43) ,"כף"));
        firstTimeFoods.add(new FoodEntry("מלפפונים במלח גדולים",gramsToUnit(12,40) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("פטריות שמפניון שלמות",gramsToUnit(22,24) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("זיתים מושחרים ללא גלעין",gramsToUnit(192,3) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("לחם קל",gramsToUnit(177,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם חלבונים - כולל גלוטן",gramsToUnit(245,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם שחור פרוס אחיד",gramsToUnit(268,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם שחור מקמח מלא",gramsToUnit(212,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם שחור קל",gramsToUnit(168,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם סובין אורז",gramsToUnit(243,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם כפרי קל",gramsToUnit(202,23) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם שיפון קל",gramsToUnit(204,23) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם חיטה מלאה מסחרי",gramsToUnit(247,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם שחור פרוס אחיד קל",gramsToUnit(176,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם כהה פרוס",gramsToUnit(250,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("לחם דגנים עם דגנים מלאים",gramsToUnit(265,30) ,"פרוסה"));
        firstTimeFoods.add(new FoodEntry("פיתה",gramsToUnit(249,100) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("פיתה מחיטה מלאה",gramsToUnit(211,100) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("הלחמניה אנג'ל",gramsToUnit(265,75) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("לחמניות המבורגר ללא גלוטן",gramsToUnit(218,90) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("לחמניות שליש באגט ללא גלוטן",gramsToUnit(218,120) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("לחמניות ג`בטה",gramsToUnit(232,160) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("לחמניות המבורגר",gramsToUnit(232,120) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("לחמניות ארוכות",gramsToUnit(232,120) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("פתי בר גדול",gramsToUnit(455,8) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("פתי-בר - ביסקויט בטעם שוקו",gramsToUnit(444,6) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("פתי-בר - ביסקויט בטעם חמאה",gramsToUnit(460,6) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("פתיבר אסם",gramsToUnit(457,8) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("ופלים בטעם שוקולד",gramsToUnit(539,10) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("אוראו - עוגיות סנדוויץ שוקולד",gramsToUnit(480,11) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("אוראו - עוגיות סנדוויץ וניל",gramsToUnit(460,11) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("עוגיות קרמוגית קרם שוקולד",gramsToUnit(519,13) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("מיני קוקיס בטעם שוקולד",gramsToUnit(545,4) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("עוגיות סנדוויץ במילוי קרם חלב",gramsToUnit(501,7) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("עוגיות שוקוצ`יפס עם שוקוצ`יפס לבן",gramsToUnit(488,13) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("עוגיות גן חיות בטעם וניל",gramsToUnit(444,2) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("עוגיות שוקולד צ`יפס",gramsToUnit(510,19) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("עוגיות בראוניס",gramsToUnit(466,34) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("עוגיות ערגליות תות",gramsToUnit(423,11) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - שוקולד פירורים",gramsToUnit(395,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית בטעם שוקולד",gramsToUnit(415,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - דמקה דבש",gramsToUnit(401,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - בראוניס פאדג`",gramsToUnit(466,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - דמקה שזיפים אגוזים",gramsToUnit(388,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית דבש תפוחים פירורים",gramsToUnit(394,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - בראוניס בטעם שוקולד וניל צרפתי",gramsToUnit(418,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - שיש בטעם שוקולד תפוז",gramsToUnit(396,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - שיש אגוזים",gramsToUnit(422,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - רולדת שמרים שוקו שוקו",gramsToUnit(444,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - תפוחים פירורים",gramsToUnit(388,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית בטעם תפוז",gramsToUnit(423,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - אנגלית",gramsToUnit(410,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("עוגת הבית - שוקולד צ`יפס",gramsToUnit(414,34) ,"חתיכה"));
        firstTimeFoods.add(new FoodEntry("ביסלי בטעם בצל",gramsToUnit(494,67) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("במבה במילוי קרם נוגט",gramsToUnit(529,60) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("תפוצ'יפס מלח ופלפל גרוס",gramsToUnit(509,45) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("ביסלי בטעם פיצה",gramsToUnit(496,70) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("דוריטוס בטעם טבעי",gramsToUnit(508,70) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("במבה",gramsToUnit(534,25) ,"שקית 25 גרם"));
        firstTimeFoods.add(new FoodEntry("במבה",gramsToUnit(534,80) ,"שקית 80 גרם"));
        firstTimeFoods.add(new FoodEntry("במבה",gramsToUnit(534,100) ,"שקית 100 גרם"));
        firstTimeFoods.add(new FoodEntry("במבה",gramsToUnit(534,200) ,"שקית 200 גרם"));
        firstTimeFoods.add(new FoodEntry("אפרופו",gramsToUnit(538,100) ,"שקית 100 גרם"));
        firstTimeFoods.add(new FoodEntry("ביסלי בטעם פלאפל",gramsToUnit(479,70) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("ביסלי בטעם גריל",gramsToUnit(498,70) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("דוריטוס בטעם גריל",gramsToUnit(527,70) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("תפוצ`יפס בטעם טבעי",gramsToUnit(542,50) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("דוריטוס בטעם חמוץ חריף",gramsToUnit(520,70) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("תפוצ`יפס קראנצ` בטעם מקסיקני",gramsToUnit(542,50) ,"שקית קטנה"));
        firstTimeFoods.add(new FoodEntry("סוכר לבן",gramsToUnit(396,5) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("סוכר חום",gramsToUnit(380,7) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("קפה נמס",gramsToUnit(356,2) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("שוקולית",gramsToUnit(400,4) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("שמן קנולה",gramsToUnit(828,10) ,"כף"));
        firstTimeFoods.add(new FoodEntry("נתחי טונה בהירה במים",gramsToUnit(117,120) ,"קופסת שימורים"));
        firstTimeFoods.add(new FoodEntry("פלפל שחור",gramsToUnit(255,5) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("מלח",gramsToUnit(0,0) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("פפריקה",gramsToUnit(289,3) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("רוטב סויה",gramsToUnit(187,5) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("ויניגרט לסלט",gramsToUnit(295,5) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("כמון",gramsToUnit(289,3) ,"כפית שטוחה"));
        firstTimeFoods.add(new FoodEntry("בוטנים מטוגנים",gramsToUnit(600,29) ,"חופן"));
        firstTimeFoods.add(new FoodEntry("שומשום",gramsToUnit(533,12) ,"כף"));
        firstTimeFoods.add(new FoodEntry("סולת חיטה",gramsToUnit(319,200) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("אבקת אפיה",gramsToUnit(133,5) ,"כפית"));
        firstTimeFoods.add(new FoodEntry("אבקת סוכר",gramsToUnit(398,6) ,"כפית"));
        firstTimeFoods.add(new FoodEntry("קמח תופח",gramsToUnit(347,155) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("חמאת בוטנים",gramsToUnit(529,8) ,"כפית"));
        firstTimeFoods.add(new FoodEntry("ממרח אגוזים השחר",gramsToUnit(533,18) ,"כפית"));
        firstTimeFoods.add(new FoodEntry("ריבה מעורבת",gramsToUnit(277,14) ,"כפית"));
        firstTimeFoods.add(new FoodEntry("ריבת חלב",gramsToUnit(324,18) ,"כפית"));
        firstTimeFoods.add(new FoodEntry("חמאה",gramsToUnit(717,5) ,"כפית"));
        firstTimeFoods.add(new FoodEntry("ביצה שלמה - טריה",gramsToUnit(143,58) ,"Medium"));
        firstTimeFoods.add(new FoodEntry("ביצה שלמה - טריה",gramsToUnit(143,68) ,"Large"));
        firstTimeFoods.add(new FoodEntry("שוקולד חלב",gramsToUnit(547,4) ,"קוביה"));
        firstTimeFoods.add(new FoodEntry("שוקולד מריר 60%",gramsToUnit(533,4) ,"קוביה"));
        firstTimeFoods.add(new FoodEntry("שוקולד מריר 40%",gramsToUnit(547,4) ,"קוביה"));
        firstTimeFoods.add(new FoodEntry("שוקולד מריר ללא תוספת סוכר",gramsToUnit(449,4) ,"קוביה"));
        firstTimeFoods.add(new FoodEntry("מרק בטעם עוף",gramsToUnit(263,7) ,"כפית גדושה"));
        firstTimeFoods.add(new FoodEntry("תמר - מג`הול",gramsToUnit(277,17) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("יוגורט + אפרסק 3%",gramsToUnit(111,150) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("יוגורט כבשים 5% BIO",gramsToUnit(75,850) ,"כופסאת פלסטיק"));
        firstTimeFoods.add(new FoodEntry("דנונה אפרסק ופסיפלורה 3%",gramsToUnit(94,150) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("דנונה - יוגורט + תות 0%",gramsToUnit(36,150) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("מעדן תנובה בטעם ריבת חלב",gramsToUnit(101,150) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("מעדן הגולן שוקולד",gramsToUnit(122,150) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("מעדן תנובה 0% בטעם שוקולד",gramsToUnit(55,150) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("קרקרים מלוחים",gramsToUnit(456,3) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("פריכיות אורז תפוח וקינמון",gramsToUnit(430,50) ,"שקית"));
        firstTimeFoods.add(new FoodEntry("פריכיות מרובות דגנים",gramsToUnit(354,5) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("פריכיות חיטה",gramsToUnit(372,5) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("שעועית ירוקה חתוכה מוקפאת",gramsToUnit(33,100) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("אפונת גינה מוקפאת",gramsToUnit(75,128) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("במיה מוקפאת",gramsToUnit(19,113) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("פול ירוק מוקפא",gramsToUnit(55,132) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("תחתיות ארטישוק - מוקפא",gramsToUnit(17,400) ,"שקית"));
        firstTimeFoods.add(new FoodEntry("גזר גמדי קפוא",gramsToUnit(36,175) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("חזה עוף טרי",gramsToUnit(114,125) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("בשר עוף טחון טרי",gramsToUnit(143,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("בשר הודו טחון טרי",gramsToUnit(148,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("בשר טלה טחון טרי",gramsToUnit(282,100) ,"100 גרם"));
        firstTimeFoods.add(new FoodEntry("פילה סלמון פרמיום קפוא",gramsToUnit(212,96) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("אמנון טרי",gramsToUnit(96,140) ,"פילה"));
        firstTimeFoods.add(new FoodEntry("בורי מפוספס",gramsToUnit(117,200) ,"פילה"));
        firstTimeFoods.add(new FoodEntry("כריות מוקה",gramsToUnit(455,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("קורנפלקס דבש",gramsToUnit(366,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("CRUNCH נסטלה",gramsToUnit(389,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("Honey Nut Cheerios דבש ושקדים ",gramsToUnit(376,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("פייבר FIBRE 1",gramsToUnit(200,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("Fitness שוקולד לבן",gramsToUnit(384,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("כריות נוגט",gramsToUnit(469,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("קורנפלקס קקאו",gramsToUnit(378,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("דליפקאן",gramsToUnit(400,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("קורנפלקס של אלופים",gramsToUnit(381,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("קרנצ'ים",gramsToUnit(324,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("פירורי לחם מוזהבים",gramsToUnit(384,4) ,"כפית"));
        firstTimeFoods.add(new FoodEntry("קטשופ",gramsToUnit(115,19) ,"כף"));
        firstTimeFoods.add(new FoodEntry("מיונז אמיתי",gramsToUnit(600,15) ,"כף"));
        firstTimeFoods.add(new FoodEntry("חומץ - סיידר",gramsToUnit(21,5) ,"כפית"));
        firstTimeFoods.add(new FoodEntry("קוקומן פצפוצי אורז בטעם שוקולד",gramsToUnit(393,30) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("חמניות מקולפות",gramsToUnit(560,17) ,"חופן"));
        firstTimeFoods.add(new FoodEntry("בוטנים קלויים עם מלח",gramsToUnit(585,29) ,"חופן"));
        firstTimeFoods.add(new FoodEntry("פודינג בטעם וניל",gramsToUnit(380,80) ,"קרטון"));
        firstTimeFoods.add(new FoodEntry("פודינג בטעם שוקולד לבן",gramsToUnit(368,80) ,"קרטון"));
        firstTimeFoods.add(new FoodEntry("ENERGY שחור לבן חטיף אנרגיה",gramsToUnit(395,25) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("ENERGY עם אגוזים ושוקולד",gramsToUnit(397,25) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("מיקספלקס פלוס אגוזים ושוקולד חלב",gramsToUnit(461,33) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("CORNY chocolate",gramsToUnit(446,25) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("to go - עם שוקולד לבן מעולה",gramsToUnit(501,23) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("NATURE VALLEY CRUNCHY",gramsToUnit(461,21) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("ENERGY חטיף קורנפלקס",gramsToUnit(399,25) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("פיצה איטלקית מרובעת",gramsToUnit(239,400) ,"קרטון"));
        firstTimeFoods.add(new FoodEntry("פיצה משפחתית מוקפאת",gramsToUnit(231,410) ,"קרטון"));
        firstTimeFoods.add(new FoodEntry("פיצה משפחתית בתוספת זיתים מוקפאת",gramsToUnit(225,460) ,"קרטון"));
        firstTimeFoods.add(new FoodEntry("בורקס גבינה קפוא",gramsToUnit(293,50) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("בורקס תפוחי אדמה קפוא",gramsToUnit(311,33) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("בורקס פילו תפוחי אדמה קפוא",gramsToUnit(247,50) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("בורקס פילו פיצה",gramsToUnit(308,50) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("מיני ג`חנון אפוי למיקרוגל",gramsToUnit(396,36) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("מלאווח תימני מקורי קפוא",gramsToUnit(310,116) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("צלחת חמין",gramsToUnit(550,100) ,"מנה"));
        firstTimeFoods.add(new FoodEntry("ג'חנון",gramsToUnit(650,100) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("קובה סולט מבושל",gramsToUnit(100,100) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("ספינג'",gramsToUnit(300,100) ,"יחידה"));
        firstTimeFoods.add(new FoodEntry("בוריק",gramsToUnit(300,100) ,"יחידה קטנה"));
        firstTimeFoods.add(new FoodEntry("פופקורן חמאה",gramsToUnit(505,80) ,"אריזת ניילון"));
        firstTimeFoods.add(new FoodEntry("בירה שחורה",gramsToUnit(41,240) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("בירה",gramsToUnit(43,500) ,"חצי ליטר"));
        firstTimeFoods.add(new FoodEntry("בירה",gramsToUnit(43,330) ,"שליש"));
        firstTimeFoods.add(new FoodEntry("יין לבן",gramsToUnit(82,60) ,"כוס יין"));
        firstTimeFoods.add(new FoodEntry("תירוש מיץ ענבים",gramsToUnit(87,30) ,"כוס יין"));
        firstTimeFoods.add(new FoodEntry("חלב 3%",gramsToUnit(60,240) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("חלב 0%",gramsToUnit(35,240) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("חלב 1%",gramsToUnit(41,240) ,"כוס"));
        firstTimeFoods.add(new FoodEntry("נס קפה 1 סוכר",53 ,"כוס"));
        firstTimeFoods.add(new FoodEntry("קפה הפוך קטן",112 ,"כוס"));
        firstTimeFoods.add(new FoodEntry("קפה הפוך גדול",170 ,"כוס"));
        firstTimeFoods.add(new FoodEntry("קפה שחור",20 ,"כוס"));
        firstTimeFoods.add(new FoodEntry("כוס תה עם כפית סוכר",20 ,"כוס"));
        firstTimeFoods.add(new FoodEntry("תיון תה",0 ,"תיון"));


        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.foodDao().insertAll(firstTimeFoods);
            }
        });
    }



}
