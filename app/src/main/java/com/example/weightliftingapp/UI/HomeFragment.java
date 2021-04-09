package com.example.weightliftingapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weightliftingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/// vantar að bæta við fyrir plús takkann

public class HomeFragment extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        //kannski breyta her??
        BottomNavigationView BottomNavigationView = findViewById(R.id.bottomNavigationView);
        //default selected
        BottomNavigationView.setSelectedItemId(R.id.navigation_settings);

        //item selection for navigation
        BottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    ///grafið gefur okkur homescreeninn
                    case R.id.navigation_graph:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_settings:
                        return false;
                    //case R.id.fab:
                    //  return true;
                }
                return false;
            }
        });

    }





}
