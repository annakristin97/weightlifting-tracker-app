package com.example.weightliftingapp.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weightliftingapp.Entities.FilteredLifts;
import com.example.weightliftingapp.Entities.Lift;
import com.example.weightliftingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TableActivity extends AppCompatActivity {

    public static final String TAG = TableActivity.class.getSimpleName();

    private LiftAdapter liftAdapter;
    private FilteredLifts filteredLifts;

    FloatingActionButton AddButton;
    RadioButton graphButton, tableButton;
    ListView prList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        //kannski breyta her??
        BottomNavigationView BottomNavigationView = findViewById(R.id.bottomNavigationView);
        //default selected
        BottomNavigationView.setSelectedItemId(R.id.navigation_graph);

        getLifts();

        //item selection for navigation
        BottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    ///grafið gefur okkur homescreeninn
                    case R.id.navigation_graph:
                        //startActivity(new Intent(getApplicationContext(), BarChartActivity.class));
                        //overridePendingTransition(0, 0);
                        return false;
                    case R.id.navigation_settings:
                        startActivity(new Intent(getApplicationContext()
                                , HomeFragment.class));
                        overridePendingTransition(0, 0);
                        return true;
                    //case R.id.fab:
                    //  return true;
                }
                return false;
            }
        });

        ButterKnife.bind(this);

        graphButton = findViewById(R.id.graph);
        tableButton = findViewById(R.id.table);
        AddButton = findViewById(R.id.fab);
        prList = findViewById(R.id.prList);

        //þegar við ýtum á plúsinn
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddFragment();
            }
        });

        graphButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                graphButton.setTextColor(Color.BLACK);
                tableButton.setTextColor(Color.WHITE);
                openMainActivity();
            }
        });

        tableButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tableButton.setTextColor(Color.BLACK);
                graphButton.setTextColor(Color.WHITE);
            }
        });
    }

    public void openAddFragment(){
        Intent intent = new Intent(this, AddFragment.class);
        startActivity(intent);
    }

    public void openMainActivity(){
        Intent intent = new Intent(this,
                MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * Hjálparfall til þess að sækja öll items
     */
    public void getLifts() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/lifts/pr")
                .build();

        callBackend(request);
    }

    /**
     * Call backend for filtered lifts
     */
    private void callBackend(Request request){
        OkHttpClient client = new OkHttpClient();

        if(isNetworkAvailable()) {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            filteredLifts = parseLiftListDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Update-ar view
     */
    private void updateDisplay() {
        liftAdapter = new LiftAdapter(this, filteredLifts.getLifts());

        prList.setAdapter(liftAdapter);
    }

    /**
     * Sækir upplýsingar um pr lyftur og setur í listann
     */
    private FilteredLifts parseLiftListDetails(String jsonData) throws JSONException{

        FilteredLifts filteredLifts = new FilteredLifts();

        ArrayList<Lift> lifts = new ArrayList<Lift>();
        JSONArray array = new JSONArray(jsonData);
        for(int i=0;i<array.length();i++) {
            JSONObject elem=(JSONObject)array.get(i);
            Lift lift = new Lift(
                    elem.getLong("itemID"),
                    elem.getString("liftName"),
                    elem.getLong("weight"),
                    elem.getLong("reps"),
                    elem.getLong("sets"),
                    elem.getLong("logTime"));
            lifts.add(lift);
        }

        filteredLifts.setLifts(lifts);

        return filteredLifts;
    }


    /**
     * Checks if backend is accessible
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo!= null && networkInfo.isConnected()) isAvailable = true;
        return isAvailable;
    }

    /**
     * Error popup
     */
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}
