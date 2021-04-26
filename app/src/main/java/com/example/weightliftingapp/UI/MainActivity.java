package com.example.weightliftingapp.UI;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.example.weightliftingapp.Entities.FilteredLifts;
import com.example.weightliftingapp.Entities.Lift;
import com.example.weightliftingapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ListView liftList;

    Button btnBarChart, btnPieChart, searchButton;
    public int[] colors = {Color.RED,Color.BLUE, Color.GREEN, Color.YELLOW, Color.BLACK, Color.GRAY, Color.WHITE, Color.CYAN, Color.MAGENTA, Color.DKGRAY};


    public static final String TAG = MainActivity.class.getSimpleName();

    private FilteredLifts mFilteredLifts;
    private LiftAdapter mAdapter;

    ArrayList<FilteredLifts> AllFilteredLifts = new ArrayList<>();

    Button searchButton;
    FloatingActionButton AddButton;
    Spinner timeIntervalFilter, repsFilter, setsFilter;
    Spinner typeFilter;
    MultiSpinnerSearch multiSelectSpinnerWithSearch;
    List<String> SelectedLifts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<String> list = Arrays.asList(getResources().getStringArray(R.array.typeItems));

        final List<KeyPairBoolData> listArray1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list.get(i));
            h.setSelected(i < 1);
            listArray1.add(h);
        }

        multiSelectSpinnerWithSearch = findViewById(R.id.multipleItemSelectionSpinner);

        //multiSelectSpinnerWithSearch.setBackgroundColor(Color.GRAY);

        // Pass true If you want searchView above the list. Otherwise false. default = true.
        multiSelectSpinnerWithSearch.setSearchEnabled(true);

        // A text that will display in search hint.
        multiSelectSpinnerWithSearch.setSearchHint("Select lift");

        // Set text that will display when search result not found...
        multiSelectSpinnerWithSearch.setEmptyTitle("Not Data Found!");

        // If you will set the limit, this button will not display automatically.
        multiSelectSpinnerWithSearch.setShowSelectAllButton(true);

        multiSelectSpinnerWithSearch.get

        //A text that will display in clear text button
        multiSelectSpinnerWithSearch.setClearText("Close & Clear");



        //kannski breyta her??
        BottomNavigationView BottomNavigationView = findViewById(R.id.bottomNavigationView);
        //default selected
        BottomNavigationView.setSelectedItemId(R.id.navigation_graph);

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

        // Removed second parameter, position. Its not required now..
        // If you want to pass preselected items, you can do it while making listArray,
        // pass true in setSelected of any item that you want to preselect

        multiSelectSpinnerWithSearch.setItems(listArray1, new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                //BREYTA
                SelectedLifts.clear();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                        //store items that where selected
                        SelectedLifts.add(items.get(i).getName());
                    }
                }
            }
        });

        // click search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update graph content
                //AllFilteredLifts.clear();
                getLifts(SelectedLifts,
                        timeIntervalFilter.getSelectedItem().toString(),
                        repsFilter.getSelectedItem().toString(),
                        setsFilter.getSelectedItem().toString());
            }

        });

    }



    public void openAddFragment(){
        Intent intent = new Intent(this, AddFragment.class);
        startActivity(intent);
    }

    /**
     * Hjálparfall til þess að sækja allar liftur items
     */
    public void getLifts() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/lifts")
                .build();

        callBackend(request);
    public void getLifts(List<String> liftNames, String timeInterval, String reps, String sets) {
        List<Request> requests = new ArrayList<Request>();

        for(int i = 0; i < liftNames.size(); i++){
            Log.d(TAG, "liftName: " + i + "  is " + liftNames.get(i));
            RequestBody formBody = new FormBody.Builder()
                    .add("timeInterval", timeInterval)
                    .add("liftName", liftNames.get(i))
                    .add("sets", sets)
                    .add("reps", reps)
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8090/lifts/search")
                    .post(formBody)
                    .build();
            requests.add(request);
        }
        callBackend(requests);
    }

    /**
     * Kallar á bakenda til þess að sækja allar liftur
     */
    private void callBackend(List<Request> requests){

        OkHttpClient client = new OkHttpClient();

        if(isNetworkAvailable()) {
            Log.d(TAG, "callBackend: requests size: " + requests.size());
            for(int i = 0; i < requests.size(); i++ ) {
                if (i == 0 ) {
                    AllFilteredLifts.clear();
                }
                Call call = client.newCall(requests.get(i));
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
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                        try {
                            String jsonData = response.body().string();
                            //Log.v(TAG, jsonData);
                            if (response.isSuccessful()) {
                                Log.d(TAG, "adding to AllfilteredLifts: " + parseLiftListDetails(jsonData));
                                AllFilteredLifts.add(parseLiftListDetails(jsonData));

                            } else {
                                alertUserAboutError();
                            }
                        } catch (IOException | ParseException e) {
                            Log.e(TAG, "Exception caught: ", e);
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON caught: ", e);
                        }
                    }
                });
            }

        }else {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }


        //runOnUiThread(new Runnable() {
         //   @Override
          //  public void run() {
           //     try {
            //        Log.d(TAG, "updateDisplay() — updating item: " + AllFilteredLifts + "size of AllFilteredLifts: " +  AllFilteredLifts.size()) ;
             //       updateDisplay();
              //  } catch (ParseException e) {
              //      e.printStackTrace();
              //  }
           // }
        // });

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override public void run() {
                try {
                    Log.d(TAG, "updateDisplay() — updating item: " + AllFilteredLifts + "size of AllFilteredLifts: " +  AllFilteredLifts.size()) ;
                    updateDisplay();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, 2000);


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
                    cal.getTime());
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
    private void updateDisplay() throws ParseException {

        // connect chart variable to the xml object
        LineChart chart = findViewById(R.id.barchart);

        // format the chart (colors, text size, label orientation,..)
        chart.getXAxis().setValueFormatter(new MyXAxisValueFormatter());
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getXAxis().setTextSize(12);
        chart.getXAxis().setLabelRotationAngle(45);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextSize(12);
        chart.getAxisRight().setDrawLabels(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.getLegend().setTextSize(12);

        if(AllFilteredLifts != null) {

            //ADD color matrix

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();


            for (int i = 0;i <AllFilteredLifts.size();i++) {

                if (AllFilteredLifts.get(i) != null) {

                    // retrieve current list of lifts
                    List<Lift> list = AllFilteredLifts.get(i).getLifts();
                    Log.d(TAG, "AllfilterLitfs.get(i) i = : " + i + "list = " + list);
                    ArrayList liftList = new ArrayList();


                    // sort by lift log time IMPORTANT
                    Collections.sort(list, new Comparator<Lift>() {
                        @Override
                        public int compare(final Lift object1, final Lift object2) {
                            return Long.compare(object1.getLogTime(), object2.getLogTime());
                        }
                    });

                    for (int j = 0; j < list.size(); j++) {
                        // create entry containing log time and weight of lift
                        liftList.add(new BarEntry(list.get(j).getLogTime(), list.get(j).getWeight()));
                    }

                    ///HER!

                    // create dataset
                    LineDataSet bardataset = new LineDataSet(liftList, SelectedLifts.get(i)); //typeFilter.getSelectedItem().toString());
                    Log.d(TAG, "selected lifts nr. " + i + " is " + SelectedLifts.get(i));

                    // format graph line
                    bardataset.setColor(colors[i]);
                    bardataset.setCircleColor(Color.BLACK);
                    bardataset.setFormSize(6);
                    bardataset.setValueTextSize(0);

                    dataSets.add(bardataset);


                }
                // animation on graph create
                chart.animateY(2000);

                // add data to chart
                LineData data = new LineData(dataSets);
                chart.setData(data);

                // format line label text color
                chart.getLineData().setValueTextColor(Color.WHITE);
            }
            
        }

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