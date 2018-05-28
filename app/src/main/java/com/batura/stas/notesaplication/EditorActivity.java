package com.batura.stas.notesaplication;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.batura.stas.notesaplication.data.NoteContract;

/**
 * Created by HOME on 18.05.2018.
 */

public class EditorActivity extends AppCompatActivity {

    private EditText mTitleTextView;

    private EditText mBodyTextView;

    private final int mColor = 0;

    private final int mTime = 0 ;

    private Uri mCurrentPetUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mTitleTextView = (EditText)findViewById(R.id.noteTitleInput);

        mBodyTextView = (EditText) findViewById(R.id.noteTextInput);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertNote();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void insertNote() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String titleString = mTitleTextView.getText().toString().trim();
        String bodyString = mBodyTextView.getText().toString().trim();
        String colorString = Integer.toString( mColor);
        String timeString = Integer.toString( mTime);

        if (mCurrentPetUri == null &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(bodyString) &&
                TextUtils.isEmpty(colorString) && TextUtils.isEmpty(timeString) ) {return;}

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_TITLE, titleString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_BODY, bodyString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, mColor);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_TIME, mTime);

        // Insert a new row for pet in the database, returning the ID of that new row.
        // Insert a new pet into the provider, returning the content URI for the new pet.
        Uri newUri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_note_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
        }
    }
}
