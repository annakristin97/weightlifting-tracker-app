package com.example.weightliftingapp.UI;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weightliftingapp.Entities.RecyclerItemClickListener;
import com.example.weightliftingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddFragment extends AppCompatActivity {

    public static final String TAG = AddFragment.class.getSimpleName();

    RecyclerView recyclerView;
    RecyclerViewAdapter rcadapter;
    Button addButton;
    EditText newLift;
    //String s1[];
    ArrayList<String> s1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add);

        recyclerView = findViewById(R.id.recyclerView);

        ArrayList<String> liftList = new ArrayList<>();
        s1 = getDistinctLiftNames();
        //String[] myLiftArray = getDistinctLiftNames(); //{"Deadlift", "Squat", "Bench Press", "Shoulder Press", "Push Press", "Snatch", "Clean", "Power Snatch", "Power Clean"};
        //s1 = myLiftArray;

        rcadapter = new RecyclerViewAdapter(this, s1);
        recyclerView.setAdapter(rcadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));


        addButton = findViewById(R.id.button_new_liftname);
        newLift = findViewById(R.id.new_liftname);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newLiftName = newLift.getText().toString();
                insertNewLift(0, newLiftName);
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        openNewLog(s1.get(position));
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        //TODO: TAKA ÚT - BARA TIL AÐ TESTA NEW LOG :D
        //addButton = findViewById(R.id.button_new_liftname);
        //addButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        openNewLog();
        //    }
        //});
    }

    public void insertNewLift(int position, String name) {
        ArrayList<String> newList = (ArrayList)s1.clone();
        newList.add(position, name);
        updateAdapter(newList);
    }

    public void openNewLog(String liftname){
        Intent intent = new Intent(this, NewLogActivity.class);
        intent.putExtra("liftname", liftname);
        startActivity(intent);

    }

    public ArrayList<String> getDistinctLiftNames() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/lifts/liftnames")
                .build();

        return callBackendLiftNames(request);
    }

    public ArrayList<String> callBackendLiftNames(Request request) {
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
                                    //updateAdapter(list.toArray(new String[0]));
                                    updateAdapter((ArrayList<String>) list);
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

        return (ArrayList<String>) list;
    }

    public void updateAdapter(ArrayList<String> result) {
        s1 = result;
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, s1);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

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
