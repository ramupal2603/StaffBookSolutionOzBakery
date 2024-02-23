package com.brinfotech.feedbacksystem.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brinfotech.feedbacksystem.R;
import com.brinfotech.feedbacksystem.data.nameList.NameListDataModel;

import java.util.ArrayList;
import java.util.List;

public class StaffNameListAdapter extends ArrayAdapter<NameListDataModel> {
    private List<NameListDataModel> arrayListFull;


    private Filter preBookingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<NameListDataModel> arrSuggestionList = new ArrayList<>();


            if (constraint == null || constraint.length() == 0) {
                arrSuggestionList.addAll(arrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (NameListDataModel arrItem : arrayListFull) {
                    String[] arrNameList = arrItem.getName().split(" ");
                    String surname = arrNameList.length > 0 ? arrNameList[0] : arrItem.getName();
                    if (surname.toLowerCase().trim().startsWith(filterPattern)) {
                        arrSuggestionList.add(arrItem);
                    }

                }
            }

            filterResults.values = arrSuggestionList;
            filterResults.count = arrSuggestionList.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((NameListDataModel) resultValue).getName();
        }
    };

    public StaffNameListAdapter(@NonNull Context context, List<NameListDataModel> arrayList) {
        super(context, 0, arrayList);
        arrayListFull = new ArrayList<>(arrayList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return preBookingFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.simple_textview, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.lbl_name);

        NameListDataModel arrItem = getItem(position);
        if (arrItem != null) {
            textView.setText(arrItem.getName());
        }

        return convertView;
    }
}
