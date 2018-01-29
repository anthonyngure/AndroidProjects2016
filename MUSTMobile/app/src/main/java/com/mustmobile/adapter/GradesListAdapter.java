package com.mustmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.Grade;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class GradesListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Grade> gradeArrayList;
    private LayoutInflater inflater;

    public GradesListAdapter(Context ctx, ArrayList<Grade> notices){
        this.context = ctx;
        this.gradeArrayList = notices;
    }

    @Override
    public int getCount() {
        return gradeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return gradeArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_grade, null);
        }

        TextView tvGrade = (TextView) convertView.findViewById(R.id.list_item_grade_grade);
        TextView tvUnitCode = (TextView) convertView.findViewById(R.id.list_item_grade_unit_code);
        TextView tvUnitName = (TextView) convertView.findViewById(R.id.list_item_grade_unit_name);

        Grade g = gradeArrayList.get(position);

        tvGrade.setText(g.getGrade());
        tvUnitCode.setText(g.getUnitCode());
        tvUnitName.setText(g.getUnitName());

        return convertView;
    }
}
