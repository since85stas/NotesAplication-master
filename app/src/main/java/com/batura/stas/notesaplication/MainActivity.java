package com.batura.stas.notesaplication;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.batura.stas.notesaplication.data.NoteContract;
import com.batura.stas.notesaplication.data.NoteDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    private NoteDbHelper mDbHelper;

    private NoteCursorAdapter mCursorAdapter;

    private static final int NOTE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add a note", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this,EditorActivity.class) ;
                startActivity(intent);
            }
        });

        ListView noteListView = (ListView) findViewById(R.id.list);

        mCursorAdapter = new NoteCursorAdapter(this,null);
        noteListView.setAdapter(mCursorAdapter);

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI,id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(NOTE_LOADER,null,this);

//        mDbHelper = new NoteDbHelper(this);
//        displayDatabaseInfo();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.add_dummy_data) {
            insertNote();
            //displayDatabaseInfo();
        }

        return super.onOptionsItemSelected(item);
    }

        /* +     * Temporary helper method to display information in the onscreen TextView about the state of
        * +     * the pets database.
        * +
        */
        private void insertNote() {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NoteContract.NoteEntry.COLUMN_NOTE_TITLE, "Test");
            values.put(NoteContract.NoteEntry.COLUMN_NOTE_BODY, "Test note");
            values.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, 1);
            values.put(NoteContract.NoteEntry.COLUMN_NOTE_TIME, 7);
            // Insert a new row for Toto in the database, returning the ID of that new row.
            // The first argument for db.insert() is the pets table name.
            // The second argument provides the name of a column in which the framework
            // can insert NULL in the event that the ContentValues is empty (if
            // this is set to "null", then the framework will not insert a row when
            // there are no values).
            //Uri newUri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI,values);
            long newRowId = db.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);
        }

//    private void displayDatabaseInfo() {
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//        // Create and/or open a database to read from it
//
//        // Perform this raw SQL query "SELECT * FROM notes"
//        // to get a Cursor that contains all rows from the pets table.
//        //Cursor cursor = db.rawQuery("SELECT * FROM " + PetContract.PetEntry.TABLE_NAME, null);
//
//        TextView displayView = findViewById(R.id.displayView);
//
//        String[] projection = new String[]{
//                NoteContract.NoteEntry._ID,
//                NoteContract.NoteEntry.COLUMN_NOTE_TITLE,
//                NoteContract.NoteEntry.COLUMN_NOTE_BODY,
//                NoteContract.NoteEntry.COLUMN_NOTE_COLOR,
//                NoteContract.NoteEntry.COLUMN_NOTE_TIME,
//                };
////      Cu
//        Cursor cursor = getContentResolver().query(NoteContract.NoteEntry.CONTENT_URI,projection,null,null,null);
//
////        ListView petsListView = (ListView)findViewById(R.id.list);
////
////        PetCursorAdapter petAdapter =new PetCursorAdapter(this,cursor);
////        petsListView.setAdapter(petAdapter);
//
//        try {
//            // Display the number of rows in the Cursor (which reflects the number of rows in the
//            // pets table in the database).
//            // Create a header in the Text View that looks like this:
//            //
//            // The pets table contains <number of rows in Cursor> pets.
//            // _id - name - breed - gender - weight
//            //
//            // In the while loop below, iterate through the rows of the cursor and display
//            // the information from each column in this order.
//            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
//            displayView.append(NoteContract.NoteEntry._ID + " - " +
//                    NoteContract.NoteEntry.COLUMN_NOTE_TITLE + " - " +
//                    NoteContract.NoteEntry.COLUMN_NOTE_BODY + " - " +
//                    NoteContract.NoteEntry.COLUMN_NOTE_TIME + " - " +
//                    NoteContract.NoteEntry.COLUMN_NOTE_COLOR + "\n");
//
////            // Figure out the index of each column
//            int idColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry._ID);
//            int titleColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
//            int bodyColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_BODY);
//            int timeColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TIME);
//            int colorColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_COLOR);
////
////            // Iterate through all the returned rows in the cursor
//            while (cursor.moveToNext()) {
//                // Use that index to extract the String or Int value of the word
//                // at the current row the cursor is on.
//                int currentID = cursor.getInt(idColumnIndex);
//                String currentTitle = cursor.getString(titleColumnIndex);
//                String currentBody = cursor.getString(bodyColumnIndex);
//                int currentTime = cursor.getInt(timeColumnIndex);
//                int currentColor = cursor.getInt(colorColumnIndex);
//                displayView.append(("\n" + currentID + " - " +
//                        currentTitle + " - " +
//                        currentBody + " - " +
//                        currentTime + " - " +
//                        currentColor));
//            }
//        } finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//            cursor.close();
//        }
//    }

    @Override
    public Loader<Cursor>  onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{
                NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_NOTE_TITLE,
                NoteContract.NoteEntry.COLUMN_NOTE_BODY,
                NoteContract.NoteEntry.COLUMN_NOTE_COLOR,
                NoteContract.NoteEntry.COLUMN_NOTE_TIME
        };
        return new CursorLoader( this,
                NoteContract.NoteEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
