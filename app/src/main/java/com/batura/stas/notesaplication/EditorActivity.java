package com.batura.stas.notesaplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.batura.stas.notesaplication.data.NoteContract;

import java.lang.reflect.Array;

/**
 * Created by HOME on 18.05.2018.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private EditText mTitleTextView;

    private EditText mBodyTextView;

    private int mColor = 665;

    private long mTime = 0;

    private final static int NOTE_LOADER_EDITOR = 0;

    private Uri mCurrentNoteUri;

    private Spinner mColorSpinner;

    private boolean mNoteHasChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mCurrentNoteUri = getIntent().getData();
        if (mCurrentNoteUri == null) {
            setTitle("Add note");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit note");
            getSupportLoaderManager().initLoader(NOTE_LOADER_EDITOR, null, this); //!!!! instead getLoaderManager
        }

        mTitleTextView = (EditText) findViewById(R.id.noteTitleInput);

        mBodyTextView = (EditText) findViewById(R.id.noteTextInput);

        mColorSpinner = (Spinner) findViewById(R.id.colorSpinner);

        mTime = System.currentTimeMillis();


        setupSpinner();

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
                saveNote();
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


    private void saveNote() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String titleString = mTitleTextView.getText().toString().trim();
        String bodyString = mBodyTextView.getText().toString().trim();
        String colorString = Integer.toString(mColor);
        String timeString = Long.toString(mTime);

        if (mCurrentNoteUri == null &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(bodyString) &&
                TextUtils.isEmpty(colorString) && TextUtils.isEmpty(timeString)) {
            return;
        }


        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_TITLE, titleString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_BODY, bodyString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, mColor);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_TIME, mTime);

        if (mCurrentNoteUri == null) {

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
        } else {
            int rowsAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_insert_note_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{
                NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_NOTE_TITLE,
                NoteContract.NoteEntry.COLUMN_NOTE_BODY,
                NoteContract.NoteEntry.COLUMN_NOTE_COLOR,
                NoteContract.NoteEntry.COLUMN_NOTE_TIME
        };
        return new android.support.v4.content.CursorLoader(this,
                mCurrentNoteUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
            int bodyColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_BODY);
            int colorColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_COLOR);
            int timeColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TIME);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String body = cursor.getString(bodyColumnIndex);
            int gender = cursor.getInt(colorColumnIndex);
            int weight = cursor.getInt(timeColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleTextView.setText(title);
            mBodyTextView.setText(body);
            //mWeightEditText.setText(Integer.toString(weight));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleTextView.setText("");
        mBodyTextView.setText("");
    }


    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        TextView spinnerDropdownTextView = (TextView)findViewById(R.id.color_spin_dropdown_item);

        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.color_array_string, R.layout.color_spiner_item);
        int[] colorId = {665,666,667,668,669,670,671};
        SpinnerColorAdapter myAdapter = new  SpinnerColorAdapter (EditorActivity.this,
                R.layout.color_dropdown_item , getResources().getStringArray(R.array.color_array_string),colorId);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(R.layout.color_dropdown_item);

        // Apply the adapter to the spinner
        mColorSpinner.setAdapter(myAdapter);

        // Set the integer mSelected to the constant values
        mColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.color_default))) {
                        mColor = 665; // Male
                    } else if (selection.equals(getString(R.string.color_red))) {
                        mColor = NoteContract.NoteEntry.COLOR_RED; // Female
                    } else if (selection.equals(getString(R.string.color_orange))) {
                        mColor = NoteContract.NoteEntry.COLOR_ORANGE;
                    } else if (selection.equals(getString(R.string.color_yellow))) {
                        mColor = NoteContract.NoteEntry.COLOR_YELLOW;
                    } else if (selection.equals(getString(R.string.color_green))) {
                        mColor = NoteContract.NoteEntry.COLOR_GREEN;
                    } else if (selection.equals(getString(R.string.color_blue))) {
                        mColor = NoteContract.NoteEntry.COLOR_BLUE;
                    } else if (selection.equals(getString(R.string.color_purple))) {
                        mColor = NoteContract.NoteEntry.COLOR_PURPLE;
                        ; // Unknown
                    } else {
                        Log.d(LOG_TAG, "Wrong color spinner value");
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mColor = 665; // default
            }
        });
    }
}
