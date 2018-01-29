package com.mustmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.FeeStructure;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class FeeStructuresListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FeeStructure> feeStructureArrayList;
    private LayoutInflater inflater;

    public FeeStructuresListAdapter(Context ctx, ArrayList<FeeStructure> feeStructures){
        this.context = ctx;
        this.feeStructureArrayList = feeStructures;
    }

    @Override
    public int getCount() {
        return feeStructureArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return feeStructureArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_feestructure, null);
        }


        TextView tvRegistration = (TextView) convertView.findViewById(R.id.list_item_feestructure_registration_fee);
        TextView tvStudentUnion = (TextView) convertView.findViewById(R.id.list_item_feestructure_student_union_sem_one);

        TextView tvTotalPayableSemOne = (TextView) convertView.findViewById(R.id.list_item_feestructure_total_payable_sem_one);
        TextView tvTotalPayableSemTwo = (TextView) convertView.findViewById(R.id.list_item_feestructure_total_payable_sem_two);


        TextView tvAccomodation = (TextView) convertView.findViewById(R.id.list_item_feestructure_accomodation);
        TextView tvAttachment = (TextView) convertView.findViewById(R.id.list_item_feestructure_attachment);

        TextView tvTuitionSemOne = (TextView) convertView.findViewById(R.id.list_item_feestructure_tuition_sem_one);
        TextView tvTuitionSemTwo = (TextView) convertView.findViewById(R.id.list_item_feestructure_tuition_sem_two);

        TextView tvExaminationSemOne = (TextView) convertView.findViewById(R.id.list_item_feestructure_examination_sem_one);
        TextView tvExaminationSemTwo = (TextView) convertView.findViewById(R.id.list_item_feestructure_examination_sem_two);

        TextView tvMedicalSemOne = (TextView) convertView.findViewById(R.id.list_item_feestructure_medical_sem_one);
        TextView tvMedicalSemTwo = (TextView) convertView.findViewById(R.id.list_item_feestructure_medical_sem_two);

        TextView tvActivitySemOne = (TextView) convertView.findViewById(R.id.list_item_feestructure_activity_sem_one);
        TextView tvActivitySemTwo = (TextView) convertView.findViewById(R.id.list_item_feestructure_activity_sem_two);

        TextView tvInternetSemOne = (TextView) convertView.findViewById(R.id.list_item_feestructure_internet_sem_one);
        TextView tvInternetSemTwo = (TextView) convertView.findViewById(R.id.list_item_feestructure_internet_sem_two);

        TextView tvTripSemOne = (TextView) convertView.findViewById(R.id.list_item_feestructure_trip_sem_one);
        TextView tvTripSemTwo = (TextView) convertView.findViewById(R.id.list_item_feestructure_trip_sem_two);

        TextView tvLibrarySemOne = (TextView) convertView.findViewById(R.id.list_item_feestructure_library_sem_one);
        TextView tvLibrarySemTwo = (TextView) convertView.findViewById(R.id.list_item_feestructure_library_sem_two);

        FeeStructure f = feeStructureArrayList.get(position);

        tvTuitionSemOne.setText(f.getTuitionSemOne());
        tvTuitionSemTwo.setText(getDotsAndLabel() + f.getTuitionSemTwo());

        tvRegistration.setText(f.getRegistration());

        tvExaminationSemOne.setText(f.getExaminationSemOne());
        tvExaminationSemTwo.setText(getDotsAndLabel()+f.getExaminationSemTwo());

        tvMedicalSemOne.setText(f.getMedicalSemOne());
        tvMedicalSemTwo.setText(getDotsAndLabel()+f.getMedicalSemTwo());

        tvActivitySemOne.setText(f.getActivitySemOne());
        tvActivitySemTwo.setText(getDotsAndLabel()+f.getActivitySemTwo());

        tvInternetSemOne.setText(f.getInternetSemOne());
        tvInternetSemTwo.setText(getDotsAndLabel()+f.getInternetSemTwo());

        tvTripSemOne.setText(f.getTripSemOne());
        tvTripSemTwo.setText(getDotsAndLabel()+f.getTripSemTwo());

        tvLibrarySemOne.setText(f.getLibrarySemOne());
        tvLibrarySemTwo.setText(getDotsAndLabel()+f.getLibrarySemTwo());

        tvStudentUnion.setText(f.getStudentUnion());

        tvTotalPayableSemOne.setText(f.getTotalPayableSemOne());
        tvTotalPayableSemTwo.setText(getDotsAndLabel()+f.getTotalPayableSemTwo());

        tvAccomodation.setText(context.getResources().getString(R.string.accomodation_heading)
                +getDotsAndLabel()
                +f.getAccommodation()+"/=");

        tvAttachment.setText("4. " + context.getResources().getString(R.string.attachment_heading)
                + getDotsAndLabel()
                + f.getAttachment() + "/=");

        return convertView;
    }

    private String getDotsAndLabel(){
        return " KShs. ";
    }
}
