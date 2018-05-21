package com.batura.stas.notesaplication.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by seeyo on 21.05.2018.
 */

public class NoteContract {

    public static final String CONTENT_AUTHORITY = "com.batura.stas.notesaplication";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.batura.stas.notesaplication is a valid path for
     * looking at pet data. content://com.batura.stas.notesaplication/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_NOTES = "notes";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private NoteContract() {

    }

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single note.
     */
    public final static class NoteEntry implements BaseColumns    {



    }
}
