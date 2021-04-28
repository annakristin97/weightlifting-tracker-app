package com.example.weightliftingapp.UI;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.example.weightliftingapp.Entities.FilteredLifts;
import com.example.weightliftingapp.Entities.Lift;
import com.example.weightliftingapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button searchButton;
    public int[] colors = {Color.RED,Color.BLUE, Color.GREEN, Color.YELLOW, Color.BLACK, Color.GRAY, Color.WHITE, Color.CYAN, Color.MAGENTA, Color.DKGRAY};


    public static final String TAG = MainActivity.class.getSimpleName();

    private FilteredLifts mFilteredLifts;

    private ArrayList<String> list;
    private ArrayAdapter<String> arrayAdapter;

    RadioButton graphButton, tableButton;

    ArrayList<FilteredLifts> AllFilteredLifts = new ArrayList<>();

    FloatingActionButton AddButton;
    Spinner timeIntervalFilter, repsFilter, setsFilter;
    Spinner typeFilter;
    MultiSpinnerSearch multiSelectSpinnerWithSearch;
    List<String> SelectedLifts = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect our variables to the xml objects
        searchButton = findViewById(R.id.searchButton);
        timeIntervalFilter = findViewById(R.id.timeIntervalFilter);
        repsFilter = findViewById(R.id.repsFilter);
        setsFilter = findViewById(R.id.setsFilter);

        graphButton = findViewById(R.id.graph);
        tableButton = findViewById(R.id.table);

        final List<String> list = getDistinctLiftNames();

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

        //þegar við ýtum á plúsinn
        AddButton = findViewById(R.id.fab);
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddFragment();
            }
        });

        tableButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        tableButton.setTextColor(Color.BLACK);
                        graphButton.setTextColor(Color.WHITE);
                        openTableActivity();
                    }
                });

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

    public void openTableActivity(){
        Intent intent = new Intent(this,
                TableActivity.class);

        startActivity(intent);
        overridePendingTransition(0, 0);
    }
    
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

    public List<String> getDistinctLiftNames() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/lifts/liftnames")
                .build();

        return callBackendLiftNames(request);
    }

    public List<String> callBackendLiftNames(Request request) {
        OkHttpClient client = new OkHttpClient();
        List<String> list = new ArrayList<>();

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
                        String responseData = response.body().string();
                        Log.v(TAG, responseData);
                        if (response.isSuccessful()) {
                            JSONArray arr = new JSONArray(responseData);
                            List<String> list = new ArrayList<String>();
                            for(int i = 0; i < arr.length(); i++){
                                list.add(arr.get(i).toString());
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateAdapter(list);
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }

        return list;
    }

    public void updateAdapter(List<String> result) {
        final List<KeyPairBoolData> listArray1 = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(result.get(i));
            h.setSelected(i < 1);
            listArray1.add(h);
        }

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
    }

    /**
     * Call backend for filtered lifts
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

        }   else {
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
     * Take response from backend, parse from JSONObject -> Lift and store in filteredLifts variable
     */
    private FilteredLifts parseLiftListDetails(String jsonData) throws JSONException, ParseException {

        FilteredLifts filteredLifts = new FilteredLifts();

        ArrayList<Lift> lifts=new ArrayList<Lift>();
        JSONArray array=new JSONArray(jsonData);
        for(int i=0;i<array.length();i++){
            JSONObject elem=(JSONObject)array.get(i);

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

        if(AllFilteredLifts != null && AllFilteredLifts.size() > 0) {

            //ADD color matrix

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();


            for (int i = 0;i <AllFilteredLifts.size();i++) {

                if (AllFilteredLifts.get(i) != null && AllFilteredLifts.get(i).getLifts().size() > 0) {

                    // retrieve current list of lifts<s
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

                    String liftNameDisplay = "";

                    for(String liftname : SelectedLifts) {
                        if(list.get(0).getLiftName().contains(liftname)) liftNameDisplay = liftname;
                    }

                    ///HER!

                    // create dataset
                    LineDataSet bardataset = new LineDataSet(liftList, liftNameDisplay); //typeFilter.getSelectedItem().toString());
                    Log.d(TAG, "selected lifts nr. " + i + " is " + liftNameDisplay);

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