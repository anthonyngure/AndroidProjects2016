package com.mustmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.Semester;
import com.mustmobile.util.Helper;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class SemestersListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Semester> semesterArrayList;
    private LayoutInflater inflater;

    public SemestersListAdapter(Context ctx, ArrayList<Semester> semesters){
        this.context = ctx;
        this.semesterArrayList = semesters;
    }

    @Override
    public int getCount() {
        return semesterArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return semesterArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_semester, null);
        }

        TextView tvNewStudentsReporting = (TextView) convertView.findViewById(R.id.list_item_semester_new_students_reporting);
        TextView tvContinuingReporting = (TextView) convertView.findViewById(R.id.list_item_semester_continuing_students_reporting);
        TextView tvCommencementOfLectures = (TextView) convertView.findViewById(R.id.list_item_semester_commencement_of_lectures);
        TextView tvCatOne = (TextView) convertView.findViewById(R.id.list_item_semester_cat_one);
        TextView tvCatTwo = (TextView) convertView.findViewById(R.id.list_item_semester_cat_two);
        TextView tvEndOfLectures = (TextView) convertView.findViewById(R.id.list_item_semester_end_of_lectures);
        TextView tvExaminationsDates = (TextView) convertView.findViewById(R.id.list_item_semester_examination_dates);
        TextView tvTeachingWeeks = (TextView) convertView.findViewById(R.id.list_item_semester_teaching_weeks);
        TextView tvExaminationWeeks = (TextView) convertView.findViewById(R.id.list_item_semester_examination_weeks);
        TextView tvBreak = (TextView) convertView.findViewById(R.id.list_item_semester_break);
        TextView tvBreakWeeks = (TextView) convertView.findViewById(R.id.list_item_semester_break_weeks);
        TextView tvRemarks = (TextView) convertView.findViewById(R.id.list_item_semester_remarks);


        Semester s = semesterArrayList.get(position);

        tvNewStudentsReporting.setText(Helper.formatDate(s.getReportingNewStudentsDate()));
        tvContinuingReporting.setText(Helper.formatDate(s.getReportingContinuingStudentsDate()));
        tvCommencementOfLectures.setText(Helper.formatDate(s.getCommencementOfLecturesDate()));
        tvCatOne.setText(Helper.formatDate(s.getCatOneStartDate())+" to\n"+Helper.formatDate(s.getCatOneEndDate()));
        tvCatTwo.setText(Helper.formatDate(s.getCatTwoStartDate())+" to\n"+Helper.formatDate(s.getCatTwoEndDate()));
        tvEndOfLectures.setText(Helper.formatDate(s.getEndOfLecturesDate()));
        tvExaminationsDates.setText(Helper.formatDate(s.getExaminationsStartDate())+" to\n"+Helper.formatDate(s.getExaminationsEndDate()));
        tvTeachingWeeks.setText(s.getTeachingWeeks()+" Weeks");
        tvExaminationWeeks.setText(s.getExaminationWeeks()+" Weeks");
        tvBreak.setText(Helper.formatDate(s.getBreakStart())+" to\n"+Helper.formatDate(s.getBreakEnd()));
        tvBreakWeeks.setText(s.getBreakWeeks()+" Weeks");
        tvRemarks.setText(s.getRemarks());

        return convertView;
    }
}
