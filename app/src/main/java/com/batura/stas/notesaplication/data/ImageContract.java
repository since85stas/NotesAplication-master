package com.batura.stas.notesaplication.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by seeyo on 06.08.2018.
 * Класс описывает структуру БД для хранения информации о сохран фото.
 */

public class ImageContract {

    public static final String CONTENT_AUTHORITY = "com.batura.stas.notesaplication";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.batura.stas.notesaplication is a valid path for
     * looking at pet data. content://com.batura.stas.notesaplication/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_NOTES = "images";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ImageContract() {

    }

    /**
     * Inner class that defines constant values for the notes database table.
     * Each entry in the table represents a single note.
     */
    public final static class ImageEntry implements BaseColumns {
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
        public final static String TABLE_NAME = "images";

        /**
         * Unique ID number for the image DB link to the Note NOTE_ID  (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String NOTE_ID = BaseColumns._ID;

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
