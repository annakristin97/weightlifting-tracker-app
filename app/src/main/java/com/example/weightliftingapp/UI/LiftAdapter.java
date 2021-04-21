package com.example.weightliftingapp.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import com.example.weightliftingapp.Entities.Lift;
import com.example.weightliftingapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Býr til "container" til þess að birta lift snyrtilega í PR skjá
 */
public class LiftAdapter extends ArrayAdapter<Lift> {

    private Context mContext;
    private List<Lift> liftList = new ArrayList<>();

    public LiftAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes List<Lift> list) {
        super(context, 0, list);
        mContext = context;
        liftList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

        Lift currentLift = liftList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.textView_name);
        name.setText(currentLift.getLiftName());

        TextView weight = (TextView) listItem.findViewById(R.id.textView_weight);
        weight.setText(currentLift.getWeight() + " KG");

        TextView set = (TextView) listItem.findViewById(R.id.textView_date);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(currentLift.getLogTime()));

        set.setText(dateString);


        return listItem;
    }
}
