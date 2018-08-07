package com.batura.stas.notesaplication.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by seeyo on 21.05.2018.
 */

public class NoteProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG = NoteProvider.class.getSimpleName();
    private NoteDbHelper mDbHelper;

    /** URI matcher code for the content URI for the pets table */
    private static final int NOTES = 666;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int NOTE_ID = 665;

    /** URI matcher code for the content URI for the images   */
    private static final int NOTE_IMAGES = 667;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY,NoteContract.PATH_NOTES,NOTES);
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY,NoteContract.PATH_NOTES + "/#",NOTE_ID);
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY,NoteContract.PATH_IMAGES,NOTE_IMAGES);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new NoteDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query( Uri uri,  String[] projection,  String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(NoteContract.NoteEntry.NOTES_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case NOTE_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(NoteContract.NoteEntry.NOTES_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case NOTE_IMAGES:
                // For the current NOTE_ID, _ID get Images from Images Table
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the images table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(NoteContract.NoteEntry.IMAGE_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return NoteContract.NoteEntry.CONTENT_LIST_TYPE;
            case NOTE_ID:
                return NoteContract.NoteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return insertNote(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertNote(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(NoteContract.NoteEntry.COLUMN_NOTE_BODY);
        if (name == null) {
            throw new IllegalArgumentException("trying to add empty note");
        }

        Integer color = values.getAsInteger(NoteContract.NoteEntry.COLUMN_NOTE_COLOR);
        if (color == null ) {
            throw new IllegalArgumentException("Wrong color for note");
        }

        Integer favoutite = values.getAsInteger(NoteContract.NoteEntry.COLUMN_NOTE_FAVOURITE);
        if ( favoutite != NoteContract.NoteEntry.NOTE_IS_FAV && favoutite != NoteContract.NoteEntry.NOTE_IS_NOT_FAV ) {
            throw new IllegalArgumentException("Wrong favorite id for note");
        }

        Integer pass = values.getAsInteger(NoteContract.NoteEntry.COLUMN_NOTE_PASSWORD);
        if ( pass != NoteContract.NoteEntry.NOTE_HAS_PASS && pass != NoteContract.NoteEntry.NOTE_HAS_NOT_PASS ) {
            throw new IllegalArgumentException("Wrong favorite id for note");
        }
        else {
            if (pass == NoteContract.NoteEntry.NOTE_HAS_PASS ) {
                //TODO проверяем хэш код праоля доделать потом
            }

        }

        Integer image = values.getAsInteger(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE);
        if ( image == null ) {
            throw new IllegalArgumentException("Wrong image id for note");
        }

        Integer widget = values.getAsInteger(NoteContract.NoteEntry.COLUMN_NOTE_WIDGET);
        if ( widget == null ) {
            throw new IllegalArgumentException("Wrong widget id for note");
        }


        Long time = values.getAsLong(NoteContract.NoteEntry.COLUMN_NOTE_TIME);
        if (time != null && time < 0) {
            throw new IllegalArgumentException("Wrong data for note");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(NoteContract.NoteEntry.NOTES_TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG, "Failed to insert row for " + uri);
            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return updateNote(uri, contentValues, selection, selectionArgs);
            case NOTE_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateNote(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateNote(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the {@link NoteEntry.COLUMN_NOTE_BODY} key is present,
        // check that the name value is not null.
        if (values.containsKey(NoteContract.NoteEntry.COLUMN_NOTE_BODY)) {
            String name = values.getAsString(NoteContract.NoteEntry.COLUMN_NOTE_BODY);
            if (name == null) {
                throw new IllegalArgumentException("Wrong title body " + name);
            }
        }

        // If the {@link NoteEntry.COLUMN_NOTE_COLOR} key is present,
        // check that the gender value is valid.
        if (values.containsKey(NoteContract.NoteEntry.COLUMN_NOTE_COLOR)) {
            Integer color = values.getAsInteger(NoteContract.NoteEntry.COLUMN_NOTE_COLOR);
            if (color == null ){
                throw new IllegalArgumentException("Note requires valid color " + color);
            }
        }



        // If the {@link NoteEntry.COLUMN_NOTE_TIME} key is present,
        // check that the weight value is valid.
        if (values.containsKey(NoteContract.NoteEntry.COLUMN_NOTE_TIME)) {
            // Check that the weight is greater than or equal to 0 kg
            Long time = values.getAsLong(NoteContract.NoteEntry.COLUMN_NOTE_TIME);
            if (time == null || time < 0) {
                throw new IllegalArgumentException("Note requires valid time " + time );
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        //return database.update(PetContract.PetEntry.IMAGE_TABLE_NAME, values, selection, selectionArgs);

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(NoteContract.NoteEntry.NOTES_TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                // Delete all rows that match the selection and selection args
                //    return database.delete(PetContract.PetEntry.IMAGE_TABLE_NAME, selection, selectionArgs);
                rowsDeleted = database.delete(NoteContract.NoteEntry.NOTES_TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                // Delete a single row given by the ID in the URI
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(NoteContract.NoteEntry.NOTES_TABLE_NAME, selection, selectionArgs);
                break;
            //return database.delete(PetContract.PetEntry.IMAGE_TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
}
