package com.batura.stas.notesaplication;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.batura.stas.notesaplication.Static.NoteUtils;
import com.batura.stas.notesaplication.data.NoteContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * {@link NoteCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class NoteCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link NoteCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public NoteCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return LayoutInflater.from(context).inflate(R.layout.);
        return LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView titleTextView = (TextView) view.findViewById(R.id.titleItem);
        TextView bodyTextView = (TextView) view.findViewById(R.id.bodyItem);
        TextView dateTextView = (TextView) view.findViewById(R.id.date);
        TextView timeTextView = (TextView) view.findViewById(R.id.time);

        int titleColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
        int bodyColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_BODY);
        int dateColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TIME);
        int colorColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_COLOR);

        String noteTitle = cursor.getString(titleColoumnIndex);
        String noteBody = cursor.getString(bodyColoumnIndex);
        Long noteTime = cursor.getLong(dateColoumnIndex);
        int noteColor = cursor.getInt(colorColoumnIndex);

        if (noteTitle.length() == 0 || noteTitle == null) {
            titleTextView.setText("No title");
        } else {
            titleTextView.setText(noteTitle);
        }
        if (noteBody.length() == 0 || noteBody == null) {
            bodyTextView.setText("No text");
        } else {
            bodyTextView.setText(noteBody);
        }


        if (noteTime < 0) {
            dateTextView.setText("Wrong time");
        } else {

            Date dateObject = new Date(noteTime);
            String formattedDate = formatDate(dateObject);
            String formattetTime = formatTime(dateObject);
            dateTextView.setText(formattedDate);
            timeTextView.setText(formattetTime);
        }

        // set back ground for note list item
        if (noteColor != NoteContract.NoteEntry.COLOR_DEFAULT) {
            int color = NoteUtils.getBackColor(noteColor);
            view.setBackgroundColor(ContextCompat.getColor(context, color));
        }


    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");

//        SimpleDateFormat dateFormat = new SimpleDateFormat(dateObject);

        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }



}

