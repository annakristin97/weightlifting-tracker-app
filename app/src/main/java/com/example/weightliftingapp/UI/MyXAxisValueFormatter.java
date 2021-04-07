package com.example.weightliftingapp.UI;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Takes date in milliseconds and formats it as "D/M/YY" on the X-axis
 */
public class MyXAxisValueFormatter extends IndexAxisValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        long emissionsMilliSince1970Time = ((long) value);

        // Show time in local version
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
        DateFormat dateTimeFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        return dateTimeFormat.format(timeMilliseconds);
    }
}
