package com.example.weightliftingapp.UI;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weightliftingapp.R;

import java.io.IOException;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewLogActivity extends AppCompatActivity {
    public static final String TAG = NewLogActivity.class.getSimpleName();

    Button setsPlusButton, setsMinusButton, repsPlusButton, repsMinusButton, saveButton;
    EditText repsNumber, setsNumber, weightNumber;
    TextView liftNameText;

    private long sets = 1;
    private long reps = 1;
    private long weight = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlog);

        ButterKnife.bind(this);

        setsPlusButton = findViewById(R.id.button_plus_sets);
        setsMinusButton = findViewById(R.id.button_minus_sets);
        repsPlusButton = findViewById(R.id.button_plus_reps);
        repsMinusButton = findViewById(R.id.button_minus_reps);

        saveButton = findViewById(R.id.saveButton);

        repsNumber = findViewById(R.id.reps_number);
        setsNumber = findViewById(R.id.sets_number);
        weightNumber = findViewById(R.id.weight_number);

        liftNameText = findViewById(R.id.lift_name_header);

        liftNameText.setText(getIntent().getStringExtra("liftname"));

        setsPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sets += 1;
                setsNumber.setText(String.valueOf(sets));
            }
        });

        setsMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sets -= 1;
                setsNumber.setText(String.valueOf(sets));
            }
        });

        repsPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reps += 1;
                repsNumber.setText(String.valueOf(reps));
            }
        });

        repsMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reps -= 1;
                repsNumber.setText(String.valueOf(reps));
            }
        });

        repsNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    reps = Long.parseLong(s.toString());
            }
        });

        setsNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    sets = Long.parseLong(s.toString());
            }
        });

        weightNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    weight = Long.parseLong(s.toString());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLift();
            }
        });
    }

    private void saveLift() {
        Log.d(TAG, "Save new lift");
        RequestBody formBody = new FormBody.Builder()
                .add("liftName", getIntent().getStringExtra("liftname"))
                .add("weight", String.valueOf(weight))
                .add("sets", String.valueOf(sets))
                .add("reps", String.valueOf(reps))
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8090/lifts/add")
                .post(formBody)
                .build();

        callBackend(request);
    }

    /**
     * Call backend for saving lift
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
                        Log.v(TAG, "New lift saved: " + jsonData);
                        if (response.isSuccessful()) {
                            openMainActivity();
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
