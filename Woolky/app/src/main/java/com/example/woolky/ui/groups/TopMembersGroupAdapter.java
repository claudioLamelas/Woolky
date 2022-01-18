package com.example.woolky.ui.groups;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.woolky.R;
import com.example.woolky.domain.User;

import java.util.List;

public class TopMembersGroupAdapter extends ArrayAdapter {

    private List<User> topMembers;
    private Activity context;
    private int week;

    public TopMembersGroupAdapter (Activity context, List<User> members, int weekNumber) {
        super(context, R.layout.top_member_item, members);

        this.context = context;
        // vem ordenados do melhor para pior
        topMembers = members;
        week = weekNumber;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();

        if(convertView == null)
            row = inflater.inflate(R.layout.top_member_item, null, true);

        TextView steps = (TextView) row.findViewById(R.id.steps_TV);
        int totalSteps = topMembers.get(position).getTotalNumberSteps(week);
        steps.setText(Integer.toString(totalSteps));

        ImageView member = (ImageView) row.findViewById(R.id.member_image);
        Glide.with(context).load(Uri.parse(topMembers.get(position).getPhotoUrl())).circleCrop().into(member);

        ProgressBar pb = row.findViewById(R.id.steps_count_PB);
        if (position == 0){
            pb.setProgress(100);

        }
        else{
            int valor = totalSteps / topMembers.get(0).getTotalNumberSteps(week);
            pb.setProgress(valor);
        }


        return  row;
    }
}
