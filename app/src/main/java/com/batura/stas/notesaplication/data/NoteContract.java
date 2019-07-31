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

    public static final String PATH_NOTES_ALL = "notes_all";

    public static final String PATH_IMAGES = "images";

    public static final String PATH_FOLDERS = "folders";


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

        /**
         * The MIME type of the {@link #CONTENT_URI} for a notes images.
         */
        public static final String CONTENT_IMAGE_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a notes images.
         */
        public static final String CONTENT_FOLDERS_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGES;

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI_ALL = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES_ALL);

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI_IMAGES = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_IMAGES);

        /** The content URI to access the folder data in the provider */
        public static final Uri CONTENT_URI_FOLDERS = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FOLDERS);
        /**
         * Name of database table for notes
         */
        public final static String NOTES_TABLE_NAME = "notes";

        /**
         * Name of database table for images
         */
        public final static String IMAGE_TABLE_NAME = "images";

        /**
         * Name of database table for folders
         */
        public final static String FOLDER_TABLE_NAME = "folders";

        /**
         * Unique ID number for the note (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Title of the pet.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_NOTE_TITLE = "title";

        /**Body
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

        /**           *
         * Favorite.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NOTE_FAVOURITE = "favourite";
        public final static  int NOTE_IS_FAV = 1;
        public final static  int NOTE_IS_NOT_FAV = 0;
        /**           *
         * Password.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NOTE_PASSWORD = "password";
        public final static  int NOTE_HAS_PASS = 1;
        public final static  int NOTE_HAS_NOT_PASS = 0;

        /**           *
         * Password hash code.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NOTE_PASSWORD_HASH = "passwordHash";

        /**           *
         * Image id.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NOTE_IMAGE = "imageId";

        /**           *
         * Widget.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NOTE_WIDGET = "widget";

        /**           *
         * Folder.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NOTE_FOLDER = "folder";

        /**
         * Unique ID number for the note (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NOTE_FOLDER_ID = "folder_id";

        /**
         * Unique ID number for the note (only for use in the database table).
         * <p>
         * Type: String
         */
        public final static String FOLDER_NAME = "folder_name";

         public static final int COLOR_DEFAULT = 665;

         public static final int COLOR_RED = 666;

         public static final int COLOR_ORANGE = 667;

         public static final int COLOR_YELLOW = 668;

         public static final int COLOR_GREEN = 669;

         public static final int COLOR_BLUE = 670;

         public static final int COLOR_PURPLE = 671;

        /**
         * Unique ID number for the image DB link to the Note NOTE_ID  (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String NOTE_ID = "note_id";

        /**
         * Name of the image. Max 10 images manes _0X (X : 1,2,3...10)
         * <p>
         * Type: TEXT
         */
        public final static String IMAGE_NAME_01 = "name_01";
        public final static String IMAGE_NAME_02 = "name_02";
        public final static String IMAGE_NAME_03 = "name_03";
        public final static String IMAGE_NAME_04 = "name_04";
        public final static String IMAGE_NAME_05 = "name_05";
        public final static String IMAGE_NAME_06 = "name_06";
        public final static String IMAGE_NAME_07 = "name_07";
        public final static String IMAGE_NAME_08 = "name_08";
        public final static String IMAGE_NAME_09 = "name_09";
        public final static String IMAGE_NAME_10 = "name_10";


    }
}
