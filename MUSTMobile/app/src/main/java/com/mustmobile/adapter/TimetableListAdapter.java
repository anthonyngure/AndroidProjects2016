package com.mustmobile.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.Timetable;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class TimetableListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Timetable> timetableArrayList;
    private LayoutInflater inflater;

    public TimetableListAdapter(Context ctx, ArrayList<Timetable> notices){
        this.context = ctx;
        this.timetableArrayList = notices;
    }

    @Override
    public int getCount() {
        return timetableArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return timetableArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_timetable, null);
        }

        TextView tvDay = (TextView) convertView.findViewById(R.id.list_item_timetable_day);
        TextView tvHourOne = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_one);
        TextView tvHourTwo = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_two);
        TextView tvHourThree = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_three);
        TextView tvHourFour = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_four);
        TextView tvHourFive = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_five);
        TextView tvHourSix = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_six);
        TextView tvHourSeven = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_seven);
        TextView tvHourEight = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_eight);
        TextView tvHourNine = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_nine);
        TextView tvHourTen = (TextView) convertView.findViewById(R.id.list_item_timetable_hour_ten);

        Timetable t = timetableArrayList.get(position);

        tvDay.setText(t.getDay().replace(" ","").toUpperCase()+"  "+t.getTimetableClass().replace(" ","").toUpperCase());
        tvHourOne.setText(setTimetable(t.getHourOne(), 1, tvHourOne));
        tvHourTwo.setText(setTimetable(t.getHourTwo(), 2, tvHourTwo));
        tvHourThree.setText(setTimetable(t.getHourThree(), 3, tvHourThree));
        tvHourFour.setText(setTimetable(t.getHourFour(), 4, tvHourFour));
        tvHourFive.setText(setTimetable(t.getHourFive(), 5, tvHourFive));
        tvHourSix.setText(setTimetable(t.getHourSix(), 6, tvHourSix));
        tvHourSeven.setText(setTimetable(t.getHourSeven(), 7, tvHourSeven));
        tvHourEight.setText(setTimetable(t.getHourEight(), 8, tvHourEight));
        tvHourNine.setText(setTimetable(t.getHourNine(), 9, tvHourNine));
        tvHourTen.setText(setTimetable(t.getHourTen(), 10, tvHourTen));
        return convertView;
    }

    private String setTimetable(String value, int hour, TextView textView){
        if (!TextUtils.isEmpty(value)){
            textView.setVisibility(View.VISIBLE);
            return sortHour(hour)+" : "+value.replace("\\"," ");
        } else {
            textView.setVisibility(View.GONE);
            return "";
        }
    }

    private String sortHour(int hour){
        switch (hour){
            case 1:
                return "8:00 - 9:00";
            case 2:
                return "9:00 - 10-00";
            case 3:
                return "10:00 - 11:00";
            case 4:
                return "11:00 - 12:00";
            case 5:
                return "12:00 - 13:00";
            case 6:
                return "13:00 - 14:00";
            case 7:
                return "14:00 - 15:00";
            case 8:
                return "15:00 - 16:00";
            case 9:
                return "16:00 - 17:00";
            case 10:
                return "17:00 - 18:00";
            default:
                return "8:00 - 9:00";
        }
    }
}
