package com.example.weightliftingapp.UI;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weightliftingapp.Entities.FilteredLifts;
import com.example.weightliftingapp.Entities.Lift;
import com.example.weightliftingapp.R;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import com.github.mikephil.charting.charts.BarChart;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ListView liftList;

    Button btnBarChart, btnPieChart, searchButton;

    public static final String TAG = MainActivity.class.getSimpleName();

    private FilteredLifts mFilteredLifts;
    private LiftAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        liftList = findViewById(R.id.liftList);

        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Leita");
                getLifts();
            }
        });

        BarChart barChart = (BarChart) findViewById(R.id.barchart);

        btnBarChart = findViewById(R.id.btnBarChart);
        btnPieChart = findViewById(R.id.btnPieChart);

        btnBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(MainActivity.this, BarChartActivity.class);
                startActivity(I);
            }
        });
        btnPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(MainActivity.this, PieChartActivity.class);
                startActivity(I);
            }
        });
    }

    /**
     * Hjálparfall til þess að sækja allar liftur items
     */
    public void getLifts() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/lifts")
                .build();

        callBackend(request);
    }

    /**
     * Kallar á bakenda til þess að sækja allar liftur
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
                            mFilteredLifts = parseLiftListDetails(jsonData);
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
     * Sækir upplýsingar um filtered lifts og setur í listann
     */
    private FilteredLifts parseLiftListDetails(String jsonData) throws JSONException{

        FilteredLifts filteredLifts = new FilteredLifts();

        ArrayList<Lift> lifts=new ArrayList<Lift>();
        JSONArray array=new JSONArray(jsonData);
        for(int i=0;i<array.length();i++){
            JSONObject elem=(JSONObject)array.get(i);
            //TODO: laga Date handling
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2021);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Lift lift = new Lift(
                    elem.getLong("itemID"),
                    elem.getString("liftName"),
                    elem.getLong("weight"),
                    elem.getLong("sets"),
                    elem.getLong("reps"),
                    elem.getLong("logTime"));
            lifts.add(lift);
        }

        filteredLifts.setLifts(lifts);

        return filteredLifts;
    }

    /**
     * Update-ar view
     */
    private void updateDisplay() {
        mAdapter = new LiftAdapter(this, mFilteredLifts.getLifts());

        liftList.setAdapter(mAdapter);
    }

    /**
     * Athugar hvort network sé aðgengilegt
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo!= null && networkInfo.isConnected()) isAvailable = true;
        return isAvailable;
    }

    /**
     * Upplýsir notanda um Error
     */
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

}