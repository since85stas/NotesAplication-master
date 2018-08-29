package com.batura.stas.notesaplication;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.batura.stas.notesaplication.AlarmFuncs.AlarmSetActivity;
import com.batura.stas.notesaplication.ImageFuncs.DividerItemDecoration;
import com.batura.stas.notesaplication.ImageFuncs.ImageMy;
import com.batura.stas.notesaplication.ImageFuncs.ImageStorage;
import com.batura.stas.notesaplication.ImageFuncs.ImagesAdapter;
import com.batura.stas.notesaplication.Static.NoteUtils;
import com.batura.stas.notesaplication.data.NoteContract;
import com.batura.stas.notesaplication.data.NoteDbHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eltos.simpledialogfragment.color.SimpleColorDialog;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Batura Stas on 18.05.2018.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
        , ImagesAdapter.ImageAdapterListener
        , SimpleColorDialog.OnDialogResultListener{

    public static final String TAG = EditorActivity.class.getSimpleName();

    private final static int NOTE_LOADER_EDITOR = 0;
    private final static int NOTE_LOADER_IMAGES = 11;
    private final static int NOTIFIC_ANSWER = 0;
    private static final int REQUEST_GALLERY = 100; // запрос для галереи
    private final static String COLOR_DIALOG = "colorDilog";

    private EditText mTitleTextView;
    private LinedEditText mBodyTextView;
    private int mColor = 665;
    private long mTime = 0;
    private int mFav   = 0;
    private int mReopenActivity = 0;
    private boolean mNotificIsOn = false;
    private Uri mCurrentNoteUri;
    private Uri mCurrentNoteImagesUri;
    //private Spinner mColorSpinner;
    private ImageSwitcher mImageSwitcher;
    private ShareActionProvider mShareActionProvider ;
    private final int[] mFavImagId = {R.id.imageStarOut,R.id.imageStarFill};
    private boolean mNoteHasChanged = false;
    private ImageView mImageView;
    private TextView  mTargetUriTextView;
    private NoteDbHelper mDbHelper;
    private SQLiteDatabase mImageDb;

    private List<ImageMy> images = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImagesAdapter mImagesAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNoteHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        Log.i(TAG, "onPostResume: ");
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
        mReopenActivity = 1;
        getSupportLoaderManager().destroyLoader(NOTE_LOADER_IMAGES);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_full);

        //getSupportActionBar()
        //        .setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));

        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);
//        bottomNavigation.text
        bottomNavigation.setBackgroundColor(getResources().getColor(R.color.actionBarColor));
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mCurrentNoteUri = getIntent().getData();
        if (mCurrentNoteUri != null) {
            mCurrentNoteImagesUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI_IMAGES,
                    ContentUris.parseId(mCurrentNoteUri));
        }

        if (mCurrentNoteUri == null) {
            setTitle("Add note");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit note");
            getSupportLoaderManager().initLoader(NOTE_LOADER_EDITOR, null, this);

            //инициализируем только при первом открытии активности
            if (mReopenActivity == 0) {
                getSupportLoaderManager().initLoader(NOTE_LOADER_IMAGES, null, this);
            }
        }

        mTitleTextView = (EditText) findViewById(R.id.noteTitleInput);

        mBodyTextView = findViewById(R.id.noteTextInput);
        //mBodyTextView = new LinedEditText(this,null,19);
        //mColorSpinner = (Spinner) findViewById(R.id.colorSpinner);
        mTime = System.currentTimeMillis();
        mImageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcherFav);

        mImageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageSwitcher.showNext();
                int imageId = mImageSwitcher.getCurrentView().getId();
                if ( imageId == mFavImagId[0]) {
                    mFav = 0;
                } if ( imageId == mFavImagId[1]) {
                    mFav = 1;
                }else {
                }
                }

            });

        mTitleTextView.setOnTouchListener(mTouchListener);
        mBodyTextView.setOnTouchListener(mTouchListener);

        //определяем recycle view для фото
        recyclerView = findViewById(R.id.recycler_view);

        mImagesAdapter = new ImagesAdapter(this,images,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(mImagesAdapter);


        mDbHelper = new NoteDbHelper(this);
        mImageDb = mDbHelper.getReadableDatabase();

        LinearLayout linearLayout = findViewById(R.id.layoutEditor);
        linearLayout.requestFocus();
        //displayDatabaseInfo();
//        mTitleTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (hasWindowFocus()) {
//                    recyclerView.setVisibility(View.GONE);
//                }
//                else {
//                    recyclerView.setVisibility(View.VISIBLE);
//                }
//            }
//        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bbn_image:
                            // запускаем Галерею
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQUEST_GALLERY);
                            Toast.makeText(getBaseContext(), "image item", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.bbn_color:
                            Toast.makeText(getBaseContext(), "color item", Toast.LENGTH_SHORT).show();
                            createColorDialog();
                            return true;
                        case R.id.bbn_notific:
                            Intent alarmIntent = new Intent(getBaseContext(), AlarmSetActivity.class);
                            String text = mBodyTextView.getText().toString();
                            alarmIntent.putExtra(AlarmSetActivity.NOTE_BODY,text);
                            startActivityForResult(alarmIntent,NOTIFIC_ANSWER);
                            Toast.makeText(getBaseContext(), "notif item", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.bbn_sett:
                            showDeleteConfirmationDialog();
                            Toast.makeText(getBaseContext(), "sett item", Toast.LENGTH_SHORT).show();
                            return true;
                    }
                    return false;
                }
            };

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteNote();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mNoteHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        //mShareActionProvider =  menuItem.getActionProvider();
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider
                .setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        mShareActionProvider.setShareIntent(createShareIntent());

        //TODO: решить проблему с невозможностью поделиться новой записью

        return true;
    }


    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String msg = mBodyTextView.getText().toString();
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                msg);
        return shareIntent;
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
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mNoteHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_share:
                //Log.v(LOG_TAG,"12121212");
                return true;
         }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap galleryBitmap = null;
        //ImageStorage imageStorage = new ImageStorage();
        //mImageView = findViewById(R.id.imageViewTest1);
        //mTargetUriTextView = findViewById(R.id.textViewTest1);
        if (requestCode == NOTIFIC_ANSWER) {
            if (resultCode == RESULT_OK) {
                mNotificIsOn = data.getBooleanExtra(AlarmSetActivity.NOTIF_IS_ON,false);
            }else {
                //infoTextView.setText(""); // стираем текст
            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                String fileName = null;
                Uri selectedImageUri = data.getData();
                try {
                    galleryBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            selectedImageUri);
                    Cursor cursor = null;
                    cursor = getContentResolver().query(selectedImageUri, new String[]{
                            MediaStore.Images.ImageColumns.DISPLAY_NAME
                    }, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                        Log.d(TAG, "name is " + fileName);
                    }
                    String s = MediaStore.Images.ImageColumns.DISPLAY_NAME;
                    Log.i(TAG, "onActivityResult: "+ s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String result = ImageStorage.saveToSdCard(galleryBitmap,fileName);

                //String del = ImageStorage.deleteFromSd(fileName);
                ImageMy imageMy = new ImageMy(fileName,galleryBitmap);
                images.add(imageMy);
                mImagesAdapter.notifyDataSetChanged();
                //galleryBitmap = ImageStorage.getImageBitmap("firstFile");
                //mImageView.setImageBitmap(galleryBitmap);
                //mTargetUriTextView.setText(selectedImageUri.toString());
            }
        }
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
                TextUtils.isEmpty(colorString) && TextUtils.isEmpty(timeString) && images.isEmpty() ) {
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_TITLE, titleString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_BODY, bodyString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, mColor);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_FAVOURITE, mFav);
        // constant values
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_PASSWORD, 0);

        //вставляем колво изображений
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE, images.size());
        if (mNotificIsOn) {
            values.put(NoteContract.NoteEntry.COLUMN_NOTE_WIDGET, 1);
        } else {
            values.put(NoteContract.NoteEntry.COLUMN_NOTE_WIDGET, 0);
        }
        //
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_TIME, mTime);
        
        //create content for images DB
        ContentValues valuesImages = new ContentValues();
        
        // get all images names in one String
        int numImages = images.size();


            String allImagesNames = "";
            for (int i = 0; i < numImages; i++) {
                String name = images.get(i).getName();
                allImagesNames += name + " ";
            }
            valuesImages.put(NoteContract.NoteEntry.IMAGE_NAME_01,allImagesNames);
            //valuesImages.put(NoteContract.NoteEntry.NOTE_ID,);

        if (mCurrentNoteUri == null) {

            // Insert a new row for note in the database, returning the ID of that new row.
            // Insert a new pet into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, values);

            Uri imagesUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI_IMAGES,
                    ContentUris.parseId(newUri));
            getContentResolver().insert(imagesUri,valuesImages);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null || imagesUri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_note_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);
            Uri imagesUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI_IMAGES,
                    ContentUris.parseId(mCurrentNoteUri));
            getContentResolver().update(imagesUri,valuesImages, null, null);
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


    /**
     * Perform the deletion of the note in the database.
     */
    private void deleteNote() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentNoteUri != null) {

            for (int i = 0; i < images.size() ; i++) {
                String res = ImageStorage.deleteFromSd(images.get(i).getName());
                Log.i(TAG, "deleteNote: " + res + " " + i);
            }

            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentNoteUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == NOTE_LOADER_EDITOR) {
            String[] projection = new String[]{
                    NoteContract.NoteEntry._ID,
                    NoteContract.NoteEntry.COLUMN_NOTE_TITLE,
                    NoteContract.NoteEntry.COLUMN_NOTE_BODY,
                    NoteContract.NoteEntry.COLUMN_NOTE_COLOR,
                    NoteContract.NoteEntry.COLUMN_NOTE_FAVOURITE,
                    NoteContract.NoteEntry.COLUMN_NOTE_TIME
            };
            return new android.support.v4.content.CursorLoader(this,
                    mCurrentNoteUri,
                    projection,
                    null,
                    null,
                    null);
        } else if (id == NOTE_LOADER_IMAGES) {
            String[] projection = new String[]{
                    NoteContract.NoteEntry.IMAGE_NAME_01,
                    NoteContract.NoteEntry.IMAGE_NAME_02,
                    NoteContract.NoteEntry.IMAGE_NAME_03,
                    NoteContract.NoteEntry._ID
            };
            return new android.support.v4.content.CursorLoader(this,
                    mCurrentNoteImagesUri,
                    //null,
                    projection,
                    null,
                    null,
                    null);
        } else {
            return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case (NOTE_LOADER_EDITOR):
                if (cursor.moveToFirst()) {
                    // Find the columns of pet attributes that we're interested in
                    int titleColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
                    int bodyColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_BODY);
                    int colorColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_COLOR);
                    int timeColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TIME);
                    int favColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_FAVOURITE);

                    // Extract out the value from the Cursor for the given column index
                    String title = cursor.getString(titleColumnIndex);
                    String body = cursor.getString(bodyColumnIndex);
                    int colorId = cursor.getInt(colorColumnIndex);
                    int weight = cursor.getInt(timeColumnIndex);
                    mFav = cursor.getInt(favColumnIndex);

                    // Update the views on the screen with the values from the database
                    if (mFav == 1) {
                        mImageSwitcher.showNext();
                    }
                    mTitleTextView.setText(title);
                    mBodyTextView.setText(body);

                    setEditorViewColor(colorId);
                    int colorBackLight = NoteUtils.getBackColorLight(colorId);
                    //mBodyTextView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), colorBackLight));
                    //mWeightEditText.setText(Integer.toString(weight));
                    //mColorSpinner.setSelection(NoteUtils.getColorPosisById(colorId));
                    mColor = colorId;
                    mBodyTextView.setColorId(colorId);
                }
                break;
            case (NOTE_LOADER_IMAGES):
                if (cursor.moveToFirst()) {
                    int image01colomnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.IMAGE_NAME_01);
//                    int image02colomnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.IMAGE_NAME_02);
//                    int image03colomnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.IMAGE_NAME_03);
                    int noteIdColomnIndex = cursor.getColumnIndex(NoteContract.NoteEntry._ID);

                    String imagesNames = cursor.getString(image01colomnIndex);
                    int noteId = cursor.getInt(noteIdColomnIndex);
                    if(mCurrentNoteUri != null) {
                        //загружаем изображения
                        String [] imageNamesMassive = imagesNames.split(" ");
                        if ( imageNamesMassive[0] != "")   {
                            for (String anImageNamesMassive : imageNamesMassive) {
                                ImageMy imageMy = new ImageMy(anImageNamesMassive, ImageStorage.getImageBitmap(anImageNamesMassive));
                                images.add(imageMy);
                            }
                        }
                    }
                    Log.i(TAG, "onLoadFinished: " + cursor.toString());
                }
                break;
        }

    }

    private void setEditorViewColor (int colorId) {
        int colorBackLight = NoteUtils.getBackColorLight(colorId);

        mBodyTextView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), colorBackLight));
        TextView titleText = findViewById(R.id.noteTitleInput);
        titleText.setBackgroundColor(ContextCompat.getColor(getBaseContext(),colorBackLight));
        mImageSwitcher.setBackgroundColor(ContextCompat.getColor(getBaseContext(),colorBackLight));
        recyclerView.setBackgroundColor(ContextCompat.getColor(getBaseContext(),colorBackLight));
        LinearLayout editorLay = findViewById(R.id.layoutEditor);
        editorLay.setBackgroundColor(ContextCompat.getColor(getBaseContext(),colorBackLight));
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
//    private void setupSpinner() {
//        // Create adapter for spinner. The list options are from the String array it will use
//        // the spinner will use the default layout
//
//        int[] colorId = {665,666,667,668,669,670,671};
//        SpinnerColorAdapter myAdapter = new  SpinnerColorAdapter (EditorActivity.this,
//                R.layout.color_dropdown_item , getResources().getStringArray(R.array.color_array_string),colorId);
//
//        // Apply the adapter to the spinner
//        mColorSpinner.setAdapter(myAdapter);
//
//        // Set the integer mSelected to the constant values
//        mColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selection = (String) parent.getItemAtPosition(position);
//                if (!TextUtils.isEmpty(selection)) {
//                    if (selection.equals(getString(R.string.color_default))) {
//                        mColor = 665;
//                    } else if (selection.equals(getString(R.string.color_red))) {
//                        mColor = NoteContract.NoteEntry.COLOR_RED;
//                    } else if (selection.equals(getString(R.string.color_orange))) {
//                        mColor = NoteContract.NoteEntry.COLOR_ORANGE;
//                    } else if (selection.equals(getString(R.string.color_yellow))) {
//                        mColor = NoteContract.NoteEntry.COLOR_YELLOW;
//                    } else if (selection.equals(getString(R.string.color_green))) {
//                        mColor = NoteContract.NoteEntry.COLOR_GREEN;
//                    } else if (selection.equals(getString(R.string.color_blue))) {
//                        mColor = NoteContract.NoteEntry.COLOR_BLUE;
//                    } else if (selection.equals(getString(R.string.color_purple))) {
//                        mColor = NoteContract.NoteEntry.COLOR_PURPLE;
//                    } else {
//                        Log.e(TAG, "Wrong color spinner value");
//                    }
//                }
//            }
//
//            // Because AdapterView is an abstract class, onNothingSelected must be defined
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                mColor = 665; // default
//            }
//        });
//    }

    @Override
    public void onImageClicked(int position) {
        Intent intent = new Intent(EditorActivity.this,BigImageActivity.class);
        intent.putExtra(BigImageActivity.class.getSimpleName(),images.get(position).getName());
        startActivity(intent);

//        intent.putExtra(ImageMy.class.getSimpleName(),byteArray);
//        startActivity(intent);
    }

    @Override
    public void onButtonClicked(int position) {
        String del = ImageStorage.deleteFromSd(images.get(position).getName());
        mImagesAdapter.removeData(position);
        Log.i(TAG, "onButtonClicked: " + del);
        mImagesAdapter.notifyDataSetChanged();
    }

    void createColorDialog() {
        int[] colors = new int[7];
        colors[0] = ContextCompat.getColor(getApplicationContext(),R.color.defaultBackLight);
        colors[5] = ContextCompat.getColor(getApplicationContext(),R.color.blueBackLight);
        colors[3] = ContextCompat.getColor(getApplicationContext(),R.color.yellowBackLight);
        colors[2] = ContextCompat.getColor(getApplicationContext(),R.color.orangeBackLight);
        colors[1] = ContextCompat.getColor(getApplicationContext(),R.color.redBackLight);
        colors[4] = ContextCompat.getColor(getApplicationContext(),R.color.greenBackLight);
        colors[6] = ContextCompat.getColor(getApplicationContext(),R.color.purpleBackLight);
        SimpleColorDialog.build()
                .title(R.string.colorDilogTitle)
                .allowCustom(false)
                .colors(colors)
                .show(this,COLOR_DIALOG);
    }

    public int getColorIdByRcolor (int colorR) {
        int colorId;
        int iColor = 0;
        int[] colors = new int[7];
        colors[0] = ContextCompat.getColor(getApplicationContext(),R.color.defaultBackLight);
        colors[5] = ContextCompat.getColor(getApplicationContext(),R.color.blueBackLight);
        colors[3] = ContextCompat.getColor(getApplicationContext(),R.color.yellowBackLight);
        colors[2] = ContextCompat.getColor(getApplicationContext(),R.color.orangeBackLight);
        colors[1] = ContextCompat.getColor(getApplicationContext(),R.color.redBackLight);
        colors[4] = ContextCompat.getColor(getApplicationContext(),R.color.greenBackLight);
        colors[6] = ContextCompat.getColor(getApplicationContext(),R.color.purpleBackLight);
        for (int i = 0; i <colors.length ; i++) {
            if (colors[i] == colorR) {
                break;
            }
            iColor ++;
        }
        colorId =NoteUtils.getColorIdByPosit(iColor);
        return colorId;
    }

    /**
     * +     * Temporary helper method to display information in the onscreen TextView about the state of
     * +     * the pets database.
     * +
     */
//    private void displayDatabaseInfo() {
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//        // Create and/or open a database to read from it
//
//        // Perform this raw SQL query "SELECT * FROM pets"
//        // to get a Cursor that contains all rows from the pets table.
//        Cursor cursor = mImageDb.rawQuery("SELECT * FROM " + NoteContract.NoteEntry.IMAGE_TABLE_NAME, null);
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
//            TextView displayView = findViewById(R.id.textViewTest1);
//            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
//            displayView.append(NoteContract.NoteEntry.NOTE_ID + " - " +
//                    NoteContract.NoteEntry.IMAGE_NAME_01 + " - " +
//                    NoteContract.NoteEntry.IMAGE_NAME_02 + " - " +
//                    NoteContract.NoteEntry.IMAGE_NAME_03 + " - " +
//                     "\n");
//
//            // Figure out the index of each column
//            int _idColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry._ID);
//            int idColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.NOTE_ID);
//            int image1NameColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.IMAGE_NAME_01);
//            int image2NameColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.IMAGE_NAME_02);
//            int image3NameColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.IMAGE_NAME_03);
//
//            // Iterate through all the returned rows in the cursor
//            while (cursor.moveToNext()) {
//                // Use that  index to extract the String or Int value of the word
//                // at the current row the cursor is on.
//                int current_ID = cursor.getInt(_idColumnIndex);
//                int currentID = cursor.getInt(idColumnIndex);
//                String currentName1 = cursor.getString(image1NameColumnIndex);
//                String currentName2 = cursor.getString(image2NameColumnIndex);
//                String currentName3 = cursor.getString(image3NameColumnIndex);
//                displayView.append(current_ID + " - " +
//                        currentName1 + " - " +
//                        + currentID +
//                        "\n");
//            }
//        } finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//            cursor.close();
//        }
//    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        Log.i("ttt", "onResult: ");
        if (which == BUTTON_POSITIVE) {
            switch (dialogTag) {
                case COLOR_DIALOG:
                    int[] colors = extras.getIntArray(SimpleColorDialog.COLORS);
                    Toast.makeText(this, "ttttttt", Toast.LENGTH_LONG);
                    //setEditorViewColor(colors[0]);

                    if (colors != null) {
                        mColor = getColorIdByRcolor(colors[0]);
                    }
                    Log.i(TAG, "onResult: ");
                    setEditorViewColor(mColor);
            }
        }
        return false;
    }
}
