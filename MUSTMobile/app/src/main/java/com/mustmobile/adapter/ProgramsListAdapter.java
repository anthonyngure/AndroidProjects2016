package com.mustmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.Program;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class ProgramsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Program> programArrayList;
    private LayoutInflater inflater;

    public ProgramsListAdapter(Context ctx, ArrayList<Program> programs){
        this.context = ctx;
        this.programArrayList = programs;
    }

    @Override
    public int getCount() {
        return programArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return programArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_item_program, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.list_item_program_name);
        TextView tvRequirements = (TextView) convertView.findViewById(R.id.list_item_program_requirements);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.list_item_program_duration);
        TextView tvMode = (TextView) convertView.findViewById(R.id.list_item_program_mode);
        TextView tvFees = (TextView) convertView.findViewById(R.id.list_item_program_fees);
        TextView tvCampus = (TextView) convertView.findViewById(R.id.list_item_program_campus);

        Program p = programArrayList.get(position);

        tvName.setText(p.getName());
        tvRequirements.setText(p.getRequirements());
        tvDuration.setText(p.getDuration());
        tvMode.setText(p.getMode());
        tvFees.setText(p.getFees());
        tvCampus.setText(p.getCampus());

        return convertView;
    }
}
