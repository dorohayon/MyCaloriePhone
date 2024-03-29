package com.example.akiscaloriephone.UI;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SearchView;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.Database.SportEntry;
import com.example.akiscaloriephone.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class SportsListActivity extends BaseActivity {
     private RecyclerView sportListRecycleView;
     private SportListAdapter sportListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_sports_list, null, false);
        dl.addView(contentView, 0);
        sportListRecycleView=findViewById(R.id.sportsListRecycleView);
        sportListAdapter = new SportListAdapter(this);
        int dateIndicator=getIntent().getIntExtra(AppContract.DATE_INDICATOR,0);
        sportListAdapter.setDateIndicator(dateIndicator);
        sportListRecycleView.setAdapter(sportListAdapter);
        sportListRecycleView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration sportsdividerItemDecoration = new DividerItemDecoration(sportListRecycleView.getContext(), VERTICAL);
        sportListRecycleView.addItemDecoration(sportsdividerItemDecoration);
        //setup searchview
        SearchView searchView=(SearchView)findViewById(R.id.search_sportlist);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                sportListAdapter.getFilter().filter(s);
                return false;
            }
        });

        addSports();
    }

    private void addSports() {
        ArrayList <SportEntry> sports = new ArrayList<>();
        LinkedHashMap <String,Double> levelAndMET=new LinkedHashMap<>();
        String name;
        name="Aerobic dancing";
        levelAndMET.put("Low",3.9);
        levelAndMET.put("Medium",6.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="ריקוד אירובי";
        levelAndMET.put("נמוכה",3.9);
        levelAndMET.put("בינונית",6.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Ballet";
        levelAndMET.put("Light",5.0);
        levelAndMET.put("Moderate",6.0);
        levelAndMET.put("Heavy",8.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="בלט";
        levelAndMET.put("נמוכה",5.0);
        levelAndMET.put("בינונית",6.0);
        levelAndMET.put("גבוהה",8.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Ball hockey";
        levelAndMET.put("Light",3.0);
        levelAndMET.put("Moderate",4.0);
        levelAndMET.put("Heavy",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="הוקי קרח";
        levelAndMET.put("נמוכה",3.0);
        levelAndMET.put("בינונית",4.0);
        levelAndMET.put("גבוהה",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Ballroom dancing";
        levelAndMET.put("Light",3.0);
        levelAndMET.put("Moderate",4.0);
        levelAndMET.put("Heavy",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="ריקודים סלוניים";
        levelAndMET.put("נמוכה",3.0);
        levelAndMET.put("בינונית",4.0);
        levelAndMET.put("גבוהה",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Baseball";
        levelAndMET.put("Light",3.0);
        levelAndMET.put("Moderate",4.0);
        levelAndMET.put("Heavy",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="בייסבול";
        levelAndMET.put("נמוכה",3.0);
        levelAndMET.put("בינונית",4.0);
        levelAndMET.put("גבוהה",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Basketball";
        levelAndMET.put("Light",6.0);
        levelAndMET.put("Moderate",8.0);
        levelAndMET.put("Heavy",11.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="כדורסל";
        levelAndMET.put("נמוכה",6.0);
        levelAndMET.put("בינונית",8.0);
        levelAndMET.put("גבוהה",11.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Bicycling";
        levelAndMET.put("10 Km/h",4.8);
        levelAndMET.put("15 Km/h",5.9);
        levelAndMET.put("20 Km/h",7.1);
        levelAndMET.put("25 Km/h",8.4);
        levelAndMET.put("30 Km/h",9.8);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="רכיבה על אופניים";
        levelAndMET.put("10 קמש",4.8);
        levelAndMET.put("15 קמש",5.9);
        levelAndMET.put("20 קמש",7.1);
        levelAndMET.put("25 קמש",8.4);
        levelAndMET.put("30 קמש",9.8);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="בודי בילדינג";
        levelAndMET.put("נמוכה",3.0);
        levelAndMET.put("בינונית",5.0);
        levelAndMET.put("גבוהה",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Body building";
        levelAndMET.put("Light",3.0);
        levelAndMET.put("Moderate",5.0);
        levelAndMET.put("Heavy",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Bowling";
        levelAndMET.put("Light",2.0);
        levelAndMET.put("Moderate",2.5);
        levelAndMET.put("Heavy",3.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="באולינג";
        levelAndMET.put("נמוכה",2.0);
        levelAndMET.put("בינונית",2.5);
        levelAndMET.put("גבוהה",3.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();


        name="Boxing";
        levelAndMET.put("Light",6.0);
        levelAndMET.put("Moderate",9.0);
        levelAndMET.put("Heavy",12.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="אגרוף";
        levelAndMET.put("נמוכה",6.0);
        levelAndMET.put("בינונית",9.0);
        levelAndMET.put("גבוהה",12.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Cricket";
        levelAndMET.put("Light",3.0);
        levelAndMET.put("Moderate",4.0);
        levelAndMET.put("Heavy",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="קריקט";
        levelAndMET.put("נמוכה",3.0);
        levelAndMET.put("בינונית",4.0);
        levelAndMET.put("גבוהה",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="דיסקו וריקודים נפוצים";
        levelAndMET.put("נמוכה",3.0);
        levelAndMET.put("בינונית",5.0);
        levelAndMET.put("גבוהה",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Disco and popular dancing";
        levelAndMET.put("Light",3.0);
        levelAndMET.put("Moderate",5.0);
        levelAndMET.put("Heavy",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Floor hockey";
        levelAndMET.put("Light",6.0);
        levelAndMET.put("Moderate",8.0);
        levelAndMET.put("Heavy",10.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="הוקי רצפה";
        levelAndMET.put("נמוכה",6.0);
        levelAndMET.put("בינונית",8.0);
        levelAndMET.put("גבוהה",10.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();


        name="פוטבול אמריקאי";
        levelAndMET.put("נמוכה",5.0);
        levelAndMET.put("בינונית",6.0);
        levelAndMET.put("גבוהה",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Football (American) ";
        levelAndMET.put("Light",5.0);
        levelAndMET.put("Moderate",6.0);
        levelAndMET.put("Heavy",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="סקי";
        levelAndMET.put("נמוכה",4.0);
        levelAndMET.put("בינונית",6.0);
        levelAndMET.put("גבוהה",9.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Freestyle skiing";
        levelAndMET.put("Light",4.0);
        levelAndMET.put("Moderate",6.0);
        levelAndMET.put("Heavy",9.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="פריזבי";
        levelAndMET.put("נמוכה",3.0);
        levelAndMET.put("בינונית",4.0);
        levelAndMET.put("גבוהה",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Frisbee";
        levelAndMET.put("Light",3.0);
        levelAndMET.put("Moderate",4.0);
        levelAndMET.put("Heavy",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();


        name="Golf";
        levelAndMET.put("Carrying clubs",5.1);
        levelAndMET.put("Pulling cart",4.0);
        levelAndMET.put("Riding cart",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="ג'ודו";
        levelAndMET.put("נמוכה",6.0);
        levelAndMET.put("בינונית",8.0);
        levelAndMET.put("גבוהה",12.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Judo";
        levelAndMET.put("Light",6.0);
        levelAndMET.put("Moderate",8.0);
        levelAndMET.put("Heavy",12.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="קרטה";
        levelAndMET.put("נמוכה",5.0);
        levelAndMET.put("בינונית",8.0);
        levelAndMET.put("גבוהה",12.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Karate";
        levelAndMET.put("Light",5.0);
        levelAndMET.put("Moderate",8.0);
        levelAndMET.put("Heavy",12.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="קייאקים";
        levelAndMET.put("12.5 קמש",7.8);
        levelAndMET.put("15 קמש",11.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Kayaking";
        levelAndMET.put("12.5 Km/h",7.8);
        levelAndMET.put("15 Km/h",11.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="אופנועי שטח";
        levelAndMET.put("נמוכה",2.5);
        levelAndMET.put("בינונית",4.0);
        levelAndMET.put("גבוהה",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Motorcycling";
        levelAndMET.put("Light",2.5);
        levelAndMET.put("Moderate",4.0);
        levelAndMET.put("Heavy",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="החלקה על גלגיליות";
        levelAndMET.put("12.9 קמש",5.7);
        levelAndMET.put("13.9 קמש",7.6);
        levelAndMET.put("16.1 קמש",9.5);
        levelAndMET.put("17.7 קמש",10.5);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Rollerskating";
        levelAndMET.put("12.9 Km/h",5.7);
        levelAndMET.put("13.9 Km/h",7.6);
        levelAndMET.put("16.1 Km/h",9.5);
        levelAndMET.put("17.7 Km/h",10.5);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Rugby";
        levelAndMET.put("Light",6.0);
        levelAndMET.put("Moderate",8.0);
        levelAndMET.put("Heavy",11.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="רוגבי";
        levelAndMET.put("נמוכה",6.0);
        levelAndMET.put("בינונית",8.0);
        levelAndMET.put("גבוהה",11.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="ריצה";
        levelAndMET.put("13 קמש",12.9);
        levelAndMET.put("15 קמש",14.6);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Running";
        levelAndMET.put("13 Km/h",12.9);
        levelAndMET.put("15 Km/h",14.6);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="רכיבה על סקייטבורד";
        levelAndMET.put("נמוכה",5.0);
        levelAndMET.put("בינונית",6.5);
        levelAndMET.put("גבוהה",8.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Skateboarding";
        levelAndMET.put("Light",5.0);
        levelAndMET.put("Moderate",6.5);
        levelAndMET.put("Heavy",8.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="כדורגל";
        levelAndMET.put("נמוכה",5.0);
        levelAndMET.put("בינונית",7.0);
        levelAndMET.put("גבוהה",11.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Soccer";
        levelAndMET.put("Light",5.0);
        levelAndMET.put("Moderate",7.0);
        levelAndMET.put("Heavy",11.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Swimming (pool)";
        levelAndMET.put("2 Km/h",4.3);
        levelAndMET.put("2.5 Km/h",6.8);
        levelAndMET.put("3.0 Km/h",8.9);
        levelAndMET.put("3.5 Km/h",11.5);
        levelAndMET.put("4.0 Km/h",13.6);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="שחיה";
        levelAndMET.put("2 קמש",4.3);
        levelAndMET.put("2.5 קמש",6.8);
        levelAndMET.put("3.0 קמש",8.9);
        levelAndMET.put("3.5 קמש",11.5);
        levelAndMET.put("4.0 קמש",13.6);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="טניס יחידים";
        levelAndMET.put("נמוכה",6.0);
        levelAndMET.put("בינונית",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Tennis (singles)";
        levelAndMET.put("Light",6.0);
        levelAndMET.put("Moderate",7.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="טניס זוגות";
        levelAndMET.put("נמוכה",4.0);
        levelAndMET.put("בינונית",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Tennis (doubles)";
        levelAndMET.put("Light",4.0);
        levelAndMET.put("Moderate",5.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="הליכה מהירה";
        levelAndMET.put("3 קמש",1.8);
        levelAndMET.put("5 קמש",3.2);
        levelAndMET.put("7 קמש",5.3);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Walking for exercise (km/h) ";
        levelAndMET.put("3 Km/h",1.8);
        levelAndMET.put("5 Km/h",3.2);
        levelAndMET.put("7 Km/h",5.3);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="עליית מדרגות";
        levelAndMET.put("נמוכה",4.0);
        levelAndMET.put("בינונית",6.0);
        levelAndMET.put("גבוהה",8.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

        name="Walking upstairs";
        levelAndMET.put("Light",4.0);
        levelAndMET.put("Moderate",6.0);
        levelAndMET.put("Heavy",8.0);
        sports.add(new SportEntry(name,new LinkedHashMap<String, Double>(levelAndMET)));
        levelAndMET.clear();

      sportListAdapter.setSports(sports);


    }
}
