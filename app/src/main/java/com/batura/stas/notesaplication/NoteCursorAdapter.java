package com.batura.stas.notesaplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.batura.stas.notesaplication.data.NoteContract;

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
        return LayoutInflater.from(context).inflate(R.layout.note_list_item,parent,false);
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

        TextView titleTextView = (TextView)view.findViewById(R.id.titleItem);
        TextView bodyTextView = (TextView)view.findViewById(R.id.bodyItem);

        int titleColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
        int bodyColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_BODY);

        String noteTitle = cursor.getString(titleColoumnIndex);
        String noteBody = cursor.getString(bodyColoumnIndex);

        if (noteTitle.length() == 0 || noteTitle == null ) {
            titleTextView.setText("No title");
        }
        else {
            titleTextView.setText(noteTitle);
        }
        if (noteBody.length() == 0 || noteBody == null ) {
            bodyTextView.setText("No text");
        }
        else {
            bodyTextView.setText(noteBody);
        }
//        TextView nameTextView = (TextView)view.findViewById(R.id.name);
//        TextView summaryTextView = (TextView)view.findViewById(R.id.summary);
//
//        int nameColoumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
//        int summaryColoumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
//
//        String petName = cursor.getString(nameColoumnIndex);
//        String petBreed = cursor.getString(summaryColoumnIndex);
//
//        nameTextView.setText(petName) ;
//
//        if (TextUtils.isEmpty(petBreed)) {
//            petBreed = context.getString(R.string.unknown_breed);
//        }
//        summaryTextView.setText(petBreed);

    }
}
