package com.batura.stas.notesaplication.data;

import android.content.ContentProvider;
import android.content.UriMatcher;
import com.batura.stas.notesaplication.data.ImageContract.ImageEntry;

/**
 * Created by seeyo on 06.08.2018.
 */

public class ImageProvider extends ContentProvider {


    /** Tag for the log messages */
    public static final String LOG = ImageProvider.class.getSimpleName();
    private NoteDbHelper mDbHelper;

    /** URI matcher code for the content URI for the note images in DB table */
    private static final int NOTE_IMAGES = 111;

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
        sUriMatcher.addURI(ImageContract.CONTENT_AUTHORITY,ImageContract.PATH_NOTES,NOTE_IMAGES);
        //sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY,NoteContract.PATH_NOTES + "/#",NOTE_ID);
    }


}
