package com.batura.stas.notesaplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by seeyo on 21.05.2018.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = NoteDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "notepad.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 6;

    /**
     * Constructs a new instance of {@link NoteDbHelper}.
     *
     * @param context of the app
     */
    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NoteContract.NoteEntry.NOTES_TABLE_NAME + " ("
                + NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_TITLE+ " TEXT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_BODY + " TEXT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_COLOR + " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_FAVOURITE + " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_PASSWORD + " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_PASSWORD_HASH + " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_IMAGE + " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_WIDGET + " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_TIME +  " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_FOLDER +  " INTEGER NOT NULL DEFAULT 0"  +");"
               ;

        // Create a String that contains the SQL statement to create the images table
        String SQL_CREATE_IMAGES_TABLE = "CREATE TABLE " + NoteContract.NoteEntry.IMAGE_TABLE_NAME + " ("
                + NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteContract.NoteEntry.NOTE_ID + " INTEGER, "
                + NoteContract.NoteEntry.IMAGE_NAME_01+ " TEXT, "
                + NoteContract.NoteEntry.IMAGE_NAME_02+ " TEXT, "
                + NoteContract.NoteEntry.IMAGE_NAME_03+ " TEXT, "

                + "FOREIGN KEY (" + NoteContract.NoteEntry.NOTE_ID + ") REFERENCES "
                + NoteContract.NoteEntry.NOTES_TABLE_NAME
                + "(" + NoteContract.NoteEntry._ID + ")); ";

        String SQL_CREATE_FOLDERS_TABLE = "CREATE TABLE " + NoteContract.NoteEntry.FOLDER_TABLE_NAME + " ("
                + NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_FOLDER_ID + " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.FOLDER_NAME + " TEXT " + ");"
                ;

        db.execSQL(SQL_CREATE_IMAGES_TABLE);
        db.execSQL(SQL_CREATE_NOTES_TABLE);
        db.execSQL(SQL_CREATE_FOLDERS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NoteEntry.NOTES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NoteEntry.IMAGE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NoteEntry.FOLDER_TABLE_NAME);

        // Create tables again
        onCreate(db);
    }
}
