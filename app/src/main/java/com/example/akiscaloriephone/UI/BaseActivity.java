package com.example.akiscaloriephone.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.akiscaloriephone.AppContract;
import com.example.akiscaloriephone.R;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity {



    protected DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        dl = (DrawerLayout)findViewById(R.id.BaseActivity);
        t = new ActionBarDrawerToggle(this, dl,R.string.openNav, R.string.closeNav);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.myDiaryNav:
                        Intent myDiary=new Intent(BaseActivity.this,MainActivity.class);
                        startActivity(myDiary);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.settingsNav:
                        Intent settings=new Intent(BaseActivity.this,SettingsActivity.class);
                        startActivity(settings);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.foodListNav:
                        Intent foodlist=new Intent(BaseActivity.this,FoodlistActivity.class);
                        foodlist.putExtra(AppContract.MODE,AppContract.MODE_ADD_TO_DIARY);
                        startActivity(foodlist);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.sportListNav:
                        Intent sportlist=new Intent(BaseActivity.this,SportsListActivity.class);
                        sportlist.putExtra(AppContract.MODE,AppContract.MODE_ADD_SPORT);
                        startActivity(sportlist);
                        overridePendingTransition(0, 0);
                        break;
                    default:
                        return true;
                }
                return true;

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}
