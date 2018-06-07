package com.batura.stas.notesaplication.data;

import android.content.ContentResolver;
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
     * Inner class that defines constant values for the notes database table.
     * Each entry in the table represents a single note.
     */
    public final static class NoteEntry implements BaseColumns    {
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);
        /**
         * Name of database table for notes
         */
        public final static String TABLE_NAME = "notes";

        /**
         * Unique ID number for the note (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;
        /**
         * Name of the pet.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_NOTE_TITLE = "title";
        /**
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_NOTE_BODY = "body";

        /**           *
         * color.
         * <p>
         * Type: INTEGER
         */
          public final static String  COLUMN_NOTE_COLOR = "color";

        /**           *
         * time.
         * <p>
         * Type: LONG
         */
         public final static String COLUMN_NOTE_TIME = "time";

         public static final int COLOR_DEFAULT = 665;

         public static final int COLOR_RED = 666;

         public static final int COLOR_ORANGE = 667;

         public static final int COLOR_YELLOW = 668;

         public static final int COLOR_GREEN = 669;

         public static final int COLOR_BLUE = 670;

         public static final int COLOR_PURPLE = 671;


    }
}
