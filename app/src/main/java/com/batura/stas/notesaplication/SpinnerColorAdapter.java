package com.batura.stas.notesaplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by seeyo on 07.06.2018.
 */

public class SpinnerColorAdapter extends ArrayAdapter<String> {

    private String [] objects;

    public SpinnerColorAdapter( Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;

    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView (int position, View convertView,  ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.color_dropdown_item, parent, false);
        TextView label = (TextView) row.findViewById(R.id.color_spin_dropdown_item);
        label.setText(objects[position]);

        return row;
    }




}

