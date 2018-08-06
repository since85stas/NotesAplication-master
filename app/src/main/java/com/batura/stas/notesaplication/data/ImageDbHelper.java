package com.batura.stas.notesaplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.batura.stas.notesaplication.data.ImageContract.ImageEntry;

/**
 * Created by seeyo on 06.08.2018.
 */

public class ImageDbHelper extends SQLiteOpenHelper {

    public static final String TAG = ImageDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "images.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link NoteDbHelper}.
     *
     * @param context of the app
     */
    public ImageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the images table
        String SQL_CREATE_IMAGES_TABLE = "CREATE TABLE " + ImageEntry.TABLE_NAME + " ("
                + ImageEntry.NOTE_ID + " INTEGER PRIMARY KEY , "
                + "FOREIGN KEY (" + ImageEntry.NOTE_ID + ") REFERENCES " + NoteContract.NoteEntry.TABLE_NAME
                + "(" + NoteContract.NoteEntry._ID + ")"

                + ImageEntry.IMAGE_NAME_01+ " TEXT, "
                + ImageEntry.IMAGE_NAME_02+ " TEXT, "
                + ImageEntry.IMAGE_NAME_03+ " TEXT);"
               
                ;
        db.execSQL(SQL_CREATE_IMAGES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
