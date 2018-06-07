package com.batura.stas.notesaplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.batura.stas.notesaplication.Static.NoteUtils;

/**
 * Created by seeyo on 07.06.2018.
 */

public class SpinnerColorAdapter extends ArrayAdapter<String> {

    private String [] objects;
    private int[]     colorId;
    private boolean isDropdown;

    public SpinnerColorAdapter( Context context, int textViewResourceId, String[] objects, int[] colorId) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        this.colorId = colorId;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        isDropdown = true;
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        isDropdown = false;

        return getCustomView(position, convertView, parent);
    }

    public View getCustomView (int position, View convertView,  ViewGroup parent) {
        if ( objects.length != colorId.length) {
            throw new IllegalArgumentException("Wrong color array sizes: name lentgh=" +"" +
                    objects.length + " colors=" + colorId.length );
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.color_dropdown_item, parent, false);
        TextView label = (TextView) row.findViewById(R.id.color_spin_dropdown_item);
        if (isDropdown) {
            label.setText(objects[position]);
        }
        else {
            label.setText("Note color");
        }
        int color = NoteUtils.getBackColor(colorId[position]);
        label.setBackgroundColor(ContextCompat.getColor(getContext(), color));

        return row;
    }




}

