package com.example.weightliftingapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.weightliftingapp.Entities.FilteredLifts;
import com.example.weightliftingapp.Entities.Lift;
import com.example.weightliftingapp.R;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BarChartActivity extends AppCompatActivity {

    private FilteredLifts mFilteredLifts;

    public static final String TAG = BarChartActivity.class.getSimpleName();

    Button searchButton;
    Spinner timeIntervalFilter, typeFilter, repsFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        // connect our variables to the xml objects
        searchButton = findViewById(R.id.searchButton);
        timeIntervalFilter = findViewById(R.id.timeIntervalFilter);
        typeFilter = findViewById(R.id.typeFilter);
        repsFilter = findViewById(R.id.repsFilter);

        // click search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update graph content
                getLifts(typeFilter.getSelectedItem().toString());
            }
        });
    }

    /**
     * Get lifts by liftName
     * TODO: bæta við timeInterval, reps og sets
     */
    public void getLifts(String type) {
        RequestBody formBody = new FormBody.Builder()
                .add("liftName", type)
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/lifts/liftname")
                .post(formBody)
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
                            mFilteredLifts = parseLiftListDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        updateDisplay();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
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
        } else {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }
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

    /**
     * Update graph based on filtered data
     */
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

        if(mFilteredLifts != null) {
            // retrieve current list of lifts
            List<Lift> list = mFilteredLifts.getLifts();
            ArrayList liftList = new ArrayList();

            // sort by lift log time IMPORTANT
            Collections.sort(list, new Comparator<Lift>() {
                @Override
                public int compare(final Lift object1, final Lift object2) {
                    return Long.compare(object1.getLogTime(), object2.getLogTime());
                }
            });

            for (int i=0; i<list.size(); i++) {
                // create entry containing log time and weight of lift
                liftList.add(new BarEntry(list.get(i).getLogTime(), list.get(i).getWeight()));
            }

            // create dataset
            LineDataSet bardataset = new LineDataSet(liftList, typeFilter.getSelectedItem().toString());

            // format graph line
            bardataset.setColor(Color.RED);
            bardataset.setCircleColor(Color.BLACK);
            bardataset.setFormSize(6);
            bardataset.setValueTextSize(0);

            // animation on graph create
            chart.animateY(5000);

            // add data to chart
            LineData data = new LineData(bardataset);
            chart.setData(data);

            // format line label text color
            chart.getLineData().setValueTextColor(Color.WHITE);
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